package com.itskillsnow.courseservice.dto.request.question;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateQuestionDto {
    private UUID questionId;
    private String questionName;
}
