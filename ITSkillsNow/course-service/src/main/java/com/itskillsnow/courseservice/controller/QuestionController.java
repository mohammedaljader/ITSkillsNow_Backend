package com.itskillsnow.courseservice.controller;

import com.itskillsnow.courseservice.dto.request.option.AddOptionDto;
import com.itskillsnow.courseservice.dto.request.option.UpdateOptionDto;
import com.itskillsnow.courseservice.dto.request.question.AddQuestionDto;
import com.itskillsnow.courseservice.dto.request.question.AddQuestionWithOptionDto;
import com.itskillsnow.courseservice.dto.request.question.UpdateQuestionDto;
import com.itskillsnow.courseservice.dto.response.OptionView;
import com.itskillsnow.courseservice.dto.response.QuestionView;
import com.itskillsnow.courseservice.dto.response.QuestionWithoutOptionView;
import com.itskillsnow.courseservice.service.interfaces.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course/quiz/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean addQuestionWithOptions(@RequestBody AddQuestionWithOptionDto addQuestionWithOptionDto){
        return questionService.addQuestion(addQuestionWithOptionDto.getAddQuestionDto(),
                addQuestionWithOptionDto.getOptionDtoList());
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionWithoutOptionView addQuestion(@RequestBody AddQuestionDto addQuestionDto){
        return questionService.addQuestion(addQuestionDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public QuestionWithoutOptionView updateQuestion(@RequestBody UpdateQuestionDto updateQuestionDto){
        return questionService.updateQuestion(updateQuestionDto);
    }

    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteQuestion(@PathVariable String questionId){
        return questionService.deleteQuestion(UUID.fromString(questionId));
    }

    @GetMapping("/{quizId}")
    @ResponseStatus(HttpStatus.OK)
    public List<QuestionView> getAllQuestionByQuiz(@PathVariable String quizId){
        return questionService.getAllQuestionByQuiz(UUID.fromString(quizId));
    }

    @PostMapping("/option")
    @ResponseStatus(HttpStatus.CREATED)
    public OptionView addOption(@RequestBody AddOptionDto addOptionDto){
        return questionService.addOption(addOptionDto);
    }

    @PutMapping("/option")
    @ResponseStatus(HttpStatus.OK)
    public OptionView updateOption(@RequestBody UpdateOptionDto updateOptionDto){
        return questionService.updateOption(updateOptionDto);
    }

    @DeleteMapping("/option/{optionId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteOption(@PathVariable String optionId){
        return questionService.deleteOption(UUID.fromString(optionId));
    }

    @GetMapping("/option/{questionId}")
    @ResponseStatus(HttpStatus.OK)
    public List<OptionView> getAllOptionsByQuestion(@PathVariable String questionId){
        return questionService.getAllOptionsByQuestion(UUID.fromString(questionId));
    }
}
