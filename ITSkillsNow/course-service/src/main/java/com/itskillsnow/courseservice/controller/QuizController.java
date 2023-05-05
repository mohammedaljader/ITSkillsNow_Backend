package com.itskillsnow.courseservice.controller;

import com.itskillsnow.courseservice.dto.request.quiz.AddQuizDto;
import com.itskillsnow.courseservice.dto.request.quiz.UpdateQuizDto;
import com.itskillsnow.courseservice.dto.response.QuizView;
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
    public Boolean addQuiz(@RequestBody AddQuizDto addQuizDto){
        return quizService.addQuiz(addQuizDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean updateQuiz(@RequestBody UpdateQuizDto updateQuizDto){
        return quizService.updateQuiz(updateQuizDto);
    }

    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean deleteQuiz(@PathVariable String quizId){
        return quizService.deleteQuiz(UUID.fromString(quizId));
    }

    @GetMapping("/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public List<QuizView> getAllQuizzesByCourse(@PathVariable String courseId){
        return quizService.getAllQuizzesByCourse(UUID.fromString(courseId));
    }
}
