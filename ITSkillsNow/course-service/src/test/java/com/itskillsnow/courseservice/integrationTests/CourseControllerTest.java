package com.itskillsnow.courseservice.integrationTests;

import com.itskillsnow.courseservice.dto.request.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.UpdateCourseDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.model.User;
import com.itskillsnow.courseservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseControllerTest {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    @Autowired
    private UserRepository userRepository;

    private static RestTemplate restTemplate;

    @BeforeAll
    public static void init(){
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp(){
        baseUrl = baseUrl.concat(":").concat(port+ "").concat("/api/course");
        //Create User
        userRepository.save(new User("User"));
    }

    @Test
    void given_addCourse_withCorrectData_returnsTrue() {

        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();

        // Send a POST request to add the course
        Boolean result = restTemplate.postForObject(baseUrl, courseDto, Boolean.class);

        // Send a GET request to retrieve all courses
        ResponseEntity<List<CourseView>> response = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify that the new courses is present in the response
        boolean coursesAdded = Objects.requireNonNull(response.getBody()).stream()
                .anyMatch(course -> course.getCourseName().equals(courseDto.getCourseName()));


        assertEquals(Boolean.TRUE, result);
        assertTrue(coursesAdded);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void given_addCourse_withNoUser_returnsFalse() {
        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();
        courseDto.setUsername("");

        // Send a POST request to add the course
        Boolean result = restTemplate.postForObject(baseUrl, courseDto, Boolean.class);

        // Send a GET request to retrieve all courses
        ResponseEntity<List<CourseView>> response = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(Boolean.FALSE, result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void given_updateCourse_withCorrectData_returnsTrue(){
        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();
        restTemplate.postForObject(baseUrl, courseDto, Boolean.class);
        // Send a GET request to retrieve all courses
        ResponseEntity<List<CourseView>> courses = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        //Get Course id the response list
        UpdateCourseDto updateCourseDto = getUpdateCourseDto(Objects.requireNonNull(courses.getBody())
                .get(0).getCourseId());


        //Send a PUT request to update course
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(updateCourseDto),
                Boolean.class);

        // Verify the response
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
    }

    @Test
    void given_updateCourse_withWrongCourseId_returnFalse(){
        //Update payload
        UpdateCourseDto updateCourseDto = getUpdateCourseDto(UUID.randomUUID());

        //Send a PUT request to update course
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(updateCourseDto),
                Boolean.class);

        // Verify the response
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(Boolean.FALSE, response.getBody());
    }


    @Test
    void given_getAllCoursesByUser_withCorrectUsername_returnsAllCoursesOfUser(){
        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();
        restTemplate.postForObject(baseUrl, courseDto, Boolean.class);

        // Send a GET request to retrieve all courses by user
        ResponseEntity<List<CourseView>> response = restTemplate.exchange(baseUrl.concat("/user/")
                        .concat(courseDto.getUsername()),
                HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<CourseView> courseViews = response.getBody();
        assert courseViews != null;
        assertEquals(courseDto.getCourseName(), courseViews.get(0).getCourseName());
        assertEquals(courseDto.getCourseDescription(), courseViews.get(0).getCourseDescription());
    }

    @Test
    void given_getAllCoursesByUser_withWrongUsername_returnsNoData(){
        // Send a GET request to retrieve all courses by user
        ResponseEntity<List<CourseView>> response = restTemplate.exchange(baseUrl
                        .concat("/user/").concat("Test"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<CourseView> courseViews = response.getBody();
        assertNull(courseViews);
    }

    @Test
    void given_deleteCourse_withCorrectCourseId_returnsTrue(){
        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();
        restTemplate.postForObject(baseUrl, courseDto, Boolean.class);
        // Send a GET request to retrieve all courses
        ResponseEntity<List<CourseView>> courses = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        //Get course id the response list
        UUID courseId = Objects.requireNonNull(courses.getBody()).get(0).getCourseId();


        //Send a Delete request to delete course
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl.concat("/")
                        .concat(courseId.toString()),
                HttpMethod.DELETE,
                null,
                Boolean.class);

        // Verify the response
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
    }


    @Test
    void given_deleteCourse_withWrongCourseId_returnsFalse(){
        //Send a Delete request to delete course
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl.concat("/")
                        .concat(UUID.randomUUID().toString()),
                HttpMethod.DELETE,
                null,
                Boolean.class);

        // Verify the response
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(Boolean.FALSE, response.getBody());
    }


    @Test
    void given_getCourseById_withCorrectId_returnsCourse(){
        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();
        restTemplate.postForObject(baseUrl, courseDto, Boolean.class);
        // Send a GET request to retrieve all courses
        ResponseEntity<List<CourseView>> courses = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        //Get course id the response list
        UUID courseId = Objects.requireNonNull(courses.getBody()).get(0).getCourseId();


        //Send a GET request to get course
        ResponseEntity<CourseView> response = restTemplate.exchange(baseUrl.concat("/")
                        .concat(courseId.toString()),
                HttpMethod.GET,
                null,
                CourseView.class);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test", Objects.requireNonNull(response.getBody()).getCourseName());
        assertEquals("Test", Objects.requireNonNull(response.getBody()).getCourseDescription());
    }

    @Test
    void given_getCourseById_withWrongId_returnsNull(){
        //Send a GET request to get course
        ResponseEntity<CourseView> response = restTemplate.exchange(baseUrl.concat("/")
                        .concat(UUID.randomUUID().toString()),
                HttpMethod.GET,
                null,
                CourseView.class);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }



    private AddCourseDto getAddCourseDto(){
        return new AddCourseDto("Test", "Test", "Test", 12.99,
                "Test", "Test", false, "User");
    }

    private UpdateCourseDto getUpdateCourseDto(UUID courseId){
        return new UpdateCourseDto(courseId,"Test", "Test", "Test", 13.99,
                "Test", "Test", false, "User");
    }
}