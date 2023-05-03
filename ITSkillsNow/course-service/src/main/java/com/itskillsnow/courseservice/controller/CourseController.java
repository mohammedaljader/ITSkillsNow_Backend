package com.itskillsnow.courseservice.controller;

import com.itskillsnow.courseservice.dto.request.course.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.service.interfaces.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Boolean updateCourse(@RequestBody UpdateCourseDto updateCourseDto){
        return courseService.updateCourse(updateCourseDto);
    }

    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Boolean deleteCourse(@PathVariable UUID courseId){
        return courseService.deleteCourse(courseId);
    }

    @GetMapping("/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public CourseView getCourseById(@PathVariable String courseId){
        return courseService.getCourseById(UUID.fromString(courseId));
    }

    @GetMapping("/user/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<CourseView> getCourseByUser(@PathVariable String username){
        return courseService.getAllCoursesByUser(username);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CourseView> getAllCourses(){
        return courseService.getAllCourses();
    }

}
