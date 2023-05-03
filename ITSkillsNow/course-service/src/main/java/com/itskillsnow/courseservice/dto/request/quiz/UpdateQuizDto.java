package com.itskillsnow.courseservice.dto.request.quiz;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateQuizDto {
    private UUID quizId;
    private String quizName;
}
