package com.itskillsnow.courseservice.repository;

import com.itskillsnow.courseservice.model.Quiz;
import com.itskillsnow.courseservice.model.QuizUser;
import com.itskillsnow.courseservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizUserRepository extends JpaRepository<QuizUser, UUID> {
    QuizUser findByQuizAndUser(Quiz quiz, User user);

    List<QuizUser> findAllByUser(User user);
}
