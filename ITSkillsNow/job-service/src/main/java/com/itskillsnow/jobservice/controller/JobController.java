package com.itskillsnow.jobservice.controller;

import com.itskillsnow.jobservice.dto.request.AddJobDto;
import com.itskillsnow.jobservice.dto.request.UpdateJobDto;
import com.itskillsnow.jobservice.dto.response.JobView;
import com.itskillsnow.jobservice.service.interfaces.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public boolean addJob(@RequestBody AddJobDto jobDto){
        return jobService.addJob(jobDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean updateJob(@RequestBody UpdateJobDto jobDto){
        return jobService.updateJob(jobDto);
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
