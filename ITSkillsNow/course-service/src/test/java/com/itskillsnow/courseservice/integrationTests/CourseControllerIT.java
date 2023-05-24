package com.itskillsnow.courseservice.integrationTests;

import com.itskillsnow.courseservice.dto.request.course.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.model.User;
import com.itskillsnow.courseservice.repository.UserRepository;
import com.itskillsnow.courseservice.service.interfaces.BlobService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseControllerIT {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    @MockBean
    private BlobService blobService;

    @Autowired
    private UserRepository userRepository;

    private static RestTemplate restTemplate;

    private static final String blobUrl = "https://example.com/blob/123456";
    private static final String username = "User";

    @BeforeAll
    public static void init(){
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp(){
        baseUrl = baseUrl.concat(":").concat(port+ "").concat("/api/course");
        //Create User
        userRepository.save(new User(username));

        when(blobService.storeFile(anyString(),
                ArgumentMatchers.any(InputStream.class),
                anyLong())
        ).thenReturn(blobUrl);
    }

    @Test
    void given_addCourse_withCorrectData_returnsTrue() {

        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();

        // Send a POST request to add the course
        CourseView result = restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto, CourseView.class);

        // Send a GET request to retrieve all courses
        ResponseEntity<List<CourseView>> response = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify that the new courses is present in the response
        boolean coursesAdded = Objects.requireNonNull(response.getBody()).stream()
                .anyMatch(course -> course.getCourseName().equals(courseDto.getCourseName()));


        assertNotNull(result);
        assertEquals(result.getCourseName(), courseDto.getCourseName());
        assertEquals(result.getCourseDescription(), courseDto.getCourseDescription());
        assertEquals(result.getCourseImage(), courseDto.getCourseImage());
        assertTrue(coursesAdded);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void given_addCourse_withNoUser_returnsException() {
        AddCourseDto courseDto = getAddCourseDto();
        courseDto.setUsername("");

        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                restTemplate.postForEntity(baseUrl.concat("/withoutImage"), courseDto, CourseView.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    @Test
    void given_updateCourse_withCorrectData_returnsTrue(){
        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();
        restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto, CourseView.class);
        // Send a GET request to retrieve all courses
        ResponseEntity<List<CourseView>> courses = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        //Get Course id the response list
        UpdateCourseDto updateCourseDto = getUpdateCourseDto(Objects.requireNonNull(courses.getBody())
                .get(0).getCourseId());


        //Send a PUT request to update course
        ResponseEntity<CourseView> response = restTemplate.exchange(baseUrl.concat("/withoutImage"),
                HttpMethod.PUT,
                new HttpEntity<>(updateCourseDto),
                CourseView.class);

        // Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(13.99, Objects.requireNonNull(response.getBody()).getCoursePrice());
    }

    @Test
    void given_updateCourse_withWrongCourseId_returnException(){
        UpdateCourseDto updateCourseDto = getUpdateCourseDto(UUID.randomUUID());


        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl.concat("/withoutImage"),
                                HttpMethod.PUT,
                                new HttpEntity<>(updateCourseDto),
                                CourseView.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    @Test
    void given_getAllCoursesByUser_withCorrectUsername_returnsAllCoursesOfUser(){
        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();
        restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto, CourseView.class);

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
        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(HttpClientErrorException.BadRequest.class, () ->
                restTemplate.exchange(baseUrl
                                .concat("/user/").concat("Test"),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {})
        );


        // Verify the response
        assertEquals(exception.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void given_deleteCourse_withCorrectCourseId_returnsTrue(){
        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();
        restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto, CourseView.class);
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
        restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto, CourseView.class);
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
        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(HttpClientErrorException.BadRequest.class, () ->
                restTemplate.exchange(baseUrl.concat("/")
                                .concat(UUID.randomUUID().toString()),
                        HttpMethod.GET,
                        null,
                        CourseView.class)
        );

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    @Test
    void given_filterCoursesWithMinAndMaxPrice_returnFilteredCourses() {

        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();
        AddCourseDto courseDto1 = getAddCourseDto();
        courseDto1.setCoursePrice(20.0);
        AddCourseDto courseDto2 = getAddCourseDto();
        courseDto2.setCoursePrice(30.0);

        // Send a POST request to add the course
        restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto, CourseView.class);
        restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto1, CourseView.class);
        restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto2, CourseView.class);

        Double minPrice = 20.0;
        Double maxPrice = 30.0;


        // Send a GET request to retrieve all courses
        ResponseEntity<List<CourseView>> response = restTemplate.exchange(
                baseUrl + "/filter?minPrice={minPrice}&maxPrice={maxPrice}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                minPrice, maxPrice
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<CourseView> filteredCourses = response.getBody();
        assertNotNull(filteredCourses);
        assertEquals(2, response.getBody().size());
    }

    @Test
    void given_filterCoursesWithMinAndMaxPriceAndTypeAndLanguage_returnFilteredCourses() {

        // Create a new course
        AddCourseDto courseDto = getAddCourseDto();
        AddCourseDto courseDto1 = getAddCourseDto();
        courseDto1.setCoursePrice(20.0);
        courseDto1.setCourseLanguage("English");
        courseDto1.setCourseType("Computer science");
        AddCourseDto courseDto2 = getAddCourseDto();
        courseDto2.setCoursePrice(30.0);
        courseDto2.setCourseLanguage("Arabic");
        courseDto2.setCourseType("Math");
        AddCourseDto courseDto3 = getAddCourseDto();
        courseDto3.setCourseLanguage("French");
        courseDto3.setCourseType("Math");


        // Send a POST request to add the course
        restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto, CourseView.class);
        restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto1, CourseView.class);
        restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto2, CourseView.class);
        restTemplate.postForObject(baseUrl.concat("/withoutImage"), courseDto3, CourseView.class);

        Double minPrice = 20.0;
        Double maxPrice = 30.0;
        String courseType = "Math";
        String courseLanguage = "English";


        // Send a GET request to retrieve all courses
        ResponseEntity<List<CourseView>> response = restTemplate.exchange(
                baseUrl + "/filter?maxPrice={maxPrice}&minPrice={minPrice}&courseType={courseType}&courseLanguage={courseLanguage}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                minPrice, maxPrice, courseType, courseLanguage
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<CourseView> filteredCourses = response.getBody();
        assertNotNull(filteredCourses);
        assertEquals(3, response.getBody().size());
    }



    private AddCourseDto getAddCourseDto(){
        return new AddCourseDto("Test", "Test", "Test", 12.99,
                "Test", "Test", false, username);
    }

    private UpdateCourseDto getUpdateCourseDto(UUID courseId){
        return new UpdateCourseDto(courseId,"Test", "Test", "Test", 13.99,
                "Test", "Test", false, username);
    }
}