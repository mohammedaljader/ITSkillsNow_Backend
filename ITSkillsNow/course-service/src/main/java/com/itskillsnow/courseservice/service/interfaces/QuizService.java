package com.itskillsnow.courseservice.service.interfaces;

import com.itskillsnow.courseservice.dto.request.quiz.AddQuizDto;
import com.itskillsnow.courseservice.dto.request.quiz.UpdateQuizDto;
import com.itskillsnow.courseservice.dto.response.QuizView;
import com.itskillsnow.courseservice.dto.response.QuizWithoutQuestionView;

import java.util.List;
import java.util.UUID;

public interface QuizService {

    QuizWithoutQuestionView addQuiz(AddQuizDto addQuizDto);

    QuizWithoutQuestionView updateQuiz(UpdateQuizDto updateQuizDto);

    boolean deleteQuiz(UUID quizId);

    List<QuizView> getAllQuizzesByCourse(UUID courseId);

    List<QuizWithoutQuestionView> getAllQuizzesWithoutQuestionsByCourse(UUID courseId);
}
