package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.dto.request.enrollment.AddEnrollmentDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.dto.response.EnrollmentView;
import com.itskillsnow.courseservice.exception.CourseNotFoundException;
import com.itskillsnow.courseservice.exception.UserNotFoundException;
import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.Enrollment;
import com.itskillsnow.courseservice.model.User;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.repository.EnrollmentRepository;
import com.itskillsnow.courseservice.repository.UserRepository;
import com.itskillsnow.courseservice.service.interfaces.EnrollmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    private static final String userNotFound = "User was not found!";

    private static final String courseNotFound = "Course was not found!";

    @Override
    public boolean enrollToCourse(AddEnrollmentDto addEnrollmentDto) {
        User user = userRepository.findByUsername(addEnrollmentDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userNotFound));

        Course course = courseRepository.findById(addEnrollmentDto.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(courseNotFound));

        if(enrollmentRepository.existsByCourse(course)){
            return false;
        }

        Enrollment enrollment = mapDtoToModel(user, course);
        enrollmentRepository.save(enrollment);
        return true;
    }

    @Override
    public List<EnrollmentView> getAllEnrollments(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(userNotFound));

        return enrollmentRepository.findAllByUser(user)
                .stream()
                .map(this::mapModelToDto)
                .collect(Collectors.toList());
    }


    private Enrollment mapDtoToModel(User user, Course course){
        return Enrollment.builder()
                .enrollmentDate(LocalDate.now())
                .enrollmentTime(LocalTime.now())
                .user(user)
                .course(course)
                .build();
    }

    private EnrollmentView mapModelToDto(Enrollment enrollment){
        return EnrollmentView.builder()
                .enrollmentDate(enrollment.getEnrollmentDate())
                .enrollmentTime(enrollment.getEnrollmentTime())
                .courseView(mapCourseModelToDto(enrollment.getCourse()))
                .build();
    }

    private CourseView mapCourseModelToDto(Course course){
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

}
