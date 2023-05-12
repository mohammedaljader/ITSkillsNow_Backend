package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.dto.request.lesson.AddLessonDto;
import com.itskillsnow.courseservice.dto.request.lesson.UpdateLessonDto;
import com.itskillsnow.courseservice.dto.response.LessonView;
import com.itskillsnow.courseservice.exception.CourseNotFoundException;
import com.itskillsnow.courseservice.exception.LessonNotFoundException;
import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.Lesson;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.repository.LessonRepository;
import com.itskillsnow.courseservice.service.interfaces.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final CourseRepository courseRepository;

    private final LessonRepository lessonRepository;


    @Override
    public LessonView addLesson(AddLessonDto addLessonDto) {
        Course course = courseRepository.findById(addLessonDto.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course was not found!"));

        Lesson lesson = mapAddLessonDtoToModel(addLessonDto, course);
        Lesson savedLesson = lessonRepository.save(lesson);
        return mapModelToDto(savedLesson);
    }

    @Override
    public LessonView updateLesson(UpdateLessonDto updateLessonDto) {
        Lesson lesson = lessonRepository.findById(updateLessonDto.getLessonId())
                .orElseThrow(() -> new LessonNotFoundException("Lesson was not found!"));

        Lesson updatedLesson = mapUpdateLessonDtoToModel(lesson, updateLessonDto);
        Lesson savedLesson = lessonRepository.save(updatedLesson);
        return mapModelToDto(savedLesson);
    }

    @Override
    public boolean deleteLesson(UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson was not found!"));

        lessonRepository.delete(lesson);
        return true;
    }

    @Override
    public List<LessonView> getAllLessonsByCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course was not found!"));

        List<Lesson> lessons = lessonRepository.findByCourse(course);
        return lessons.stream().map(this::mapModelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public LessonView getLessonById(UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson was not found!"));
        return mapModelToDto(lesson);
    }

    private Lesson mapAddLessonDtoToModel(AddLessonDto addLessonDto, Course course){
        return Lesson.builder()
                .lessonName(addLessonDto.getLessonName())
                .lessonContent(addLessonDto.getLessonContent())
                .course(course)
                .build();
    }


    private Lesson mapUpdateLessonDtoToModel(Lesson lesson, UpdateLessonDto updateLessonDto){
        lesson.setLessonName(updateLessonDto.getLessonName());
        lesson.setLessonContent(updateLessonDto.getLessonContent());
        return lesson;
    }

    private LessonView mapModelToDto(Lesson lesson){
        return LessonView.builder()
                .lessonId(lesson.getLessonId())
                .lessonName(lesson.getLessonName())
                .lessonContent(lesson.getLessonContent())
                .build();
    }
}
