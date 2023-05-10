package com.itskillsnow.courseservice.dto.request.quizResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResultsDto {
    private UUID quizId;
    private String username;
}
