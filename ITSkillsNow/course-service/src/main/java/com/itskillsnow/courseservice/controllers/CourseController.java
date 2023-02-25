package com.itskillsnow.courseservice.controllers;

import com.itskillsnow.courseservice.dto.request.AddCourseDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.service.interfaces.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean addCourse(@RequestBody AddCourseDto addCourseDto){
        return courseService.addCourse(addCourseDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CourseView> getAllCourses(){
        return courseService.getAllCourses();
    }

}
