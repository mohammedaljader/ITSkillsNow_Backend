package com.itskillsnow.courseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizResultView {
    private UUID quizUserId;
    private String username;
    private QuizView quiz;
    private Integer result;
    private Integer questionsSize;
    private Integer grade;
}
