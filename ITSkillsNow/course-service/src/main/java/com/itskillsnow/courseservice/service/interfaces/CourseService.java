package com.itskillsnow.courseservice.service.interfaces;

import com.itskillsnow.courseservice.dto.request.course.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseDto;
import com.itskillsnow.courseservice.dto.response.CourseView;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    Boolean addCourse(AddCourseDto courseDto);

    Boolean updateCourse(UpdateCourseDto courseDto);

    Boolean deleteCourse(UUID courseId);

    CourseView getCourseById(UUID courseId);

    List<CourseView> getAllCoursesByUser(String username);

    List<CourseView> getAllCourses();
}
