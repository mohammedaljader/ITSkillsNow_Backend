package com.itskillsnow.courseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OptionView {
    private UUID optionId;
    private String optionName;
    private boolean optionIsCorrect;
}
