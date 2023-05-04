package com.itskillsnow.courseservice.service.interfaces;

import com.itskillsnow.courseservice.dto.request.option.AddOptionDto;
import com.itskillsnow.courseservice.dto.request.option.UpdateOptionDto;
import com.itskillsnow.courseservice.dto.request.question.AddQuestionDto;
import com.itskillsnow.courseservice.dto.request.question.UpdateQuestionDto;
import com.itskillsnow.courseservice.dto.response.OptionView;
import com.itskillsnow.courseservice.dto.response.QuestionView;

import java.util.List;
import java.util.UUID;

public interface QuestionService {

    boolean addQuestion(AddQuestionDto addQuestionDto, List<AddOptionDto> optionDtoList);

    boolean addQuestion(AddQuestionDto addQuestionDto);

    boolean updateQuestion(UpdateQuestionDto updateQuestionDto);

    boolean deleteQuestion(UUID questionId);

    boolean addOption(AddOptionDto addOptionDto);

    boolean updateOption(UpdateOptionDto updateOptionDto);

    boolean deleteOption(UUID optionId);

    List<QuestionView> getAllQuestionByQuiz(UUID quizId);

    List<OptionView> getAllOptionsByQuestion(UUID questionId);
}
