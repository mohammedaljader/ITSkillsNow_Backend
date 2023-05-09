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
public class OptionView {
    private UUID optionId;
    private String optionName;
    private boolean optionIsCorrect;
}
