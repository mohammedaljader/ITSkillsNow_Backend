package com.itskillsnow.courseservice.dto.request.question;

import lombok.Data;

import java.util.UUID;

@Data
public class AddQuestionDto {
    private String questionName;
    private UUID quizId;
}
