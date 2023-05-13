package com.itskillsnow.courseservice.integrationTests;

import com.itskillsnow.courseservice.dto.request.quiz.AddQuizDto;
import com.itskillsnow.courseservice.dto.request.quiz.UpdateQuizDto;
import com.itskillsnow.courseservice.dto.response.QuizView;
import com.itskillsnow.courseservice.dto.response.QuizWithoutQuestionView;
import com.itskillsnow.courseservice.exception.CourseNotFoundException;
import com.itskillsnow.courseservice.model.*;
import com.itskillsnow.courseservice.repository.*;
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
public class QuizControllerIT {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";


    @MockBean
    private BlobService blobService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

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
        baseUrl = baseUrl.concat(":").concat(port+ "").concat("/api/course/quiz");
        //Create User
        User savedUser = userRepository.save(new User(username));
        //Create Course
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
    void given_addQuiz_withCorrectData_returnsSavedQuiz() {
        String quizName = "QuizName";
        AddQuizDto addQuizDto = new AddQuizDto(quizName, courseId);

        // Send a POST request
        ResponseEntity<QuizWithoutQuestionView> result = restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addQuizDto), new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(quizName, Objects.requireNonNull(result.getBody()).getQuizName());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void given_addQuiz_withWithWrongCourseId_returnsException() {
        String quizName = "QuizName";
        AddQuizDto addQuizDto = new AddQuizDto(quizName, UUID.randomUUID());

        // Send a POST request
        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl, HttpMethod.POST,
                                new HttpEntity<>(addQuizDto), new ParameterizedTypeReference<>() {})
        );

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void given_updateQuiz_withCorrectData_returnsSavedQuiz() {
        String quizName = "QuizName";
        AddQuizDto addQuizDto = new AddQuizDto(quizName, courseId);

        // Send a POST request
        ResponseEntity<QuizWithoutQuestionView> postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addQuizDto), new ParameterizedTypeReference<>() {});

        UUID quizId = Objects.requireNonNull(postResponse.getBody()).getQuizId();
        UpdateQuizDto updateQuizDto = new UpdateQuizDto(quizId, "newQuizName");

        // Send a PUT request
        ResponseEntity<QuizWithoutQuestionView> result = restTemplate.exchange(baseUrl,
                HttpMethod.PUT, new HttpEntity<>(updateQuizDto), new ParameterizedTypeReference<>() {});

        // Verify
        assertNotEquals(quizName, Objects.requireNonNull(result.getBody()).getQuizName());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void given_updateQuiz_withWrongQuizId_returnsException() {
        UpdateQuizDto updateQuizDto = new UpdateQuizDto(UUID.randomUUID(), "newQuizName");

        // Send a PUT request
        HttpClientErrorException.BadRequest exception = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl,
                                HttpMethod.PUT, new HttpEntity<>(updateQuizDto), new ParameterizedTypeReference<>() {})
        );

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void given_deleteQuiz_withCorrectData_returnsTrue() {
        String quizName = "QuizName";
        AddQuizDto addQuizDto = new AddQuizDto(quizName, courseId);

        // Send a POST request
        ResponseEntity<QuizWithoutQuestionView> postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addQuizDto), new ParameterizedTypeReference<>() {});

        UUID quizId = Objects.requireNonNull(postResponse.getBody()).getQuizId();

        // Send a PUT request
        ResponseEntity<Boolean> result = restTemplate.exchange(baseUrl.concat("/").concat(quizId.toString()),
                HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(Boolean.TRUE, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void given_deleteQuiz_withWrongQuizId_returnsFalse() {
        // Send a DELETE request
        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl.concat("/").concat(UUID.randomUUID().toString()),
                                HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {})
        );

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_getAllQuizzesByCourse_withCorrectData_returnsQuizzes() {

        // Get Course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course was not found!"));

        // Add Quiz
        Quiz quiz = Quiz.builder()
                .quizName("quizName")
                .course(course)
                .build();
        Quiz savedQuiz = quizRepository.save(quiz);

        // Add Question
        Question question = Question.builder()
                .questionName("questionName")
                .quiz(savedQuiz)
                .build();

        Question savedQuestion = questionRepository.save(question);

        // Add option
        Option option = Option.builder()
                .optionName("optionName")
                .optionIsCorrect(true)
                .question(savedQuestion)
                .build();

        optionRepository.save(option);


        // Send a GET request
        ResponseEntity<List<QuizView>> result = restTemplate.exchange(baseUrl.concat("/")
                        .concat(courseId.toString()), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        // Verify
        assertEquals("quizName", Objects.requireNonNull(result.getBody()).get(0).getQuizName());
        assertEquals("questionName", Objects.requireNonNull(result.getBody()).get(0)
                .getQuestions().get(0).getQuestionName());
        assertEquals("optionName", Objects.requireNonNull(result.getBody()).get(0).getQuestions().get(0)
                .getQuestionOptions().get(0).getOptionName());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }



    @Test
    void given_getAllQuizzesWithoutQuestionByCourse_withCorrectData_returnsQuizzes() {
        String quizName = "QuizName";
        AddQuizDto addQuizDto = new AddQuizDto(quizName, courseId);

        // Send a POST request
        restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(addQuizDto), new ParameterizedTypeReference<>() {});

        // Send a GET request
        ResponseEntity<List<QuizWithoutQuestionView>> result = restTemplate.exchange(baseUrl.concat("/")
                        .concat(courseId.toString()), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        // Verify
        assertEquals("QuizName", Objects.requireNonNull(result.getBody()).get(0).getQuizName());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
