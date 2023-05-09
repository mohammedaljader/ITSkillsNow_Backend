package com.itskillsnow.courseservice.integrationTests;

import com.itskillsnow.courseservice.dto.request.lesson.AddLessonDto;
import com.itskillsnow.courseservice.dto.request.lesson.UpdateLessonDto;
import com.itskillsnow.courseservice.dto.response.LessonView;
import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.User;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.repository.UserRepository;
import com.itskillsnow.courseservice.service.interfaces.BlobService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LessonControllerIT {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";


    @MockBean
    private BlobService blobService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    private static RestTemplate restTemplate;

    private static final String username = "User1";


    private UUID courseId;


    private static final String blobUrl = "https://example.com/blob/123456";

    @BeforeAll
    public static void init(){
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp(){
        baseUrl = baseUrl.concat(":").concat(port+ "").concat("/api/course/lesson");
        //Create User
        User savedUser = userRepository.save(new User(username));
        Course course = Course.builder()
                .courseName("courseName")
                .courseDescription("courseDescription")
                .courseImage("courseImage")
                .coursePrice(10.00)
                .courseType("courseType")
                .courseLanguage("courseLanguage")
                .user(savedUser)
                .build();
        Course savedCourse = courseRepository.save(course);
        courseId = savedCourse.getCourseId();

        when(blobService.storeFile(anyString(),
                ArgumentMatchers.any(InputStream.class),
                anyLong())
        ).thenReturn(blobUrl);
    }

    @Test
    void given_addLesson_withCorrectData_returnsSavedLesson() {
        String lessonName = "LessonName";
        String lessonContent = "LessonContent";
        AddLessonDto addLessonDto = new AddLessonDto(lessonName, lessonContent, courseId);

        // Send a POST request
        ResponseEntity<LessonView> result = restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addLessonDto), new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(lessonName, Objects.requireNonNull(result.getBody()).getLessonName());
        assertEquals(lessonContent, Objects.requireNonNull(result.getBody()).getLessonContent());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void given_addLesson_withWrongCourseId_returnsException() {
        String lessonName = "LessonName";
        String lessonContent = "LessonContent";
        AddLessonDto addLessonDto = new AddLessonDto(lessonName, lessonContent, UUID.randomUUID());

        // Send a POST request
        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl, HttpMethod.POST,
                                new HttpEntity<>(addLessonDto), new ParameterizedTypeReference<>() {})
        );

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void given_updateLesson_withCorrectData_returnsSavedLesson() {
        String lessonName = "LessonName";
        String lessonContent = "LessonContent";
        AddLessonDto addLessonDto = new AddLessonDto(lessonName, lessonContent, courseId);

        // Send a POST request
        ResponseEntity<LessonView> postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addLessonDto), new ParameterizedTypeReference<>() {});

        UUID lessonId = Objects.requireNonNull(postResponse.getBody()).getLessonId();

        // Send a PUT request
        UpdateLessonDto updateLessonDto = new UpdateLessonDto(lessonId, "newName", "newContent");
        ResponseEntity<LessonView> result = restTemplate.exchange(baseUrl, HttpMethod.PUT,
                new HttpEntity<>(updateLessonDto), new ParameterizedTypeReference<>() {});

        // Verify
        assertNotEquals(lessonName, Objects.requireNonNull(result.getBody()).getLessonName());
        assertNotEquals(lessonContent, Objects.requireNonNull(result.getBody()).getLessonContent());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void given_updateLesson_withWrongLessonId_returnsException() {
        // Send a PUT request
        UpdateLessonDto updateLessonDto = new UpdateLessonDto(UUID.randomUUID(), "newName", "newContent");

        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl, HttpMethod.PUT,
                                new HttpEntity<>(updateLessonDto), new ParameterizedTypeReference<>() {})
        );

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    @Test
    void given_deleteLesson_withCorrectData_returnsTrue() {
        String lessonName = "LessonName";
        String lessonContent = "LessonContent";
        AddLessonDto addLessonDto = new AddLessonDto(lessonName, lessonContent, courseId);

        // Send a POST request
        ResponseEntity<LessonView> postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addLessonDto), new ParameterizedTypeReference<>() {});

        UUID lessonId = Objects.requireNonNull(postResponse.getBody()).getLessonId();

        // Send a DELETE request
        ResponseEntity<Boolean> result = restTemplate.exchange(baseUrl.concat("/").concat(lessonId.toString()),
                HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(Boolean.TRUE, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void given_deleteLesson_withWrongLessonId_returnsFalse() {
        // Send a DELETE request
        ResponseEntity<Boolean> result = restTemplate.exchange(baseUrl.concat("/").concat(UUID.randomUUID().toString()),
                HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(Boolean.FALSE, result.getBody());
    }

    @Test
    void given_getLessonsByCourse_withCorrectData_returnsLesson() {
        String lessonName = "LessonName";
        String lessonContent = "LessonContent";
        AddLessonDto addLessonDto = new AddLessonDto(lessonName, lessonContent, courseId);

        String lessonName2 = "LessonName1";
        String lessonContent2 = "LessonContent1";
        AddLessonDto addLessonDto2 = new AddLessonDto(lessonName2, lessonContent2, courseId);

        // Send a POST request
        restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addLessonDto), new ParameterizedTypeReference<>() {});

        restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addLessonDto2), new ParameterizedTypeReference<>() {});

        // Send a GET request
        ResponseEntity<List<LessonView>> result = restTemplate.exchange(baseUrl.concat("/").concat(courseId.toString()),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(2, Objects.requireNonNull(result.getBody()).size());
        assertEquals(lessonName, Objects.requireNonNull(result.getBody()).get(0).getLessonName());
        assertEquals(lessonName2, Objects.requireNonNull(result.getBody()).get(1).getLessonName());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void given_getLessonsByCourse_withWrongCourseId_returnsException() {
        // Send a GET request
        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl.concat("/").concat(UUID.randomUUID().toString()),
                                HttpMethod.GET, null, new ParameterizedTypeReference<>() {})
        );
        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    @Test
    void given_getLessonByLessonId_withCorrectData_returnsLesson() {
        String lessonName = "LessonName";
        String lessonContent = "LessonContent";
        AddLessonDto addLessonDto = new AddLessonDto(lessonName, lessonContent, courseId);


        // Send a POST request
        ResponseEntity<LessonView> postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addLessonDto), new ParameterizedTypeReference<>() {});

        UUID lessonId = Objects.requireNonNull(postResponse.getBody()).getLessonId();


        // Send a GET request
        ResponseEntity<LessonView> result = restTemplate.exchange(baseUrl.concat("/get/").concat(lessonId.toString()),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(lessonName, Objects.requireNonNull(result.getBody()).getLessonName());
        assertEquals(lessonContent, Objects.requireNonNull(result.getBody()).getLessonContent());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }


    @Test
    void given_getLessonByLessonId_withWrongLessonId_returnsException() {
        // Send a GET request
        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl.concat("/get/").concat(UUID.randomUUID().toString()),
                                HttpMethod.GET, null, new ParameterizedTypeReference<>() {})
        );
        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
