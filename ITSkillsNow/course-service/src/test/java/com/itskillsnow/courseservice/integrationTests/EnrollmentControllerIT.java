package com.itskillsnow.courseservice.integrationTests;

import com.itskillsnow.courseservice.dto.request.enrollment.AddEnrollmentDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.dto.response.EnrollmentView;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EnrollmentControllerIT {
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

    private static final String username1 = "User1";

    private static final String username2 = "User2";

    private UUID courseId1;

    private UUID courseId2;

    private static final String blobUrl = "https://example.com/blob/123456";

    @BeforeAll
    public static void init(){
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp(){
        baseUrl = baseUrl.concat(":").concat(port+ "").concat("/api/course/enrollment");
        //Create User
        User savedUser1 = userRepository.save(new User(username1));
        User savedUser2 = userRepository.save(new User(username2));
        Course course1 = Course.builder()
                .courseName("courseName")
                .courseDescription("courseDescription")
                .courseImage("courseImage")
                .coursePrice(10.00)
                .courseType("courseType")
                .courseLanguage("courseLanguage")
                .user(savedUser1)
                .build();
        Course course2 = Course.builder()
                .courseName("courseName2")
                .courseDescription("courseDescription2")
                .courseImage("courseImage2")
                .coursePrice(10.99)
                .courseType("courseType2")
                .courseLanguage("courseLanguage2")
                .user(savedUser2)
                .build();
        Course savedCourse1 = courseRepository.save(course1);
        Course savedCourse2 = courseRepository.save(course2);
        courseId1 = savedCourse1.getCourseId();
        courseId2 = savedCourse2.getCourseId();

        when(blobService.storeFile(anyString(),
                ArgumentMatchers.any(InputStream.class),
                anyLong())
        ).thenReturn(blobUrl);
    }

    @Test
    void given_enrollToCourse_withCorrectData_returnsTrue() {

        AddEnrollmentDto addEnrollmentDto = new AddEnrollmentDto(courseId1, username1);

        // Send a POST request to enroll to course
        ResponseEntity<Boolean> result = restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addEnrollmentDto), new ParameterizedTypeReference<>() {});

        // Send a GET request to retrieve all enrollments
        ResponseEntity<List<EnrollmentView>> response = restTemplate.exchange(baseUrl.concat("/").concat(username1), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify that the new enrollments is present in the response
        boolean enrollmentAdded = Objects.requireNonNull(response.getBody()).stream()
                .anyMatch(enrollment -> enrollment.getCourseView().getCourseId().equals(courseId1));


        assertEquals(Boolean.TRUE, result.getBody());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(enrollmentAdded);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void given_enrollToCourse_withWrongUsername_returnsFalse() {

        AddEnrollmentDto addEnrollmentDto = new AddEnrollmentDto(courseId1, "wrongUsername");

        // Send a POST request to enroll to course
        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl, HttpMethod.POST,
                                new HttpEntity<>(addEnrollmentDto), new ParameterizedTypeReference<>() {})
        );


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_getAllEnrollmentsByUser_withCorrectData_returnsEnrollments() {

        AddEnrollmentDto addEnrollmentDto1 = new AddEnrollmentDto(courseId1, username1);
        AddEnrollmentDto addEnrollmentDto2 = new AddEnrollmentDto(courseId2, username1);


        // Send a POST request to enroll to course
        restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addEnrollmentDto1), new ParameterizedTypeReference<>() {});

        restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addEnrollmentDto2), new ParameterizedTypeReference<>() {});

        // Send a GET request to retrieve all enrollments
        ResponseEntity<List<EnrollmentView>> responseUser1 = restTemplate.exchange(baseUrl.concat("/").concat(username1), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        ResponseEntity<List<EnrollmentView>> responseUser2 = restTemplate.exchange(baseUrl.concat("/").concat(username2), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify that the new enrollments is present in the response
        assertNotNull(responseUser1);
        assertEquals(Objects.requireNonNull(responseUser1.getBody()).get(0).getCourseView().getCourseId(), courseId1);
        assertEquals(Objects.requireNonNull(responseUser1.getBody()).get(1).getCourseView().getCourseId(), courseId2);
        assertEquals(HttpStatus.OK, responseUser1.getStatusCode());

        // the second user doesn't have enrollments
        assertEquals(0, Objects.requireNonNull(responseUser2.getBody()).size());
        assertEquals(HttpStatus.OK, responseUser2.getStatusCode());
    }


    @Test
    void given_getAllEnrollmentsByUser_withNoEnrollments_returnsEmptyResponse() {

        // Send a GET request to retrieve all enrollments
        ResponseEntity<List<EnrollmentView>> response = restTemplate.exchange(baseUrl.concat("/").concat(username1), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify that the new enrollments is present in the response
        assertEquals(0, Objects.requireNonNull(response.getBody()).size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
