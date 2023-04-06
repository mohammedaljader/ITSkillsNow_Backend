package com.itskillsnow.courseservice.repository;


import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findAllByUser(User user);
}
