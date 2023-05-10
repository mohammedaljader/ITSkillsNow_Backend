package com.itskillsnow.courseservice.integrationTests;

import com.itskillsnow.courseservice.dto.request.quizResult.GetUserResultsDto;
import com.itskillsnow.courseservice.dto.request.quizResult.SubmitQuizDto;
import com.itskillsnow.courseservice.dto.request.quizResult.UserAnswersDto;
import com.itskillsnow.courseservice.dto.response.QuizResultView;
import com.itskillsnow.courseservice.exception.CourseNotFoundException;
import com.itskillsnow.courseservice.model.*;
import com.itskillsnow.courseservice.repository.*;
import com.itskillsnow.courseservice.service.interfaces.BlobService;
import org.junit.jupiter.api.*;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuizUserControllerIT {
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
        baseUrl = baseUrl.concat(":").concat(port+ "").concat("/api/course/quizResult");
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

    @AfterEach
    public void tearDown() {
        // Clean up databases
        userRepository.deleteAll();
        courseRepository.deleteAll();
        quizRepository.deleteAll();
        questionRepository.deleteAll();
        optionRepository.deleteAll();
    }
    

    @Test
    void given_submitQuiz_withCorrectData_returnsResults() {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course was not found!"));

        Quiz quiz = quizRepository.save(Quiz.builder().quizName("QuizName").course(course).build());

        List<Question> questions = addQuestions(quiz);
        List<Option> selectedOptions = addAndGetOptions(questions);
        List<UserAnswersDto> userAnswers = getUserAnswers(selectedOptions, questions);

        SubmitQuizDto request = new SubmitQuizDto(quiz.getQuizId(), username, userAnswers);

        // Send a POST request
        ResponseEntity<QuizResultView> response = restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(request), new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(3, Objects.requireNonNull(response.getBody()).getResult());
        assertEquals(4, Objects.requireNonNull(response.getBody()).getQuestionsSize());
        assertEquals(75, Objects.requireNonNull(response.getBody()).getGrade());
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }


    @Test
    void given_submitQuiz_withWrongUsername_returnsException() {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course was not found!"));

        Quiz quiz = quizRepository.save(Quiz.builder().quizName("QuizName").course(course).build());

        List<Question> questions = addQuestions(quiz);
        List<Option> selectedOptions = addAndGetOptions(questions);
        List<UserAnswersDto> userAnswers = getUserAnswers(selectedOptions, questions);

        SubmitQuizDto request = new SubmitQuizDto(quiz.getQuizId(), "WrongUsername", userAnswers);

        // Send a POST request
        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl, HttpMethod.POST,
                                new HttpEntity<>(request), new ParameterizedTypeReference<>() {})
        );

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }


    @Test
    void given_submitQuiz_withWrongQuizId_returnsException() {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course was not found!"));

        Quiz quiz = quizRepository.save(Quiz.builder().quizName("QuizName").course(course).build());

        List<Question> questions = addQuestions(quiz);
        List<Option> selectedOptions = addAndGetOptions(questions);
        List<UserAnswersDto> userAnswers = getUserAnswers(selectedOptions, questions);

        SubmitQuizDto request = new SubmitQuizDto(UUID.randomUUID(), username, userAnswers);

        // Send a POST request
        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl, HttpMethod.POST,
                                new HttpEntity<>(request), new ParameterizedTypeReference<>() {})
        );

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }


    @Test
    void given_getQuizResultByQuizIdAndUsername_returnsResult() {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course was not found!"));

        Quiz quiz = quizRepository.save(Quiz.builder().quizName("QuizName").course(course).build());

        List<Question> questions = addQuestions(quiz);
        List<Option> selectedOptions = addAndGetOptions(questions);
        List<UserAnswersDto> userAnswers = getUserAnswers(selectedOptions, questions);

        SubmitQuizDto postRequest = new SubmitQuizDto(quiz.getQuizId(), username, userAnswers);

        // Send a POST request
        restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(postRequest), new ParameterizedTypeReference<>() {});

        GetUserResultsDto request = new GetUserResultsDto(quiz.getQuizId(), username);

        ResponseEntity<QuizResultView> response = restTemplate.exchange(baseUrl.concat("/get"), HttpMethod.POST,
                new HttpEntity<>(request), new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(3, Objects.requireNonNull(response.getBody()).getResult());
        assertEquals(4, Objects.requireNonNull(response.getBody()).getQuestionsSize());
        assertEquals(75, Objects.requireNonNull(response.getBody()).getGrade());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void given_getAllQuizzesResultByUsername_returnsResult() {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course was not found!"));

        Quiz quiz1 = quizRepository.save(Quiz.builder().quizName("QuizName").course(course).build());
        Quiz quiz2 = quizRepository.save(Quiz.builder().quizName("QuizName").course(course).build());

        List<Question> questions1 = addQuestions(quiz1);
        List<Question> questions2 = addQuestions(quiz2);
        List<Option> selectedOptions1 = addAndGetOptions(questions1);
        List<Option> selectedOptions2 = addAndGetOptions(questions2);
        List<UserAnswersDto> userAnswers1 = getUserAnswers(selectedOptions1, questions1);
        List<UserAnswersDto> userAnswers2 = getUserAnswers(selectedOptions2, questions2);

        SubmitQuizDto postRequest1 = new SubmitQuizDto(quiz1.getQuizId(), username, userAnswers1);
        SubmitQuizDto postRequest2 = new SubmitQuizDto(quiz2.getQuizId(), username, userAnswers2);

        // Send a POST request
        restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(postRequest1), new ParameterizedTypeReference<>() {});

        restTemplate.exchange(baseUrl, HttpMethod.POST,
                new HttpEntity<>(postRequest2), new ParameterizedTypeReference<>() {});


        ResponseEntity<List<QuizResultView>> response = restTemplate.exchange(baseUrl.concat("/")
                        .concat(username), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private List<Question> addQuestions(Quiz quiz){

        Question question1 = questionRepository.save(Question.builder().questionName("Question1").quiz(quiz).build());
        Question question2 = questionRepository.save(Question.builder().questionName("Question2").quiz(quiz).build());
        Question question3 = questionRepository.save(Question.builder().questionName("Question3").quiz(quiz).build());
        Question question4 = questionRepository.save(Question.builder().questionName("Question4").quiz(quiz).build());

        return List.of(question1, question2, question3, question4);
    }

    private List<Option> addAndGetOptions(List<Question> questions){
        Option option1Q1 = optionRepository.save(Option.builder().optionName("Option1")
                .optionIsCorrect(false).question(questions.get(0)).build());
        optionRepository.save(Option.builder().optionName("Option2").optionIsCorrect(true)
                .question(questions.get(0)).build());

        Option option1Q2 = optionRepository.save(Option.builder().optionName("Option1")
                .optionIsCorrect(true).question(questions.get(1)).build());
        optionRepository.save(Option.builder().optionName("Option2")
                .optionIsCorrect(false).question(questions.get(1)).build());

        Option option1Q3 = optionRepository.save(Option.builder().optionName("Option1")
                .optionIsCorrect(true).question(questions.get(2)).build());
        optionRepository.save(Option.builder().optionName("Option2")
                .optionIsCorrect(false).question(questions.get(2)).build());

        Option option1Q4 = optionRepository.save(Option.builder().optionName("Option1")
                .optionIsCorrect(true).question(questions.get(3)).build());
        optionRepository.save(Option.builder().optionName("Option2")
                .optionIsCorrect(false).question(questions.get(3)).build());

        return List.of(option1Q1, option1Q2, option1Q3, option1Q4);
    }

    private List<UserAnswersDto> getUserAnswers(List<Option> options, List<Question> questions){
        UserAnswersDto answer1 = new UserAnswersDto(questions.get(0).getQuestionId(), options.get(0).getOptionId());
        UserAnswersDto answer2 = new UserAnswersDto(questions.get(1).getQuestionId(), options.get(1).getOptionId());
        UserAnswersDto answer3 = new UserAnswersDto(questions.get(2).getQuestionId(), options.get(2).getOptionId());
        UserAnswersDto answer4 = new UserAnswersDto(questions.get(3).getQuestionId(), options.get(3).getOptionId());

        return List.of(answer1, answer2, answer3, answer4);
    }
}
