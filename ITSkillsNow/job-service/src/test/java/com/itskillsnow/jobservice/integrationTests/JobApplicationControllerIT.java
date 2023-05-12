package com.itskillsnow.jobservice.integrationTests;

import com.itskillsnow.jobservice.dto.response.JobApplicationView;
import com.itskillsnow.jobservice.exception.JobNotFoundException;
import com.itskillsnow.jobservice.exception.UserNotFoundException;
import com.itskillsnow.jobservice.model.Job;
import com.itskillsnow.jobservice.model.JobApplication;
import com.itskillsnow.jobservice.model.User;
import com.itskillsnow.jobservice.repository.JobApplicationRepository;
import com.itskillsnow.jobservice.repository.JobRepository;
import com.itskillsnow.jobservice.repository.UserRepository;
import com.itskillsnow.jobservice.service.interfaces.BlobService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
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
public class JobApplicationControllerIT {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    @MockBean
    private BlobService blobService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;


    private static RestTemplate restTemplate;

    private static UUID jobId;

    private static final String username = "User";

    private static final String blobUrl = "https://example.com/blob/123456";

    @BeforeAll
    public static void init(){
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp(){
        baseUrl = baseUrl.concat(":").concat(port+ "").concat("/api/jobApplication");
        //Create User
        User user = userRepository.save(new User(username));

        // Create Job
        Job job = Job.builder()
                .jobName("jobName")
                .jobDescription("jobDescription")
                .jobImage("jobImage")
                .jobAddress("jobAddress")
                .jobCategory("")
                .jobEducationLevel("")
                .jobType("")
                .jobHours(40)
                .user(user)
                .build();

        Job savedJob = jobRepository.save(job);
        jobId = savedJob.getJobId();

        when(blobService.storeFile(anyString(),
                ArgumentMatchers.any(InputStream.class),
                anyLong())
        ).thenReturn(blobUrl);
    }

    @AfterEach
    public void tearDown() {
        // Clean up databases
        userRepository.deleteAll();
        jobRepository.deleteAll();
        jobApplicationRepository.deleteAll();
    }


    @Test
    void given_deleteJobApplication_withCorrectData_returnsTrue(){
        //Add job Application to database
        JobApplication jobApplication = addJobApplication();

        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl.concat("/")
                        .concat(jobApplication.getApplicationId().toString()), HttpMethod.DELETE,
                null, new ParameterizedTypeReference<>() {});

        assertEquals(Boolean.TRUE, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void given_deleteJobApplication_withWrongApplicationId_returnsException(){

        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl.concat("/")
                                        .concat(UUID.randomUUID().toString()), HttpMethod.DELETE,
                                null, new ParameterizedTypeReference<>() {})
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void given_getJobApplication_withCorrectData_returnsApplication(){
        //Add job Application to database
        JobApplication jobApplication = addJobApplication();

        ResponseEntity<JobApplicationView> response = restTemplate.exchange(baseUrl.concat("/")
                        .concat(jobApplication.getApplicationId().toString()), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        assertEquals("cv.pdf", Objects.requireNonNull(response.getBody()).getApplicationCv());
        assertEquals("I like this job!", Objects.requireNonNull(response.getBody()).getApplicationMotivation());
        assertEquals("jobName", Objects.requireNonNull(response.getBody()).getJobView().getJobName());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void given_getJobApplication_withWrongApplicationId_returnsException(){

        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl.concat("/")
                                        .concat(UUID.randomUUID().toString()), HttpMethod.GET,
                                null, new ParameterizedTypeReference<>() {})
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_getAllJobApplicationsByUser_withCorrectData_returnsApplications(){
        //Add two job Applications to database
        addJobApplication();
        addJobApplication();

        ResponseEntity<List<JobApplicationView>> response = restTemplate.exchange(baseUrl.concat("/user/")
                        .concat(username), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void  given_getAllJobApplicationsByUser_withWrongUsername_returnsException(){

        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl.concat("/user/")
                                        .concat("wrongUsername"), HttpMethod.GET,
                                null, new ParameterizedTypeReference<>() {})
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_getAllJobApplicationsByJob_withCorrectData_returnsApplications(){
        //Add job Applications to database
        addJobApplication();

        ResponseEntity<List<JobApplicationView>> response = restTemplate.exchange(baseUrl.concat("/job/")
                        .concat(jobId.toString()), HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void  given_getAllJobApplicationsByJob_withWrongJobId_returnsException(){

        HttpClientErrorException.BadRequest response = Assertions.assertThrows(
                HttpClientErrorException.BadRequest.class, () ->
                        restTemplate.exchange(baseUrl.concat("/job/")
                                        .concat(UUID.randomUUID().toString()), HttpMethod.GET,
                                null, new ParameterizedTypeReference<>() {})
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private JobApplication addJobApplication(){
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job was not found!"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));
        JobApplication jobApplication = JobApplication.builder()
                .applicationCv("cv.pdf")
                .applicationMotivation("I like this job!")
                .job(job)
                .user(user)
                .build();

        return jobApplicationRepository.save(jobApplication);
    }
}
