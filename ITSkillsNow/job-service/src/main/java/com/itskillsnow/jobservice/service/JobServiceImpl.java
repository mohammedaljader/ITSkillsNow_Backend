package com.itskillsnow.jobservice.service;


import com.itskillsnow.jobservice.dto.request.AddJobDto;
import com.itskillsnow.jobservice.dto.response.JobView;
import com.itskillsnow.jobservice.models.Job;
import com.itskillsnow.jobservice.repository.JobRepository;
import com.itskillsnow.jobservice.service.interfaces.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Override
    public void addJob(AddJobDto jobDto) {
        jobRepository.save(new Job(jobDto.getJobName()));
    }

    @Override
    public List<JobView> getAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(job -> new JobView(job.getJobId(), job.getJobName()))
                .collect(Collectors.toList());
    }
}
