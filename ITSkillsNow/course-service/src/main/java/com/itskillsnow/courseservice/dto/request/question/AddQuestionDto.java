package com.itskillsnow.courseservice.dto.request.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddQuestionDto {
    private String questionName;
    private UUID quizId;
}
