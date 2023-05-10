package com.itskillsnow.courseservice.dto.request.option;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOptionDto {
    private UUID optionId;
    private String optionName;
    private boolean optionIsCorrect;
}
