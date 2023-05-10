package com.itskillsnow.courseservice.integrationTests;

import com.itskillsnow.courseservice.dto.request.option.AddOptionDto;
import com.itskillsnow.courseservice.dto.request.option.UpdateOptionDto;
import com.itskillsnow.courseservice.dto.request.question.AddQuestionDto;
import com.itskillsnow.courseservice.dto.request.question.AddQuestionWithOptionDto;
import com.itskillsnow.courseservice.dto.request.question.UpdateQuestionDto;
import com.itskillsnow.courseservice.dto.response.OptionView;
import com.itskillsnow.courseservice.dto.response.QuestionView;
import com.itskillsnow.courseservice.dto.response.QuestionWithoutOptionView;
import com.itskillsnow.courseservice.exception.QuizNotFoundException;
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
public class QuestionControllerIT {
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


    private UUID quizId;


    private static final String blobUrl = "https://example.com/blob/123456";

    @BeforeAll
    public static void init(){
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp(){
        baseUrl = baseUrl.concat(":").concat(port+ "").concat("/api/course/quiz/question");
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

        // Create Quiz
        Quiz quiz = Quiz.builder()
                .quizName("QuizName")
                .course(savedCourse)
                .build();

        Quiz savedQuiz = quizRepository.save(quiz);
        quizId = savedQuiz.getQuizId();


        when(blobService.storeFile(anyString(),
                ArgumentMatchers.any(InputStream.class),
                anyLong())
        ).thenReturn(blobUrl);
    }

    @Test
    void given_addQuestionWithItsOptions_withCorrectData_returnsTrue() {
        String questionName = "questionName";
        AddQuestionDto addQuestionDto = new AddQuestionDto(questionName, quizId);

        AddOptionDto addOption1 = new AddOptionDto("Option1", false, null);
        AddOptionDto addOption2 = new AddOptionDto("Option2", false, null);
        AddOptionDto addOption3 = new AddOptionDto("Option3", true, null);
        AddOptionDto addOption4 = new AddOptionDto("Option4", false, null);

        AddQuestionWithOptionDto request = new AddQuestionWithOptionDto(addQuestionDto, List.of(addOption1, addOption2,
                addOption3, addOption4));

        // Send a POST request
        ResponseEntity<Boolean> result = restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(request), new ParameterizedTypeReference<>() {});


        // Verify
        assertEquals(Boolean.TRUE, result.getBody());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }


    @Test
    void given_addQuestionWithItsOptions_withWrongQuizId_returnsFalse() {
        String questionName = "questionName";
        AddQuestionDto addQuestionDto = new AddQuestionDto(questionName, UUID.randomUUID());

        AddOptionDto addOption1 = new AddOptionDto("Option1", false, null);
        AddOptionDto addOption2 = new AddOptionDto("Option2", false, null);
        AddOptionDto addOption3 = new AddOptionDto("Option3", true, null);
        AddOptionDto addOption4 = new AddOptionDto("Option4", false, null);

        AddQuestionWithOptionDto request = new AddQuestionWithOptionDto(addQuestionDto, List.of(addOption1, addOption2,
                addOption3, addOption4));

        // Send a POST request
        ResponseEntity<Boolean> result = restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(request), new ParameterizedTypeReference<>() {});


        // Verify
        assertEquals(Boolean.FALSE, result.getBody());
    }

    @Test
    void given_addQuestion_withCorrectData_returnsSavedQuestion() {
        String questionName = "questionName";
        AddQuestionDto request = new AddQuestionDto(questionName, quizId);


        // Send a POST request
        ResponseEntity<QuestionWithoutOptionView> response = restTemplate.exchange(baseUrl.concat("/new"), HttpMethod.POST,
                new HttpEntity<>(request), new ParameterizedTypeReference<>() {});


        // Verify
        assertEquals(questionName, Objects.requireNonNull(response.getBody()).getQuestionName());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void given_addQuestion_withWrongQuizId_returnsException() {
        String questionName = "questionName";
        AddQuestionDto request = new AddQuestionDto(questionName, UUID.randomUUID());


        // Send a POST request
        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl.concat("/new"), HttpMethod.POST,
                                new HttpEntity<>(request), new ParameterizedTypeReference<>() {})
        );

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_updateQuestion_withCorrectData_returnsSavedQuestion() {
        String questionName = "questionName";
        AddQuestionDto postRequest = new AddQuestionDto(questionName, quizId);

        // Send a POST request
        ResponseEntity<QuestionWithoutOptionView> postResponse = restTemplate.exchange(baseUrl.concat("/new"), HttpMethod.POST,
                new HttpEntity<>(postRequest), new ParameterizedTypeReference<>() {});

        UUID questionId = Objects.requireNonNull(postResponse.getBody()).getQuestionId();

        UpdateQuestionDto request = new UpdateQuestionDto(questionId, "newQuestion");

        // Send a PUT request
        ResponseEntity<QuestionWithoutOptionView> response = restTemplate.exchange(baseUrl, HttpMethod.PUT,
                new HttpEntity<>(request), new ParameterizedTypeReference<>() {});


        // Verify
        assertNotEquals(questionName, Objects.requireNonNull(response.getBody()).getQuestionName());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void given_updateQuestion_withWrongQuestionId_returnsException() {
        UpdateQuestionDto request = new UpdateQuestionDto(UUID.randomUUID(), "newQuestion");

        // Send a PUT request
        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl, HttpMethod.PUT,
                                new HttpEntity<>(request), new ParameterizedTypeReference<>() {})
        );

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_deleteQuestion_withCorrectData_returnsTrue() {
        String questionName = "questionName";
        AddQuestionDto postRequest = new AddQuestionDto(questionName, quizId);

        // Send a POST request
        ResponseEntity<QuestionWithoutOptionView> postResponse = restTemplate.exchange(baseUrl.concat("/new"), HttpMethod.POST,
                new HttpEntity<>(postRequest), new ParameterizedTypeReference<>() {});

        UUID questionId = Objects.requireNonNull(postResponse.getBody()).getQuestionId();


        // Send a DELETE request
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl.concat("/")
                        .concat(questionId.toString())
                , HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {});


        // Verify
        assertEquals(Boolean.TRUE, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void given_deleteQuestion_withWrongQuestionId_returnsFalse() {
        // Send a DELETE request
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl.concat("/")
                        .concat(UUID.randomUUID().toString())
                , HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {});


        // Verify
        assertEquals(Boolean.FALSE, response.getBody());
    }


    @Test
    void given_addOption_withCorrectData_returnsSavedOption() {
        UUID questionId = addQuestionToRepository();
        String optionName = "optionName";
        AddOptionDto request = new AddOptionDto(optionName,false,questionId);


        // Send a POST request
        ResponseEntity<OptionView> response = restTemplate.exchange(baseUrl.concat("/option"), HttpMethod.POST,
                new HttpEntity<>(request), new ParameterizedTypeReference<>() {});


        // Verify
        assertEquals(optionName, Objects.requireNonNull(response.getBody()).getOptionName());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }


    @Test
    void given_addOption_withWithWrongQuestionId_returnsException() {
        UUID questionId = UUID.randomUUID();
        String optionName = "optionName";
        AddOptionDto request = new AddOptionDto(optionName,false,questionId);


        // Send a POST request
        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl.concat("/option"), HttpMethod.POST,
                                new HttpEntity<>(request), new ParameterizedTypeReference<>() {})
        );
        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_updateOption_withCorrectData_returnsSavedOption() {
        UUID questionId = addQuestionToRepository();
        String optionName = "optionName";
        AddOptionDto postRequest = new AddOptionDto(optionName,false,questionId);

        // Send a POST request
        ResponseEntity<OptionView> postResponse = restTemplate.exchange(baseUrl.concat("/option"), HttpMethod.POST,
                new HttpEntity<>(postRequest), new ParameterizedTypeReference<>() {});

        UUID optionId = Objects.requireNonNull(postResponse.getBody()).getOptionId();
        UpdateOptionDto request = new UpdateOptionDto(optionId, "newOption", true);

        // Send a PUT request
        ResponseEntity<OptionView> response = restTemplate.exchange(baseUrl.concat("/option"), HttpMethod.PUT,
                new HttpEntity<>(request), new ParameterizedTypeReference<>() {});


        // Verify
        assertNotEquals(optionName, Objects.requireNonNull(response.getBody()).getOptionName());
        assertNotEquals(optionName, Objects.requireNonNull(response.getBody()).getOptionName());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void given_updateOption_withWrongOptionId_returnsException() {
        UUID optionId = UUID.randomUUID();
        UpdateOptionDto request = new UpdateOptionDto(optionId, "newOption", true);

        // Send a PUT request
        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl.concat("/option"), HttpMethod.PUT,
                                new HttpEntity<>(request), new ParameterizedTypeReference<>() {})
        );
        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void given_deleteOption_withCorrectData_returnsTrue() {
        UUID questionId = addQuestionToRepository();
        String optionName = "optionName";
        AddOptionDto postRequest = new AddOptionDto(optionName,false,questionId);

        // Send a POST request
        ResponseEntity<OptionView> postResponse = restTemplate.exchange(baseUrl.concat("/option"), HttpMethod.POST,
                new HttpEntity<>(postRequest), new ParameterizedTypeReference<>() {});

        UUID optionId = Objects.requireNonNull(postResponse.getBody()).getOptionId();

        // Send a DELETE request
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl.concat("/option/")
                        .concat(optionId.toString())
                , HttpMethod.DELETE,
               null, new ParameterizedTypeReference<>() {});


        // Verify
        assertEquals(Boolean.TRUE, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void given_deleteOption_withWrongOptionId_returnsFalse() {
        UUID optionId = UUID.randomUUID();

        // Send a DELETE request
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl.concat("/option/")
                        .concat(optionId.toString())
                , HttpMethod.DELETE,
                null, new ParameterizedTypeReference<>() {});


        // Verify
        assertEquals(Boolean.FALSE, response.getBody());
    }


    @Test
    void given_getAllOptionsByQuestionId_withCorrectData_returnsOptions() {
        UUID questionId = addQuestionToRepository();

        // Send a GET request
        ResponseEntity<List<Option>> response = restTemplate.exchange(baseUrl.concat("/option/")
                        .concat(questionId.toString())
                , HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        // Verify
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void given_getAllQuestionByQuizId_withCorrectData_returnsQuestions() {
        String questionName = "questionName";
        AddQuestionDto addQuestionDto = new AddQuestionDto(questionName, quizId);

        AddOptionDto addOption1 = new AddOptionDto("Option1", false, null);
        AddOptionDto addOption2 = new AddOptionDto("Option2", false, null);
        AddOptionDto addOption3 = new AddOptionDto("Option3", true, null);
        AddOptionDto addOption4 = new AddOptionDto("Option4", false, null);

        AddQuestionWithOptionDto request = new AddQuestionWithOptionDto(addQuestionDto, List.of(addOption1, addOption2,
                addOption3, addOption4));

        // Send a POST request
        restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(request), new ParameterizedTypeReference<>() {});


        // Send a GET request to check if question and its options are added to the database
        ResponseEntity<List<QuestionView>> getResult = restTemplate.exchange(baseUrl.concat("/").concat(quizId.toString()), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});



        // Verify
        assertEquals(questionName, Objects.requireNonNull(getResult.getBody()).get(0).getQuestionName());
        assertEquals(4, Objects.requireNonNull(getResult.getBody()).get(0).getQuestionOptions().size());
        assertEquals(HttpStatus.OK, getResult.getStatusCode());
    }

    private UUID addQuestionToRepository(){
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizNotFoundException("Quiz was not found!"));
        Question question = Question.builder().questionName("QuestionName").quiz(quiz).build();
        Question savedQuestion = questionRepository.save(question);

        Option option = new Option();
        option.setOptionName("new");
        option.setOptionIsCorrect(true);
        option.setQuestion(question);
        optionRepository.save(option);
        return savedQuestion.getQuestionId();
    }
}
