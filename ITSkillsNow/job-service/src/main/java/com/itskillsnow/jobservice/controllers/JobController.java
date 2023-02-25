package com.itskillsnow.jobservice.controllers;

import com.itskillsnow.jobservice.dto.request.AddJobDto;
import com.itskillsnow.jobservice.dto.response.JobView;
import com.itskillsnow.jobservice.service.interfaces.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addJob(@RequestBody AddJobDto jobDto){
        jobService.addJob(jobDto);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<JobView> getAllJobs(){
        return jobService.getAllJobs();
    }
}
