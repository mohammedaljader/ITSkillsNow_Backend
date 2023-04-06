package com.itskillsnow.jobservice.service.interfaces;

import com.itskillsnow.jobservice.dto.request.AddJobDto;
import com.itskillsnow.jobservice.dto.request.UpdateJobDto;
import com.itskillsnow.jobservice.dto.response.JobView;

import java.util.List;
import java.util.UUID;

public interface JobService {

    boolean addJob(AddJobDto jobDto);

    boolean updateJob(UpdateJobDto jobDto);

    boolean deleteJob(UUID jobId);

    JobView getJobByJobId(UUID jobId);

    List<JobView> getAllJobs();

    List<JobView> getAllJobsByUserId(String username);
}
