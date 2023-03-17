package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.config.CourseMQConfig;
import com.itskillsnow.courseservice.dto.request.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.CustomMessage;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.models.Course;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.service.interfaces.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final RabbitTemplate template;

    @Override
    public Boolean addCourse(AddCourseDto courseDto) {
        Course course = new Course(courseDto.getCourseName());
        courseRepository.save(course);
        String newCourse = "course: " + course.getCourseName() + " added!";
        CustomMessage message = new CustomMessage(UUID.randomUUID().toString(), newCourse, new Date());
        try{
            template.convertAndSend(CourseMQConfig.EXCHANGE, CourseMQConfig.ROUTING_KEY, message);
        }catch (Exception exception){
            log.info(exception.getMessage());
        }
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
