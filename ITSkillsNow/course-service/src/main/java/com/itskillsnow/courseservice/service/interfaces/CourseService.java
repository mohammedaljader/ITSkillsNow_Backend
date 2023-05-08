package com.itskillsnow.courseservice.service.interfaces;

import com.itskillsnow.courseservice.dto.request.course.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.course.AddCourseWithFileDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseWithFileDto;
import com.itskillsnow.courseservice.dto.response.CourseView;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface CourseService {
    CourseView addCourse(AddCourseDto courseDto);

    CourseView addCourse(AddCourseWithFileDto courseDto) throws IOException;

    CourseView updateCourse(UpdateCourseDto courseDto);

    CourseView updateCourse(UpdateCourseWithFileDto courseDto) throws IOException;

    Boolean deleteCourse(UUID courseId);

    CourseView getCourseById(UUID courseId);

    List<CourseView> getAllCoursesByUser(String username);

    List<CourseView> getAllCourses();
}
