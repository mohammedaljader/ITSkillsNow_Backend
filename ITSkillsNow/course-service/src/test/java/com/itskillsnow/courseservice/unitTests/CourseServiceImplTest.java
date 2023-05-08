package com.itskillsnow.courseservice.unitTests;

import com.itskillsnow.courseservice.dto.request.course.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.course.AddCourseWithFileDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseWithFileDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.exception.CourseNotFoundException;
import com.itskillsnow.courseservice.exception.GeneralException;
import com.itskillsnow.courseservice.exception.UserNotFoundException;
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

    private static final UUID courseId = UUID.randomUUID();

    private static final String courseName = "CourseName";
    private static final String courseDescription = "CourseDescription";
    private static final String courseImage = "CourseImage";
    private static final Double coursePrice = 10.0;
    private static final String courseType = "CourseType";
    private static final String courseLanguage = "C#";
    private static final String username = "Username";


    @BeforeEach
    void setUp() {
        courseService = new CourseServiceImpl(courseRepository, userRepository, blobService);
    }

    @Test
    public void given_addCourse_withCorrectData_expectToSaveCourse() {
        // Arrange
        AddCourseDto courseDto = addCourse();
        User user = new User(username);
        Course expectedCourse = getCourse(user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(courseRepository.save(any(Course.class))).thenReturn(expectedCourse);


        // Act
        CourseView actual = courseService.addCourse(courseDto);

        // Assert
        assertEquals(actual.getCourseId(), courseId);
        assertEquals(actual.getCourseName(), courseName);
        assertEquals(actual.getCourseDescription(), courseDescription);


        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    public void given_addCourseWithImage_expectToSaveCourse() throws IOException {
        // Arrange
        MockMultipartFile mockMultipartFile = getFile();
        AddCourseWithFileDto dto = addCourseWithFile(mockMultipartFile);
        String newCourseImage = "newCourseImage";
        User user = new User(username);
        Course expectedCourse = getCourse(user);
        expectedCourse.setCourseImage(newCourseImage);


        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(blobService.storeFile(anyString(),
                any(InputStream.class),
                anyLong())
        ).thenReturn(newCourseImage);
        when(courseRepository.save(any(Course.class))).thenReturn(expectedCourse);


        // Act
        CourseView actual = courseService.addCourse(dto);

        // Assert
        verify(userRepository).findByUsername(username);
        verify(blobService).storeFile(anyString(), any(InputStream.class), anyLong());
        verify(courseRepository).save(any(Course.class));
        assertEquals(actual.getCourseId(), courseId);
        assertEquals(actual.getCourseName(), courseName);
        assertEquals(actual.getCourseDescription(), courseDescription);

        assertNotEquals(actual.getCourseImage(), courseImage);
    }

    @Test
    public void given_addCourse_withNoUser_returnsException() throws IOException {
        // Arrange

        MockMultipartFile mockMultipartFile = getFile();
        AddCourseWithFileDto dto = addCourseWithFile(mockMultipartFile);

        String expected = "User or course was not found!";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());


        // Act
        GeneralException actual = Assertions.assertThrows(GeneralException.class, () ->
            courseService.addCourse(dto)
        );

        // Assert
        verify(userRepository).findByUsername(username);
        verify(courseRepository, times(0)).save(any(Course.class));
        verify(blobService, times(0)).storeFile(any(String.class),
                any(InputStream.class), any(Long.class));
        assertEquals(actual.getMessage(), expected);
    }

    @Test
    public void testAddCourse_withNoImage_returnsException() {
        // Arrange
        AddCourseWithFileDto dto = new AddCourseWithFileDto();
        dto.setUsername(username);

        String expected = "User or course was not found!";

        // Act
        GeneralException actual = Assertions.assertThrows(GeneralException.class, () ->
                courseService.addCourse(dto)
        );

        // Assert
        verify(userRepository).findByUsername(username);
        verify(courseRepository, times(0)).save(any(Course.class));
        verify(blobService, times(0)).storeFile(any(String.class),
                any(InputStream.class), any(Long.class));
        assertEquals(actual.getMessage(), expected);
    }

    @Test
    public void given_addCourse_withWrongData_returnsException() {
        // Arrange
        AddCourseDto courseDto = addCourse();
        String expected = "User was not found!";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        UserNotFoundException actual = Assertions.assertThrows(UserNotFoundException.class, () ->
                courseService.addCourse(courseDto)
        );

        // Assert
        assertEquals(actual.getMessage(), expected);

        verify(courseRepository, never()).save(any(Course.class));
    }


    @Test
    public void given_updateCourse_withNewImage_expectToUpdateCourse() throws IOException {
        // Arrange
        String newCourseName = "newCourseName";
        String newCourseDescription = "newCourseDescription";
        String newCourseImage = "newCourseImage";

        MockMultipartFile mockMultipartFile = getFile();
        UpdateCourseWithFileDto dto = updateCourseWithFile(mockMultipartFile);

        dto.setCourseName(newCourseName);
        dto.setCourseDescription(newCourseDescription);

        User user = new User(username);
        Course oldCourse = getCourse(user);

        Course expected = getCourse(user);
        expected.setCourseName(newCourseName);
        expected.setCourseDescription(newCourseDescription);
        expected.setCourseImage(newCourseImage);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(oldCourse));
        when(blobService.storeFile(anyString(),
                any(InputStream.class),
                anyLong())
        ).thenReturn(newCourseImage);
        when(courseRepository.save(any(Course.class))).thenReturn(expected);


        // Act
        CourseView actual = courseService.updateCourse(dto);

        // Assert
        verify(courseRepository).findById(courseId);
        verify(blobService).storeFile(anyString(), any(InputStream.class), anyLong());
        verify(courseRepository, times(1)).save(any(Course.class));

        assertEquals(actual.getCourseImage(), newCourseImage);
        assertEquals(actual.getCourseName(), newCourseName);
        assertEquals(actual.getCourseDescription(), newCourseDescription);
        assertNotEquals(actual.getCourseImage(), oldCourse.getCourseImage());
    }


    @Test
    public void given_updateCourse_withNoNewImage_keepTheOldImageAndExpectToUpdateCourse() throws IOException {
        // Arrange
        String newCourseName = "newCourseName";
        String newCourseDescription = "newCourseDescription";
        UpdateCourseWithFileDto dto = updateCourseWithFile(null);
        dto.setCourseName(newCourseName);
        dto.setCourseDescription(newCourseDescription);

        User user = new User(username);
        Course oldCourse = getCourse(user);

        Course expected = getCourse(user);
        expected.setCourseName(newCourseName);
        expected.setCourseDescription(newCourseDescription);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(oldCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(expected);

        // Act
        CourseView actual = courseService.updateCourse(dto);

        // Assert
        verify(courseRepository).findById(courseId);
        verifyNoInteractions(blobService);
        verify(courseRepository, times(1)).save(any(Course.class));

        assertEquals(actual.getCourseImage(), oldCourse.getCourseImage());
        assertEquals(actual.getCourseName(), newCourseName);
        assertEquals(actual.getCourseDescription(), newCourseDescription);
    }

    @Test
    public void given_updateCourseWithWrongCourseId_returnsException() {
        // Arrange
        UpdateCourseWithFileDto dto = updateCourseWithFile(null);

        String expected = "Course was not found!";

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act
        CourseNotFoundException actual = Assertions.assertThrows(CourseNotFoundException.class, () ->
                courseService.updateCourse(dto)
        );


        // Assert
        verify(courseRepository).findById(courseId);
        verifyNoInteractions(blobService);
        verify(courseRepository, times(0)).save(any(Course.class));

        assertEquals(actual.getMessage(), expected);
    }

    @Test
    public void given_updateCourse_withCorrectData_expectToUpdateCourse() {
        // Arrange
        String newCourseName = "newCourseName";
        String newCourseDescription = "newCourseDescription";
        User user = new User(username);

        UpdateCourseDto courseDto = updateCourse();
        courseDto.setCourseName(newCourseName);
        courseDto.setCourseDescription(newCourseDescription);

        Course oldCourse = getCourse(user);

        Course expected = getCourse(user);
        expected.setCourseName(newCourseName);
        expected.setCourseDescription(newCourseDescription);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(oldCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(expected);

        // Act
        CourseView actual = courseService.updateCourse(courseDto);


        // Assert
        verify(courseRepository, times(1)).save(any(Course.class));
        assertEquals(actual.getCourseDescription(), newCourseDescription);
        assertEquals(actual.getCourseName(), newCourseName);
        assertEquals(actual.getCourseId(), oldCourse.getCourseId());
    }

    @Test
    public void given_updateCourse_withWrongId_returnsException() {
        // Arrange
        UpdateCourseDto courseDto = updateCourse();

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        String expected = "Course was not found!";

        // Act
        CourseNotFoundException actual = Assertions.assertThrows(CourseNotFoundException.class, () ->
                courseService.updateCourse(courseDto)
        );


        // Assert
        verify(courseRepository, never()).save(any(Course.class));
        assertEquals(actual.getMessage(), expected);
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

    private Course getCourse(User user){
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

    private AddCourseDto addCourse(){
        return new AddCourseDto(courseName, courseDescription,
                courseImage, coursePrice,
                courseType, courseLanguage,
                false, username);
    }

    private AddCourseWithFileDto addCourseWithFile(MockMultipartFile mockMultipartFile){
        return new AddCourseWithFileDto(courseName, courseDescription,
                mockMultipartFile, coursePrice,
                courseType, courseLanguage,
                false, username);
    }

    private UpdateCourseDto updateCourse(){
        return new UpdateCourseDto(courseId ,courseName, courseDescription,
                courseImage, coursePrice,
                courseType, courseLanguage,
                false, username);
    }

    private UpdateCourseWithFileDto updateCourseWithFile(MockMultipartFile mockMultipartFile){
        return new UpdateCourseWithFileDto(courseId, courseName, courseDescription,
                mockMultipartFile, coursePrice,
                courseType, courseLanguage,
                false, username);
    }
}