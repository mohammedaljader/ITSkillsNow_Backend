package com.itskillsnow.jobservice.service;


import com.itskillsnow.jobservice.dto.request.job.AddJobDto;
import com.itskillsnow.jobservice.dto.request.job.AddJobWithFileDto;
import com.itskillsnow.jobservice.dto.request.job.UpdateJobDto;
import com.itskillsnow.jobservice.dto.request.job.UpdateJobWithFileDto;
import com.itskillsnow.jobservice.dto.response.JobView;
import com.itskillsnow.jobservice.exception.GeneralException;
import com.itskillsnow.jobservice.exception.JobNotFoundException;
import com.itskillsnow.jobservice.exception.UserNotFoundException;
import com.itskillsnow.jobservice.model.Job;
import com.itskillsnow.jobservice.model.User;
import com.itskillsnow.jobservice.repository.JobRepository;
import com.itskillsnow.jobservice.repository.UserRepository;
import com.itskillsnow.jobservice.service.interfaces.BlobService;
import com.itskillsnow.jobservice.service.interfaces.JobService;
import com.itskillsnow.jobservice.util.FileNamingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    private final UserRepository userRepository;

    private final BlobService blobService;

    @Override
    public boolean addJob(AddJobDto jobDto) {
        Optional<User> user = userRepository.findByUsername(jobDto.getUsername());
        if(user.isEmpty()){
            return false;
        }
        Job job = mapAddJobDtoToModel(jobDto, user.get());
        jobRepository.save(job);
        return true;
    }

    @Override
    public JobView addJob(AddJobWithFileDto jobDto) throws IOException {
        User user = userRepository.findByUsername(jobDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        if(jobDto.getJobImage().isEmpty()){
            throw new GeneralException("Image is empty!");
        }

        String jobImage = blobService.storeFile(jobDto.getJobImage().getOriginalFilename(),
                jobDto.getJobImage().getInputStream(),
                jobDto.getJobImage().getSize());

        Job newJob = mapAddJobDtoToModel(jobDto, user, jobImage);
        Job savedJob = jobRepository.save(newJob);
        return mapModelToDto(savedJob);

    }

    @Override
    public boolean updateJob(UpdateJobDto jobDto) {
        Optional<Job> job = jobRepository.findById(jobDto.getJobId());

        if(job.isEmpty()){
            return false;
        }
        Job updatedJob = mapUpdateJobDtoToModel(jobDto, job.get().getUser());
        jobRepository.save(updatedJob);
        return true;
    }

    @Override
    public JobView updateJob(UpdateJobWithFileDto jobDto) throws IOException {
        Job job = jobRepository.findById(jobDto.getJobId())
                .orElseThrow(() -> new JobNotFoundException("Job was not found!"));

        String jobImage = jobDto.getJobImage().getOriginalFilename();
        String originalImage = FileNamingUtils.getOriginalFilename(job.getJobImage());

        if(!Objects.equals(jobDto.getJobImage().getOriginalFilename(), "") &&
                !Objects.equals(jobImage, originalImage)){
            jobImage = blobService.storeFile(jobDto.getJobImage().getOriginalFilename(),
                    jobDto.getJobImage().getInputStream(),
                    jobDto.getJobImage().getSize());
            //delete the old image from blob storage
            String blobFileName = FileNamingUtils.getBlobFilename(job.getJobImage());
            blobService.deleteFile(blobFileName);
        }else {
            jobImage = job.getJobImage();
        }

        Job updatedJob = mapUpdateJobDtoToModel(job, jobDto, jobImage);
        Job savedJob = jobRepository.save(updatedJob);
        return mapModelToDto(savedJob);
    }

    @Override
    public boolean deleteJob(UUID jobId) {
        Optional<Job> job = jobRepository.findById(jobId);
        if(job.isEmpty()){
            return false;
        }

        //delete the image from blob storage
        String blobFileName = FileNamingUtils.getBlobFilename(job.get().getJobImage());
        blobService.deleteFile(blobFileName);
        jobRepository.delete(job.get());

        return true;
    }

    @Override
    public JobView getJobByJobId(UUID jobId) {
        Optional<Job> job = jobRepository.findById(jobId);
        if(job.isEmpty()){
            return null;
        }
        return mapModelToDto(job.get());
    }

    @Override
    public List<JobView> getAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(this::mapModelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobView> getAllJobsByUserId(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new UserNotFoundException("User was not found!");
        }
        List<Job> users = jobRepository.findAllByUser(user.get());

        return users.stream()
                .map(this::mapModelToDto)
                .collect(Collectors.toList());
    }


    private JobView mapModelToDto(Job job){
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

    private Job mapAddJobDtoToModel(AddJobWithFileDto jobDto, User user, String jobImage){
        return Job.builder()
                .jobName(jobDto.getJobName())
                .jobDescription(jobDto.getJobDescription())
                .jobImage(jobImage)
                .jobAddress(jobDto.getJobAddress())
                .jobCategory(jobDto.getJobCategory())
                .jobEducationLevel(jobDto.getJobEducationLevel())
                .jobType(jobDto.getJobType())
                .jobHours(jobDto.getJobHours())
                .user(user)
                .build();
    }


    private Job mapAddJobDtoToModel(AddJobDto jobDto, User user){
        return Job.builder()
                .jobName(jobDto.getJobName())
                .jobDescription(jobDto.getJobDescription())
                .jobAddress(jobDto.getJobAddress())
                .jobCategory(jobDto.getJobCategory())
                .jobEducationLevel(jobDto.getJobEducationLevel())
                .jobType(jobDto.getJobType())
                .jobHours(jobDto.getJobHours())
                .user(user)
                .build();
    }

    private Job mapUpdateJobDtoToModel(UpdateJobDto jobDto, User user){
        return Job.builder()
                .jobId(jobDto.getJobId())
                .jobName(jobDto.getJobName())
                .jobDescription(jobDto.getJobDescription())
                .jobImage(jobDto.getJobImage())
                .jobAddress(jobDto.getJobAddress())
                .jobCategory(jobDto.getJobCategory())
                .jobEducationLevel(jobDto.getJobEducationLevel())
                .jobType(jobDto.getJobType())
                .jobHours(jobDto.getJobHours())
                .user(user)
                .build();
    }

    private Job mapUpdateJobDtoToModel(Job job,UpdateJobWithFileDto jobDto, String jobImage){
        job.setJobName(jobDto.getJobName());
        job.setJobDescription(job.getJobDescription());
        job.setJobImage(jobImage);
        job.setJobAddress(jobDto.getJobAddress());
        job.setJobCategory(jobDto.getJobCategory());
        job.setJobEducationLevel(jobDto.getJobEducationLevel());
        job.setJobType(jobDto.getJobType());
        job.setJobHours(jobDto.getJobHours());
        return job;
    }
}
