package com.itskillsnow.jobservice.controller;


import com.itskillsnow.jobservice.dto.request.jobApplication.AddJobApplication;
import com.itskillsnow.jobservice.dto.response.JobApplicationView;
import com.itskillsnow.jobservice.service.interfaces.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobApplication")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplicationView addJobApplication(@RequestParam MultipartFile applicationCv,
                                     @RequestParam String applicationMotivation,
                                     @RequestParam String username,
                                     @RequestParam String jobId) throws IOException {
        AddJobApplication jobApplication = new AddJobApplication(applicationCv, applicationMotivation,
                username, UUID.fromString(jobId));
        return jobApplicationService.applyForJob(jobApplication);
    }

    @DeleteMapping("/{jobApplicationId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteJobApplication(@PathVariable String jobApplicationId)  {
        return jobApplicationService.deleteJobApplication(UUID.fromString(jobApplicationId));
    }

    @GetMapping("/{jobApplicationId}")
    @ResponseStatus(HttpStatus.OK)
    public JobApplicationView getJobApplicationById(@PathVariable String jobApplicationId)  {
        return jobApplicationService.getJobApplicationById(UUID.fromString(jobApplicationId));
    }

    @GetMapping("/user/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<JobApplicationView> getAllJobApplicationsByUser(@PathVariable String username)  {
        return jobApplicationService.getAllJobApplicationsByUser(username);
    }

    @GetMapping("/job/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    public List<JobApplicationView> getAllJobApplicationsByJob(@PathVariable String jobId)  {
        return jobApplicationService.getAllJobApplicationsByJob(UUID.fromString(jobId));
    }
}
