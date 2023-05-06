package com.itskillsnow.courseservice.controller;

import com.itskillsnow.courseservice.dto.request.quizResult.GetUserResultsDto;
import com.itskillsnow.courseservice.dto.request.quizResult.SubmitQuizDto;
import com.itskillsnow.courseservice.dto.response.QuizResultView;
import com.itskillsnow.courseservice.service.interfaces.QuizUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course/quizResult")
@RequiredArgsConstructor
public class QuizUserController {

    private final QuizUserService quizUserService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Boolean submitQuiz(@RequestBody SubmitQuizDto submitQuizDto){
        return quizUserService.submitQuiz(submitQuizDto.getQuizId(),
                submitQuizDto.getUsername(), submitQuizDto.getUserAnswers());
    }

    @PostMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public QuizResultView getQuizResultByUser(@RequestBody GetUserResultsDto resultsDto){
        return quizUserService.getQuizResultByUser(resultsDto.getQuizId(), resultsDto.getUsername());
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<QuizResultView> getAllQuizzesResultByUser(@PathVariable String username){
        return quizUserService.getAllQuizzesResultByUser(username);
    }
}
