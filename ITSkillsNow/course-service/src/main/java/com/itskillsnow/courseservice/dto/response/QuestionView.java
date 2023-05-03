package com.itskillsnow.courseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class QuestionView {
    private UUID questionId;
    private String questionName;
    private List<OptionView> questionOptions;
}
