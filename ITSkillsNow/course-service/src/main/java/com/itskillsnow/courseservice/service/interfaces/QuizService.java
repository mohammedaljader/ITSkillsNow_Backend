package com.itskillsnow.courseservice.service.interfaces;

import com.itskillsnow.courseservice.dto.request.quiz.AddQuizDto;
import com.itskillsnow.courseservice.dto.request.quiz.UpdateQuizDto;
import com.itskillsnow.courseservice.dto.request.quiz.submitQuizDto;
import com.itskillsnow.courseservice.dto.response.QuizView;

import java.util.List;
import java.util.UUID;

public interface QuizService {

    boolean addQuiz(AddQuizDto addQuizDto);

    boolean updateQuiz(UpdateQuizDto updateQuizDto);

    boolean deleteQuiz(UUID quizId);

    List<QuizView> getAllQuizzesByCourse(UUID courseId);

    Integer checkQuizResult(UUID quizId, List<submitQuizDto> submitQuizzes);
}
