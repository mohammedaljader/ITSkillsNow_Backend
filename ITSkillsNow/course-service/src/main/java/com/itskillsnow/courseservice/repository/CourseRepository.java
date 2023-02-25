package com.itskillsnow.courseservice.repository;


import com.itskillsnow.courseservice.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
}
