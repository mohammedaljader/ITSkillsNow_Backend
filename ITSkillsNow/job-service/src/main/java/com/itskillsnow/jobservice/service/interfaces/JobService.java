package com.itskillsnow.jobservice.service.interfaces;

import com.itskillsnow.jobservice.dto.request.AddJobDto;
import com.itskillsnow.jobservice.dto.response.JobView;

import java.util.List;

public interface JobService {

    void addJob(AddJobDto jobDto);

    List<JobView> getAllJobs();
}
