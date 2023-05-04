package com.itskillsnow.courseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class QuizView {
    UUID quizId;
    String quizName;
    List<QuestionView> questions;
}
