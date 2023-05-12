package com.itskillsnow.jobservice.service.interfaces;

import com.itskillsnow.jobservice.dto.request.jobApplication.AddJobApplication;
import com.itskillsnow.jobservice.dto.response.JobApplicationView;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface JobApplicationService {
    JobApplicationView applyForJob(AddJobApplication jobApplication) throws IOException;
    Boolean deleteJobApplication(UUID jobApplicationId);
    JobApplicationView getJobApplicationById(UUID jobApplicationId);
    List<JobApplicationView> getAllJobApplicationsByUser(String username);
    List<JobApplicationView> getAllJobApplicationsByJob(UUID jobId);
}
