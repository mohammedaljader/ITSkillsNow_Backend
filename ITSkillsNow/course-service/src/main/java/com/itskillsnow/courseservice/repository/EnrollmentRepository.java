package com.itskillsnow.courseservice.repository;

import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.Enrollment;
import com.itskillsnow.courseservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    List<Enrollment> findAllByUser(User user);
    boolean existsByCourse(Course course);
}
