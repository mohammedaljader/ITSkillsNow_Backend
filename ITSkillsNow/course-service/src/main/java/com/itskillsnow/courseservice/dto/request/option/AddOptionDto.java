package com.itskillsnow.courseservice.dto.request.option;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddOptionDto {
    private String OptionName;
    private boolean OptionIsCorrect;
    private UUID questionId;
}
