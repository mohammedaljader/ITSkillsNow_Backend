package com.itskillsnow.courseservice.unitTests;

import com.itskillsnow.courseservice.dto.request.course.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.User;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.repository.UserRepository;
import com.itskillsnow.courseservice.service.CourseServiceImpl;
import com.itskillsnow.courseservice.service.interfaces.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    public void given_addCourse_withCorrectData_returnsTrue() {
        // Prepare test data
        AddCourseDto courseDto = new AddCourseDto();
        courseDto.setCourseName("Test Course");
        courseDto.setCourseDescription("This is a test course");
        courseDto.setUsername("Username");

        User user = new User("Username");

        when(userRepository.findByUsername("Username")).thenReturn(Optional.of(user));
        when(courseRepository.save(any(Course.class))).thenReturn(new Course());

        // Call the addCourse function
        boolean result = courseService.addCourse(courseDto);

        // Verify the result
        assertTrue(result);

        // Verify that the course was saved
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    public void given_addCourse_withWrongData_returnsFalse() {
        // Prepare test data
        AddCourseDto courseDto = new AddCourseDto();
        courseDto.setCourseName("Test Course");
        courseDto.setCourseDescription("This is a test course");
        courseDto.setUsername("Username");

        when(userRepository.findByUsername("Username")).thenReturn(Optional.empty());

        // Call the addCourse function
        boolean result = courseService.addCourse(courseDto);

        // Verify the result
        assertFalse(result);

        // Verify that the course was not saved
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void given_updateCourse_withCorrectData_returnsTrue() {
        // Prepare test data
        UUID courseId = UUID.randomUUID();
        UpdateCourseDto courseDto = new UpdateCourseDto();
        courseDto.setCourseId(courseId);
        courseDto.setCourseName("New Course Name");
        courseDto.setCourseDescription("New course description");

        Course existingCourse = new Course();
        existingCourse.setCourseId(courseId);
        existingCourse.setCourseName("Old Course Name");
        existingCourse.setCourseDescription("Old course description");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(new Course());

        // Call the updateCourse function
        boolean result = courseService.updateCourse(courseDto);

        // Verify the result
        assertTrue(result);

        // Verify that the course was updated
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    public void given_updateCourse_withWrongData_returnsFalse() {
        // Prepare test data
        UUID courseId = UUID.randomUUID();
        UpdateCourseDto courseDto = new UpdateCourseDto();
        courseDto.setCourseId(courseId);
        courseDto.setCourseName("New Course Name");
        courseDto.setCourseDescription("New course description");

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Call the updateCourse function
        boolean result = courseService.updateCourse(courseDto);

        // Verify the result
        assertFalse(result);

        // Verify that the course was not updated
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void given_deleteCourse_withCorrectData_returnsTrue() {
        // Prepare test data
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setCourseId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Call the deleteCourse function
        boolean result = courseService.deleteCourse(courseId);

        // Verify the result
        assertTrue(result);

        // Verify that the course was deleted
        verify(courseRepository, times(1)).delete(course);
    }

    @Test
    public void given_deleteCourse_withWrongData_returnsFalse() {
        // Prepare test data
        UUID courseId = UUID.randomUUID();

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Call the deleteCourse function
        boolean result = courseService.deleteCourse(courseId);

        // Verify the result
        assertFalse(result);

        // Verify that the course was not deleted
        verify(courseRepository, never()).delete(any(Course.class));
    }

    @Test
    public void given_getCourseById_withCorrectData_returnsCourse() {
        // Prepare test data
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setCourseId(courseId);
        course.setUser(new User("Username"));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Call the getCourseById function
        CourseView result = courseService.getCourseById(courseId);

        // Verify the result
        assertNotNull(result);
        assertEquals(courseId, result.getCourseId());

        // Verify that the course was retrieved from the database
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    public void given_getCourseById_withWrongData_returnsNull() {
        // Prepare test data
        UUID courseId = UUID.randomUUID();

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Call the getCourseById function
        CourseView result = courseService.getCourseById(courseId);

        // Verify the result
        assertNull(result);

        // Verify that the course was not retrieved from the database
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    public void given_getAllCoursesByUser_withCorrectData_returnsListOfCourses() {
        // Prepare test data
        String username = "Username";
        User user = new User(username);
        Course course1 = new Course();
        course1.setUser(user);
        Course course2 = new Course();
        course2.setUser(user);
        List<Course> courses = new ArrayList<>();
        courses.add(course1);
        courses.add(course2);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(courseRepository.findAllByUser(user)).thenReturn(courses);

        // Call the getAllCoursesByUser function
        List<CourseView> result = courseService.getAllCoursesByUser(username);

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify that the courses were retrieved from the database
        verify(userRepository, times(1)).findByUsername(username);
        verify(courseRepository, times(1)).findAllByUser(user);
    }

    @Test
    public void given_getAllCoursesByUser_withWrongData_returnsNull() {
        // Prepare test data
        String username = "Username";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Call the getAllCoursesByUser function
        List<CourseView> result = courseService.getAllCoursesByUser(username);

        // Verify the result
        assertNull(result);

        // Verify that the user was not retrieved from the database
        verify(userRepository, times(1)).findByUsername(username);
        // Verify that the courses were not retrieved from the database
        verify(courseRepository, never()).findAllByUser(any());
    }

    @Test
    void given_getAllCourses_returnsAllCourses(){
        User user = new User("test");
        Course course = Course.builder()
                .courseId(UUID.randomUUID())
                .courseName("Test")
                .user(user)
                .build();
        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<CourseView> courses = courseService.getAllCourses();
        int expected = 1;

        assertEquals(courses.size(), expected);
    }
}