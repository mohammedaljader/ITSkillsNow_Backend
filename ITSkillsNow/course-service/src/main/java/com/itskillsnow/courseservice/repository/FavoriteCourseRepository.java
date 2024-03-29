package com.itskillsnow.courseservice.repository;

import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.FavoriteCourse;
import com.itskillsnow.courseservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FavoriteCourseRepository extends JpaRepository<FavoriteCourse, UUID> {
    List<FavoriteCourse> findAllByUser(User user);
    boolean existsByCourse(Course course);
}
