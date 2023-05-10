package com.itskillsnow.courseservice.repository;

import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findByCourse(Course course);
}
