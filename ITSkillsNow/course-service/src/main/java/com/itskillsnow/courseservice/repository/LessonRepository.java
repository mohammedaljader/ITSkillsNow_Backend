package com.itskillsnow.courseservice.repository;

import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findByCourse(Course course);
}
