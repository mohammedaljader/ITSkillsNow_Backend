package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.dto.request.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.UpdateCourseDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.User;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.repository.UserRepository;
import com.itskillsnow.courseservice.service.interfaces.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    @Override
    public Boolean addCourse(AddCourseDto courseDto) {
        Optional<User> user = userRepository.findByUsername(courseDto.getUsername());
        if(user.isEmpty()){
            return false;
        }
        Course course = mapCreateDtoToModel(courseDto, user.get());
        courseRepository.save(course);
        return true;
    }

    @Override
    public Boolean updateCourse(UpdateCourseDto courseDto) {
        Optional<Course> course = courseRepository.findById(courseDto.getCourseId());
        if(course.isEmpty()){
            return false;
        }
        Course updatedCourse = mapUpdateDtoToModel(courseDto,course.get().getUser());
        courseRepository.save(updatedCourse);
        return true;
    }

    @Override
    public Boolean deleteCourse(UUID courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if(course.isEmpty()){
            return false;
        }
        course.ifPresent(courseRepository::delete);
        return true;
    }

    @Override
    public CourseView getCourseById(UUID courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if(course.isEmpty()){
            //TODO: proper Exceptions
            return null;
        }
        return mapModelToDto(course.get());
    }

    @Override
    public List<CourseView> getAllCoursesByUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            //TODO: proper Exceptions
            return null;
        }
        List<Course> courses = courseRepository.findAllByUser(user.get());
        return courses.stream()
                .map(this::mapModelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseView> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapModelToDto)
                .toList();
    }

    private CourseView mapModelToDto(Course course){
        return CourseView.builder()
                .courseId(course.getCourseId())
                .courseName(course.getCourseName())
                .courseDescription(course.getCourseDescription())
                .courseImage(course.getCourseImage())
                .coursePrice(course.getCoursePrice())
                .courseType(course.getCourseType())
                .courseLanguage(course.getCourseLanguage())
                .username(course.getUser().getUsername())
                .build();
    }

    private Course mapCreateDtoToModel(AddCourseDto courseDto, User user){
        return Course.builder()
                .courseName(courseDto.getCourseName())
                .courseDescription(courseDto.getCourseDescription())
                .courseImage(courseDto.getCourseImage())
                .coursePrice(courseDto.getCoursePrice())
                .courseType(courseDto.getCourseType())
                .courseLanguage(courseDto.getCourseLanguage())
                .user(user)
                .build();
    }

    private Course mapUpdateDtoToModel(UpdateCourseDto courseDto, User user){
        return Course.builder()
                .courseId(courseDto.getCourseId())
                .courseName(courseDto.getCourseName())
                .courseDescription(courseDto.getCourseDescription())
                .courseImage(courseDto.getCourseImage())
                .coursePrice(courseDto.getCoursePrice())
                .courseType(courseDto.getCourseType())
                .courseLanguage(courseDto.getCourseLanguage())
                .user(user)
                .build();
    }
}
