package com.itskillsnow.courseservice.service.interfaces;

import com.itskillsnow.courseservice.dto.request.option.AddOptionDto;
import com.itskillsnow.courseservice.dto.request.option.UpdateOptionDto;
import com.itskillsnow.courseservice.dto.request.question.AddQuestionDto;
import com.itskillsnow.courseservice.dto.request.question.UpdateQuestionDto;
import com.itskillsnow.courseservice.dto.response.OptionView;
import com.itskillsnow.courseservice.dto.response.QuestionView;
import com.itskillsnow.courseservice.dto.response.QuestionWithoutOptionView;

import java.util.List;
import java.util.UUID;

public interface QuestionService {

    boolean addQuestion(AddQuestionDto addQuestionDto, List<AddOptionDto> optionDtoList);

    QuestionWithoutOptionView addQuestion(AddQuestionDto addQuestionDto);

    QuestionWithoutOptionView updateQuestion(UpdateQuestionDto updateQuestionDto);

    boolean deleteQuestion(UUID questionId);

    OptionView addOption(AddOptionDto addOptionDto);

    OptionView updateOption(UpdateOptionDto updateOptionDto);

    boolean deleteOption(UUID optionId);

    List<QuestionView> getAllQuestionByQuiz(UUID quizId);

    List<OptionView> getAllOptionsByQuestion(UUID questionId);
}
