package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.repository.UserRepository;
import com.itskillsnow.courseservice.service.interfaces.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {


    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    private CourseService courseService;


    @BeforeEach
    void setUp() {
        courseService = new CourseServiceImpl(courseRepository, userRepository);
    }


    @Test
    void TestGetAllCourses(){
        Course course = Course.builder()
                .courseId(UUID.randomUUID())
                .courseName("Test")
                .build();
        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<CourseView> courses = courseService.getAllCourses();
        int expected = 1;

        assertEquals(courses.size(), expected);
    }
}