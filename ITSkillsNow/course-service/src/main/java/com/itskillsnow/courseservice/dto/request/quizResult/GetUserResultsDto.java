package com.itskillsnow.courseservice.dto.request.quizResult;

import lombok.Data;

import java.util.UUID;

@Data
public class GetUserResultsDto {
    private UUID quizId;
    private String username;
}
