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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final CourseRepository courseRepository;

    private final LessonRepository lessonRepository;


    @Override
    public boolean addLesson(AddLessonDto addLessonDto) {
        Optional<Course> course = courseRepository.findById(addLessonDto.getCourseId());
        if(course.isEmpty()){
            return false;
        }
        Lesson lesson = mapDtoToModel(addLessonDto, course.get());
        lessonRepository.save(lesson);
        return true;
    }

    @Override
    public boolean updateLesson(UpdateLessonDto updateLessonDto) {
        Optional<Lesson> lesson = lessonRepository.findById(updateLessonDto.getLessonId());
        if(lesson.isEmpty()){
            return false;
        }
        Lesson updatedLesson = mapDtoToModel(updateLessonDto);
        lessonRepository.save(updatedLesson);
        return true;
    }

    @Override
    public boolean deleteLesson(UUID lessonId) {
        Optional<Lesson> lesson = lessonRepository.findById(lessonId);
        if(lesson.isEmpty()){
            return false;
        }
        lessonRepository.delete(lesson.get());
        return true;
    }

    @Override
    public List<LessonView> getAllLessonsByCourse(UUID courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if(course.isEmpty()){
            throw new CourseNotFoundException("Course was not found!");
        }
        List<Lesson> lessons = lessonRepository.findByCourse(course.get());
        return lessons.stream().map(this::mapModelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public LessonView getLessonById(UUID lessonId) {
        Optional<Lesson> lesson = lessonRepository.findById(lessonId);
        if(lesson.isEmpty()){
            throw new LessonNotFoundException("Lesson was not found!");
        }
        return mapModelToDto(lesson.get());
    }

    private Lesson mapDtoToModel(AddLessonDto addLessonDto, Course course){
        return Lesson.builder()
                .lessonName(addLessonDto.getLessonName())
                .lessonContent(addLessonDto.getLessonContent())
                .course(course)
                .build();
    }

    private Lesson mapDtoToModel(UpdateLessonDto updateLessonDto){
        return Lesson.builder()
                .lessonId(updateLessonDto.getLessonId())
                .lessonName(updateLessonDto.getLessonName())
                .lessonContent(updateLessonDto.getLessonContent())
                .build();
    }

    private LessonView mapModelToDto(Lesson lesson){
        return LessonView.builder()
                .lessonId(lesson.getLessonId())
                .lessonName(lesson.getLessonName())
                .lessonContent(lesson.getLessonContent())
                .build();
    }
}
