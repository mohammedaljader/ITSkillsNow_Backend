package com.itskillsnow.courseservice.dto.request.question;

import com.itskillsnow.courseservice.dto.request.option.AddOptionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddQuestionWithOptionDto {
    private AddQuestionDto addQuestionDto;
    private List<AddOptionDto> optionDtoList;
}
