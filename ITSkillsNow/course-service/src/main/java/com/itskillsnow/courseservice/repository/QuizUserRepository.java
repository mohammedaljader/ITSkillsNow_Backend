package com.itskillsnow.courseservice.repository;

import com.itskillsnow.courseservice.model.QuizUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizUserRepository extends JpaRepository<QuizUser, UUID> {
}
