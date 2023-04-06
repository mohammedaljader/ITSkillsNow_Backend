package com.itskillsnow.jobservice.service;


import com.itskillsnow.jobservice.dto.request.AddJobDto;
import com.itskillsnow.jobservice.dto.request.UpdateJobDto;
import com.itskillsnow.jobservice.dto.response.JobView;
import com.itskillsnow.jobservice.exception.UserNotFoundException;
import com.itskillsnow.jobservice.model.Job;
import com.itskillsnow.jobservice.model.User;
import com.itskillsnow.jobservice.repository.JobRepository;
import com.itskillsnow.jobservice.repository.UserRepository;
import com.itskillsnow.jobservice.service.interfaces.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    private final UserRepository userRepository;

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
    public boolean updateJob(UpdateJobDto jobDto) {
        Optional<User> user = userRepository.findByUsername(jobDto.getUsername());

        if(user.isEmpty()){
            return false;
        }
        Job job = mapUpdateJobDtoToModel(jobDto, user.get());
        jobRepository.save(job);
        return true;
    }

    @Override
    public boolean deleteJob(UUID jobId) {
        Optional<Job> job = jobRepository.findById(jobId);
        if(job.isEmpty()){
            return false;
        }
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
            throw new UserNotFoundException("User Not Found");
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
                .jobAddress(job.getJobAddress())
                .jobCategory(job.getJobCategory())
                .jobEducationLevel(job.getJobEducationLevel())
                .jobType(job.getJobType())
                .jobHours(job.getJobHours())
                .username(job.getUser().getUsername())
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
                .jobAddress(jobDto.getJobAddress())
                .jobCategory(jobDto.getJobCategory())
                .jobEducationLevel(jobDto.getJobEducationLevel())
                .jobType(jobDto.getJobType())
                .jobHours(jobDto.getJobHours())
                .user(user)
                .build();
    }
}
