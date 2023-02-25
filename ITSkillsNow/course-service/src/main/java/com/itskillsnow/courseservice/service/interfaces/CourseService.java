package com.itskillsnow.courseservice.service.interfaces;

import com.itskillsnow.courseservice.dto.request.AddCourseDto;
import com.itskillsnow.courseservice.dto.response.CourseView;

import java.util.List;

public interface CourseService {
    Boolean addCourse(AddCourseDto courseDto);
    List<CourseView> getAllCourses();
}
