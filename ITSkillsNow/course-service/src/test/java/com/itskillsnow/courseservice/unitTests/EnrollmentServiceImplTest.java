package com.itskillsnow.courseservice.unitTests;

import com.itskillsnow.courseservice.dto.request.enrollment.AddEnrollmentDto;
import com.itskillsnow.courseservice.dto.response.EnrollmentView;
import com.itskillsnow.courseservice.exception.CourseNotFoundException;
import com.itskillsnow.courseservice.exception.UserNotFoundException;
import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.Enrollment;
import com.itskillsnow.courseservice.model.User;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.repository.EnrollmentRepository;
import com.itskillsnow.courseservice.repository.UserRepository;
import com.itskillsnow.courseservice.service.EnrollmentServiceImpl;
import com.itskillsnow.courseservice.service.interfaces.EnrollmentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserRepository userRepository;

    private EnrollmentService enrollmentService;

    private static final String username = "User";

    private static final String userNotFound = "User was not found!";

    private static final String courseNotFound = "Course was not found!";

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentServiceImpl(enrollmentRepository, userRepository, courseRepository);
    }


    @Test
    void givenEnrollToCourse_withCorrectData_returnsTrue(){
        // Arrange
        User user = new User(username);
        Course course = getCourse(user);
        AddEnrollmentDto addEnrollmentDto = new AddEnrollmentDto(course.getCourseId(), username);
        boolean expected = true;

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(courseRepository.findById(course.getCourseId())).thenReturn(Optional.of(course));

        // Act
        boolean actual = enrollmentService.enrollToCourse(addEnrollmentDto);

        // Assert
        assertEquals(expected, actual);

        verify(userRepository, times(1)).findByUsername(username);
        verify(courseRepository, times(1)).findById(course.getCourseId());
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));

    }

    @Test
    void givenEnrollToCourse_withWrongUsername_returnsFalse(){
        // Arrange
        Course course = getCourse(new User("WrongUsername"));
        AddEnrollmentDto addEnrollmentDto = new AddEnrollmentDto(course.getCourseId(),
                "WrongUsername");

        when(userRepository.findByUsername("WrongUsername")).thenReturn(Optional.empty());

        // Act
        UserNotFoundException actual = Assertions.assertThrows(UserNotFoundException.class, () ->
                enrollmentService.enrollToCourse(addEnrollmentDto)
        );

        // Assert
        assertEquals(userNotFound, actual.getMessage());

        verify(userRepository, times(1)).findByUsername("WrongUsername");
        verify(courseRepository, times(0)).findById(course.getCourseId());
        verify(enrollmentRepository, times(0)).save(any(Enrollment.class));
    }


    @Test
    void givenEnrollToCourse_withWrongCourseId_returnsFalse(){
        // Arrange
        User user = new User(username);
        Course course = getCourse(user);
        AddEnrollmentDto addEnrollmentDto = new AddEnrollmentDto(course.getCourseId(), username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(courseRepository.findById(course.getCourseId())).thenReturn(Optional.empty());

        // Act
        CourseNotFoundException actual = Assertions.assertThrows(CourseNotFoundException.class, () ->
                enrollmentService.enrollToCourse(addEnrollmentDto)
        );

        // Assert
        assertEquals(courseNotFound, actual.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(courseRepository, times(1)).findById(course.getCourseId());
        verify(enrollmentRepository, times(0)).save(any(Enrollment.class));
    }


    @Test
    void givenGetAllEnrollmentsByUser_withCorrectUsername_returnsEnrollmentData(){
        // Arrange
        User user = new User(username);
        Enrollment enrollment1 = getEnrollment(user);
        Enrollment enrollment2 = getEnrollment(user);
        Enrollment enrollment3 = getEnrollment(user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(enrollmentRepository.findAllByUser(user)).thenReturn(List.of(enrollment1, enrollment2, enrollment3));

        // Act

        List<EnrollmentView> actual = enrollmentService.getAllEnrollments(username);


        // Assert
        assertEquals(3, actual.size());
        verify(userRepository, times(1)).findByUsername(username);
        verify(enrollmentRepository, times(1)).findAllByUser(user);
    }


    @Test
    void givenGetAllEnrollmentsByUser_withWrongUsername_returnsException(){
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        String expected = "User was not found!";

        // Act
        UserNotFoundException actual = Assertions.assertThrows(UserNotFoundException.class, () ->
                enrollmentService.getAllEnrollments(username)
        );


        // Assert
        assertEquals(expected, actual.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(enrollmentRepository, times(0)).findAllByUser(any(User.class));
    }


    private Course getCourse(User user){
        UUID courseId = UUID.randomUUID();
        String courseName = "CourseName";
        String courseDescription = "CourseDescription";
        String courseImage = "CourseImage";
        Double coursePrice = 10.0;
        String courseType = "CourseType";
        String courseLanguage = "C#";

      return Course.builder()
              .courseId(courseId)
              .courseName(courseName)
              .courseDescription(courseDescription)
              .courseImage(courseImage)
              .coursePrice(coursePrice)
              .courseType(courseType)
              .courseLanguage(courseLanguage)
              .user(user)
              .build();
    }

    private Enrollment getEnrollment(User user){
        Course course = new Course();
        course.setUser(user);
        return Enrollment.builder()
                .enrollmentId(UUID.randomUUID())
                .user(user)
                .course(course)
                .build();
    }
}