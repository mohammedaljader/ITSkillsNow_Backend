package com.itskillsnow.courseservice.controller;

import com.itskillsnow.courseservice.dto.request.course.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.course.AddCourseWithFileDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseWithFileDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.service.interfaces.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;


    @PostMapping("/withoutImage")
    @ResponseStatus(HttpStatus.CREATED)
    public CourseView addCourse(@RequestBody AddCourseDto addCourseDto){
        return courseService.addCourse(addCourseDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseView addCourseWithImage(@RequestParam String courseName,
                                         @RequestParam String courseDescription,
                                         @RequestParam("courseImage") MultipartFile courseImage,
                                         @RequestParam Double coursePrice,
                                         @RequestParam String courseType,
                                         @RequestParam String courseLanguage,
                                         @RequestParam Boolean isPublished,
                                         @RequestParam String username) throws IOException {

        AddCourseWithFileDto addCourseWithFileDto = new AddCourseWithFileDto(courseName, courseDescription,
                courseImage, coursePrice, courseType,
                courseLanguage, isPublished, username);
        return courseService.addCourse(addCourseWithFileDto);
    }

    @PutMapping("/withoutImage")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CourseView updateCourse(@RequestBody UpdateCourseDto updateCourseDto){
        return courseService.updateCourse(updateCourseDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CourseView updateCourseWithImage(@RequestParam UUID courseId,
                                            @RequestParam String courseName,
                                            @RequestParam String courseDescription,
                                            @RequestParam("courseImage") MultipartFile courseImage,
                                            @RequestParam Double coursePrice,
                                            @RequestParam String courseType,
                                            @RequestParam String courseLanguage,
                                            @RequestParam Boolean isPublished,
                                            @RequestParam String username) throws IOException {
        UpdateCourseWithFileDto updateCourseWithFileDto = new UpdateCourseWithFileDto(courseId, courseName, courseDescription,
                courseImage, coursePrice, courseType,
                courseLanguage, isPublished, username);
        return courseService.updateCourse(updateCourseWithFileDto);
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

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<CourseView> filterCourses(
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) String courseLanguage,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        return courseService.filterCourses(courseName, courseType, courseLanguage, minPrice, maxPrice);
    }
}
