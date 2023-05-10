package com.itskillsnow.courseservice.controller;

import com.itskillsnow.courseservice.dto.request.quiz.AddQuizDto;
import com.itskillsnow.courseservice.dto.request.quiz.UpdateQuizDto;
import com.itskillsnow.courseservice.dto.response.QuizView;
import com.itskillsnow.courseservice.dto.response.QuizWithoutQuestionView;
import com.itskillsnow.courseservice.service.interfaces.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizWithoutQuestionView addQuiz(@RequestBody AddQuizDto addQuizDto){
        return quizService.addQuiz(addQuizDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public QuizWithoutQuestionView updateQuiz(@RequestBody UpdateQuizDto updateQuizDto){
        return quizService.updateQuiz(updateQuizDto);
    }

    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteQuiz(@PathVariable String quizId){
        return quizService.deleteQuiz(UUID.fromString(quizId));
    }

    @GetMapping("/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public List<QuizView> getAllQuizzesByCourse(@PathVariable String courseId){
        return quizService.getAllQuizzesByCourse(UUID.fromString(courseId));
    }

    @GetMapping("/get/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public List<QuizWithoutQuestionView> getAllQuizzesWithQuestionsByCourse(@PathVariable String courseId){
        return quizService.getAllQuizzesWithoutQuestionsByCourse(UUID.fromString(courseId));
    }
}
