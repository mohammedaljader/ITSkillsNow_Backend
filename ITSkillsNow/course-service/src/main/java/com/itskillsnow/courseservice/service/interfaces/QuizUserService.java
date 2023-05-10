package com.itskillsnow.courseservice.service.interfaces;

import com.itskillsnow.courseservice.dto.request.quizResult.UserAnswersDto;
import com.itskillsnow.courseservice.dto.response.QuizResultView;

import java.util.List;
import java.util.UUID;

public interface QuizUserService {

    QuizResultView submitQuiz(UUID quizId, String username, List<UserAnswersDto> userAnswers);

    QuizResultView getQuizResultByUser(UUID quizId, String username);

    List<QuizResultView> getAllQuizzesResultByUser(String username);
}
