package com.itskillsnow.courseservice.dto.request.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuizDto {
    private UUID quizId;
    private String quizName;
}
