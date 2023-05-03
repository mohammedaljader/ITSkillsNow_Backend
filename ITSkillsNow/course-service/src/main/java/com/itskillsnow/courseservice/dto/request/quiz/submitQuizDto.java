package com.itskillsnow.courseservice.dto.request.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class submitQuizDto {
    UUID questionId;
    UUID optionId;
}
