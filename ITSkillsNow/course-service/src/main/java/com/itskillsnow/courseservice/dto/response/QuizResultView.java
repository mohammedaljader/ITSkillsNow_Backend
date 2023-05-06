package com.itskillsnow.courseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class QuizResultView {
    private UUID quizUserId;
    private String username;
    private QuizView quiz;
    private Integer result;
}
