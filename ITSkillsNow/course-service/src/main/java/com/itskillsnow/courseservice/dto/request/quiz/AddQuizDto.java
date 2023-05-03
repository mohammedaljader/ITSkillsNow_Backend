package com.itskillsnow.courseservice.dto.request.quiz;

import lombok.Data;

import java.util.UUID;

@Data
public class AddQuizDto {
    private String quizName;
    private UUID courseId;
}
