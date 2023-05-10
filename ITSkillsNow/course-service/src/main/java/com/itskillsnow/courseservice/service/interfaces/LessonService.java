package com.itskillsnow.courseservice.service.interfaces;


import com.itskillsnow.courseservice.dto.request.lesson.AddLessonDto;
import com.itskillsnow.courseservice.dto.request.lesson.UpdateLessonDto;
import com.itskillsnow.courseservice.dto.response.LessonView;

import java.util.List;
import java.util.UUID;

public interface LessonService {

    LessonView addLesson(AddLessonDto addLessonDto);

    LessonView updateLesson(UpdateLessonDto updateLessonDto);

    boolean deleteLesson(UUID lessonId);

    List<LessonView> getAllLessonsByCourse(UUID courseId);

    LessonView getLessonById(UUID lessonId);
}
