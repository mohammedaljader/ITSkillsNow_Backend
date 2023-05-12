package com.itskillsnow.jobservice.service.interfaces;

import com.itskillsnow.jobservice.dto.request.job.AddJobDto;
import com.itskillsnow.jobservice.dto.request.job.AddJobWithFileDto;
import com.itskillsnow.jobservice.dto.request.job.UpdateJobDto;
import com.itskillsnow.jobservice.dto.request.job.UpdateJobWithFileDto;
import com.itskillsnow.jobservice.dto.response.JobView;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface JobService {

    boolean addJob(AddJobDto jobDto);

    JobView addJob(AddJobWithFileDto jobDto) throws IOException;

    boolean updateJob(UpdateJobDto jobDto);

    JobView updateJob(UpdateJobWithFileDto jobDto) throws IOException;

    boolean deleteJob(UUID jobId);

    JobView getJobByJobId(UUID jobId);

    List<JobView> getAllJobs();

    List<JobView> getAllJobsByUserId(String username);
}
