package com.itskillsnow.jobservice.controller;

import com.itskillsnow.jobservice.dto.request.job.AddJobDto;
import com.itskillsnow.jobservice.dto.request.job.AddJobWithFileDto;
import com.itskillsnow.jobservice.dto.request.job.UpdateJobDto;
import com.itskillsnow.jobservice.dto.request.job.UpdateJobWithFileDto;
import com.itskillsnow.jobservice.dto.response.JobView;
import com.itskillsnow.jobservice.service.interfaces.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;


    @PostMapping("/withoutImage")
    @ResponseStatus(HttpStatus.CREATED)
    public boolean addJob(@RequestBody AddJobDto jobDto){
        return jobService.addJob(jobDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobView addJobWithImage(@RequestParam String jobName,
                                         @RequestParam String jobDescription,
                                         @RequestParam("jobImage") MultipartFile jobImage,
                                         @RequestParam String jobAddress,
                                         @RequestParam String jobCategory,
                                         @RequestParam String jobEducationLevel,
                                         @RequestParam String jobType,
                                         @RequestParam Integer jobHours,
                                         @RequestParam String username) throws IOException {

        AddJobWithFileDto addJobWithFileDto = new AddJobWithFileDto(jobName, jobDescription,
                jobImage, jobAddress, jobCategory,
                jobEducationLevel, jobType, jobHours, username);
        return jobService.addJob(addJobWithFileDto);
    }

    @PutMapping("/withoutImage")
    @ResponseStatus(HttpStatus.OK)
    public boolean updateJob(@RequestBody UpdateJobDto jobDto){
        return jobService.updateJob(jobDto);
    }


    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public JobView updateJobWithImage(@RequestParam UUID jobId,
                                      @RequestParam String jobName,
                                      @RequestParam String jobDescription,
                                      @RequestParam("jobImage") MultipartFile jobImage,
                                      @RequestParam String jobAddress,
                                      @RequestParam String jobCategory,
                                      @RequestParam String jobEducationLevel,
                                      @RequestParam String jobType,
                                      @RequestParam Integer jobHours,
                                      @RequestParam String username) throws IOException {
        UpdateJobWithFileDto updateJobWithFileDto = new UpdateJobWithFileDto(jobId, jobName, jobDescription,
                jobImage, jobAddress, jobCategory,
                jobEducationLevel, jobType, jobHours , username);
        return jobService.updateJob(updateJobWithFileDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<JobView> getAllJobs(){
        return jobService.getAllJobs();
    }

    @GetMapping("/user/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<JobView> getJobByUserId(@PathVariable String username){
        return jobService.getAllJobsByUserId(username);
    }


    @GetMapping("/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    public JobView getJobById(@PathVariable String jobId){
        return jobService.getJobByJobId(UUID.fromString(jobId));
    }

    @DeleteMapping("/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteJob(@PathVariable String jobId){
        return jobService.deleteJob(UUID.fromString(jobId));
    }
}
