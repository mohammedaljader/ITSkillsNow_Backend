package com.itskillsnow.courseservice.service.interfaces;

import com.itskillsnow.courseservice.dto.request.enrollment.AddEnrollmentDto;
import com.itskillsnow.courseservice.dto.response.EnrollmentView;

import java.util.List;

public interface EnrollmentService {
    boolean enrollToCourse(AddEnrollmentDto addEnrollmentDto);
    List<EnrollmentView> getAllEnrollments(String username);
}
