package com.itskillsnow.courseservice.repository;

import com.itskillsnow.courseservice.model.Question;
import com.itskillsnow.courseservice.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByQuiz(Quiz quiz);
}
