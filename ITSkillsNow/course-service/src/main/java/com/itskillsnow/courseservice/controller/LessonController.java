package com.itskillsnow.courseservice.controller;


import com.itskillsnow.courseservice.dto.request.lesson.AddLessonDto;
import com.itskillsnow.courseservice.dto.request.lesson.UpdateLessonDto;
import com.itskillsnow.courseservice.dto.response.LessonView;
import com.itskillsnow.courseservice.service.interfaces.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LessonView addLesson(@RequestBody AddLessonDto addLessonDto){
        return lessonService.addLesson(addLessonDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public LessonView updateLesson(@RequestBody UpdateLessonDto updateLessonDto){
        return lessonService.updateLesson(updateLessonDto);
    }

    @DeleteMapping("/{lessonId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteLesson(@PathVariable String lessonId){
        return lessonService.deleteLesson(UUID.fromString(lessonId));
    }

    @GetMapping("/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public List<LessonView> getAllLessonsByCourse(@PathVariable String courseId){
        return lessonService.getAllLessonsByCourse(UUID.fromString(courseId));
    }

    @GetMapping("/get/{lessonId}")
    @ResponseStatus(HttpStatus.OK)
    public LessonView getLessonById(@PathVariable String lessonId){
        return lessonService.getLessonById(UUID.fromString(lessonId));
    }

}
