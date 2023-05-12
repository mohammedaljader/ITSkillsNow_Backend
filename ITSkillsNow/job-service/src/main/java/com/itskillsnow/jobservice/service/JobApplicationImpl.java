package com.itskillsnow.jobservice.service;

import com.itskillsnow.jobservice.dto.request.jobApplication.AddJobApplication;
import com.itskillsnow.jobservice.dto.response.JobApplicationView;
import com.itskillsnow.jobservice.dto.response.JobView;
import com.itskillsnow.jobservice.exception.JobApplicationNotFoundException;
import com.itskillsnow.jobservice.exception.JobNotFoundException;
import com.itskillsnow.jobservice.exception.UserNotFoundException;
import com.itskillsnow.jobservice.model.Job;
import com.itskillsnow.jobservice.model.JobApplication;
import com.itskillsnow.jobservice.model.User;
import com.itskillsnow.jobservice.repository.JobApplicationRepository;
import com.itskillsnow.jobservice.repository.JobRepository;
import com.itskillsnow.jobservice.repository.UserRepository;
import com.itskillsnow.jobservice.service.interfaces.BlobService;
import com.itskillsnow.jobservice.service.interfaces.JobApplicationService;
import com.itskillsnow.jobservice.util.FileNamingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobApplicationImpl implements JobApplicationService {

    private final UserRepository userRepository;

    private final JobRepository jobRepository;

    private final JobApplicationRepository jobApplicationRepository;

    private final BlobService blobService;


    @Override
    public JobApplicationView applyForJob(AddJobApplication jobApplicationDto) throws IOException {
        Job job = jobRepository.findById(jobApplicationDto.getJobId())
                .orElseThrow(() -> new JobNotFoundException("Job was not found!"));

        User user = userRepository.findByUsername(jobApplicationDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        String applicationCv = blobService.storeFile(jobApplicationDto.getApplicationCv().getOriginalFilename(),
                jobApplicationDto.getApplicationCv().getInputStream(), jobApplicationDto.getApplicationCv().getSize());

        JobApplication jobApplication = mapAddJobApplicationDtoToModel(jobApplicationDto, job, user, applicationCv);
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);
        return mapJobApplicationModelToDto(savedJobApplication);
    }

    @Override
    public Boolean deleteJobApplication(UUID jobApplicationId) {
        JobApplication jobApplication = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new JobApplicationNotFoundException("Job Application was not found!"));

        //delete the cv from blob storage
        String blobFileName = FileNamingUtils.getBlobFilename(jobApplication.getApplicationCv());
        blobService.deleteFile(blobFileName);
        jobApplicationRepository.delete(jobApplication);

        return true;
    }

    @Override
    public JobApplicationView getJobApplicationById(UUID jobApplicationId) {
        JobApplication jobApplication = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new JobApplicationNotFoundException("Job Application was not found!"));
        return mapJobApplicationModelToDto(jobApplication);
    }

    @Override
    public List<JobApplicationView> getAllJobApplicationsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        List<JobApplication> jobApplications = jobApplicationRepository.findAllByUser(user);
        return jobApplications.stream()
                .map(this::mapJobApplicationModelToDto)
                .toList();
    }

    @Override
    public List<JobApplicationView> getAllJobApplicationsByJob(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job was not found!"));
        List<JobApplication> jobApplications = jobApplicationRepository.findAllByJob(job);
        return jobApplications.stream()
                .map(this::mapJobApplicationModelToDto)
                .toList();
    }

    private JobApplication mapAddJobApplicationDtoToModel(AddJobApplication jobApplication, Job job, User user,
                                                          String applicationCv){
        return JobApplication.builder()
                .applicationCv(applicationCv)
                .applicationMotivation(jobApplication.getApplicationMotivation())
                .applicationDate(LocalDate.now())
                .applicationTime(LocalTime.now())
                .user(user)
                .job(job)
                .build();
    }

    private JobApplicationView mapJobApplicationModelToDto(JobApplication jobApplication){

        JobView jobView = mapJobModelToDto(jobApplication.getJob());

        return JobApplicationView.builder()
                .applicationId(jobApplication.getApplicationId())
                .applicationCv(jobApplication.getApplicationCv())
                .applicationMotivation(jobApplication.getApplicationMotivation())
                .applicationDate(jobApplication.getApplicationDate())
                .applicationTime(jobApplication.getApplicationTime())
                .jobView(jobView)
                .build();
    }

    private JobView mapJobModelToDto(Job job){
        return JobView.builder()
                .jobId(job.getJobId())
                .jobName(job.getJobName())
                .jobDescription(job.getJobDescription())
                .jobImage(job.getJobImage())
                .jobAddress(job.getJobAddress())
                .jobCategory(job.getJobCategory())
                .jobEducationLevel(job.getJobEducationLevel())
                .jobType(job.getJobType())
                .jobHours(job.getJobHours())
                .username(job.getUser().getUsername())
                .build();
    }
}
