package com.itskillsnow.courseservice.unitTests;

import com.itskillsnow.courseservice.dto.request.course.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.course.AddCourseWithFileDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseWithFileDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.exception.CourseNotFoundException;
import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.User;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.repository.UserRepository;
import com.itskillsnow.courseservice.service.CourseServiceImpl;
import com.itskillsnow.courseservice.service.interfaces.BlobService;
import com.itskillsnow.courseservice.service.interfaces.CourseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    @Mock
    private BlobService blobService;

    private CourseService courseService;


    @BeforeEach
    void setUp() {
        courseService = new CourseServiceImpl(courseRepository, userRepository, blobService);
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
    public void given_addCourseWithImage_returnsTrue() throws IOException {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        MockMultipartFile mockMultipartFile = getFile();
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(blobService.storeFile(anyString(),
                any(InputStream.class),
                anyLong())
        ).thenReturn("Course_image");


        AddCourseWithFileDto dto = new AddCourseWithFileDto();
        dto.setCourseImage(mockMultipartFile);
        dto.setUsername("testUser");

        // Act
        boolean result = courseService.addCourse(dto);

        // Assert
        verify(userRepository).findByUsername("testUser");
        verify(blobService).storeFile(anyString(), any(InputStream.class), anyLong());
        verify(courseRepository).save(any(Course.class));
        assertTrue(result);
    }

    @Test
    public void given_addCourse_withNoUser_returnsFalse() throws IOException {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        AddCourseWithFileDto dto = new AddCourseWithFileDto();
        dto.setCourseImage(new MockMultipartFile("test-image", new byte[] {1, 2, 3}));
        dto.setUsername("testUser");

        // Act
        boolean result = courseService.addCourse(dto);

        // Assert
        verify(userRepository).findByUsername("testUser");
        verify(courseRepository, times(0)).save(any(Course.class));
        verify(blobService, times(0)).storeFile(any(String.class), any(InputStream.class), any(Long.class));
        assertFalse(result);
    }

    @Test
    public void testAddCourse_noImage() throws IOException {
        // Arrange
        AddCourseWithFileDto dto = new AddCourseWithFileDto();
        dto.setUsername("testUser");

        // Act
        boolean result = courseService.addCourse(dto);

        // Assert
        verify(userRepository).findByUsername("testUser");
        verify(courseRepository, times(0)).save(any(Course.class));
        verify(blobService, times(0)).storeFile(any(String.class), any(InputStream.class), any(Long.class));
        assertFalse(result);
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
    public void given_updateCourse_withNewImage_returnsTrue() throws IOException {
        // Arrange
        UpdateCourseWithFileDto dto = new UpdateCourseWithFileDto();
        UUID courseId = UUID.randomUUID();
        dto.setCourseId(courseId);
        MockMultipartFile mockMultipartFile = getFile();
        dto.setCourseImage(mockMultipartFile);

        Course course = new Course();
        course.setUser(new User());
        course.setCourseImage("oldImage.jpg");
        Optional<Course> optionalCourse = Optional.of(course);

        when(courseRepository.findById(courseId)).thenReturn(optionalCourse);
        when(blobService.storeFile(anyString(),
                any(InputStream.class),
                anyLong())
        ).thenReturn("newImage.jpg");


        // Act
        boolean result = courseService.updateCourse(dto);

        // Assert
        verify(courseRepository).findById(courseId);
        verify(blobService).storeFile(anyString(), any(InputStream.class), anyLong());
        verify(courseRepository, times(1)).save(any(Course.class));
        assertTrue(result);
    }



    @Test
    public void given_updateCourse_withNoNewImage_keepTheOldImageAndReturnsTrue() throws IOException {
        // Arrange
        UpdateCourseWithFileDto dto = new UpdateCourseWithFileDto();
        UUID courseId = UUID.randomUUID();
        dto.setCourseId(courseId);

        Course course = new Course();
        course.setUser(new User());
        course.setCourseImage("oldImage.jpg");
        Optional<Course> optionalCourse = Optional.of(course);

        when(courseRepository.findById(courseId)).thenReturn(optionalCourse);

        // Act
        boolean result = courseService.updateCourse(dto);

        // Assert
        verify(courseRepository).findById(courseId);
        verifyNoInteractions(blobService);
        verify(courseRepository, times(1)).save(any(Course.class));
        assertTrue(result);
    }

    @Test
    public void given_updateCourseWithWrongCourseId_returnsFalse() throws IOException {
        // Arrange
        UpdateCourseWithFileDto dto = new UpdateCourseWithFileDto();
        UUID courseId = UUID.randomUUID();
        dto.setCourseId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act
        boolean result = courseService.updateCourse(dto);

        // Assert
        verify(courseRepository).findById(courseId);
        verifyNoInteractions(blobService);
        verify(courseRepository, times(0)).save(any(Course.class));
        assertFalse(result);
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
        verify(blobService, times(1)).deleteFile(course.getCourseImage());
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
        verify(blobService, never()).deleteFile(any(String.class));
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
        UUID courseId = UUID.randomUUID();

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        CourseNotFoundException exception = Assertions.assertThrows(CourseNotFoundException.class, () ->
            courseService.getCourseById(courseId)
        );

        String expectedMessage = "Course with id: " + courseId + " not found!";
        String actualMessage = exception.getMessage();
        Assertions.assertEquals(expectedMessage, actualMessage);

        verify(courseRepository).findById(courseId);
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
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        CourseNotFoundException exception = Assertions.assertThrows(CourseNotFoundException.class, () ->
            courseService.getCourseById(courseId)
        );

        String expectedMessage = "Course with id: " + courseId + " not found!";
        String actualMessage = exception.getMessage();
        Assertions.assertEquals(expectedMessage, actualMessage);

        verify(courseRepository).findById(courseId);
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

    private MockMultipartFile getFile() throws IOException {
        String name = "courseImage";
        String originalFilename = "courseImage";
        String contentType = "image/jpeg";
        byte[] content = new byte[]{1, 2, 3};
        InputStream inputStream = new ByteArrayInputStream(content);

        return new MockMultipartFile(name, originalFilename, contentType, inputStream);
    }
}