package com.itskillsnow.courseservice.controller;

import com.itskillsnow.courseservice.dto.request.enrollment.AddEnrollmentDto;
import com.itskillsnow.courseservice.dto.response.EnrollmentView;
import com.itskillsnow.courseservice.service.interfaces.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean enrollToCourse(@RequestBody AddEnrollmentDto addEnrollmentDto){
        return enrollmentService.enrollToCourse(addEnrollmentDto);
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<EnrollmentView> getAllEnrollments(@PathVariable String username){
        return enrollmentService.getAllEnrollments(username);
    }
}
