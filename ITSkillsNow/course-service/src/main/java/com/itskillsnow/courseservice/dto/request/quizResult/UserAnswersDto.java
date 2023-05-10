package com.itskillsnow.courseservice.dto.request.quizResult;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserAnswersDto {
    UUID questionId;
    UUID optionId;
}
