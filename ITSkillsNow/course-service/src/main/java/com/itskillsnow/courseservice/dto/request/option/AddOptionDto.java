package com.itskillsnow.courseservice.dto.request.option;


import lombok.Data;

import java.util.UUID;

@Data
public class AddOptionDto {
    private String OptionName;
    private boolean OptionIsCorrect;
    private UUID questionId;
}
