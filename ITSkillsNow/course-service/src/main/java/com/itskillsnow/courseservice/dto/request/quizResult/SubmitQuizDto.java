package com.itskillsnow.courseservice.dto.request.quizResult;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SubmitQuizDto {
    private UUID quizId;
    private String username;
    private List<UserAnswersDto> userAnswers;
}
