package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.dto.request.AddCourseDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.models.Course;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.service.interfaces.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public Boolean addCourse(AddCourseDto courseDto) {
        Course course = new Course(courseDto.getCourseName());
        courseRepository.save(course);
        return true;
    }

    @Override
    public List<CourseView> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(x -> new CourseView(x.getCourseId(), x.getCourseName()))
                .toList();
    }
}
