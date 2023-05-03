package com.itskillsnow.courseservice.service.interfaces;

import com.itskillsnow.courseservice.dto.request.enrollment.AddEnrollmentDto;
import com.itskillsnow.courseservice.dto.response.CourseView;

import java.util.List;

public interface EnrollmentService {
    boolean enrollToCourse(AddEnrollmentDto addEnrollmentDto);
    List<CourseView> getAllEnrollments(String username);
}
