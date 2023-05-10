package com.itskillsnow.courseservice.dto.request.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuestionDto {
    private UUID questionId;
    private String questionName;
}
