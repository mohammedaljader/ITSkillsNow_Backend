package com.itskillsnow.courseservice.dto.request.option;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateOptionDto {
    private UUID optionId;
    private String optionName;
    private boolean optionIsCorrect;
}
