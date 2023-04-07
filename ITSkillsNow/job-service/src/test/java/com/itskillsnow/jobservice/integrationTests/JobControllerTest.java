package com.itskillsnow.jobservice.integrationTests;

import com.itskillsnow.jobservice.dto.request.AddJobDto;
import com.itskillsnow.jobservice.dto.request.UpdateJobDto;
import com.itskillsnow.jobservice.dto.response.JobView;
import com.itskillsnow.jobservice.model.User;
import com.itskillsnow.jobservice.repository.UserRepository;
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
class JobControllerTest {

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
       baseUrl = baseUrl.concat(":").concat(port+ "").concat("/api/job");
       //Create User
       userRepository.save(new User("User"));
    }


    @Test
    void given_addJob_withCorrectData_returnsTrue() {

        // Create a new job
        AddJobDto jobDto = getAddJobDto();

        // Send a POST request to add the job
        Boolean result = restTemplate.postForObject(baseUrl, jobDto, Boolean.class);

        // Send a GET request to retrieve all jobs
        ResponseEntity<List<JobView>> response = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify that the new job is present in the response
        boolean jobAdded = Objects.requireNonNull(response.getBody()).stream()
                .anyMatch(job -> job.getJobName().equals(jobDto.getJobName()));


        assertEquals(Boolean.TRUE, result);
        assertTrue(jobAdded);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void given_addJob_withNoUser_returnsFalse() {
        // Create a new job
        AddJobDto jobDto = getAddJobDto();
        jobDto.setUsername("");

        // Send a POST request to add the job
        Boolean result = restTemplate.postForObject(baseUrl, jobDto, Boolean.class);

        // Send a GET request to retrieve all jobs
        ResponseEntity<List<JobView>> response = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify
        assertEquals(Boolean.FALSE, result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void given_updateJob_withCorrectData_returnsTrue(){
        // Create a new job
        AddJobDto jobDto = getAddJobDto();
        restTemplate.postForObject(baseUrl, jobDto, Boolean.class);
        // Send a GET request to retrieve all jobs
        ResponseEntity<List<JobView>> jobs = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        //Get Job id the response list
        UpdateJobDto updateJobDto = getUpdateJobDto(Objects.requireNonNull(jobs.getBody())
                .get(0).getJobId());


        //Send a PUT request to update job
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(updateJobDto),
                Boolean.class);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
    }

    @Test
    void given_updateJob_withWrongJobId_returnFalse(){
        //Update payload
        UpdateJobDto updateJobDto = getUpdateJobDto(UUID.randomUUID());

        //Send a PUT request to update job
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(updateJobDto),
                Boolean.class);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.FALSE, response.getBody());
    }

    @Test
    void given_getAllJobsByUser_withCorrectUsername_returnsAllJobsOfUser(){
        // Create a new job
        AddJobDto jobDto = getAddJobDto();
        restTemplate.postForObject(baseUrl, jobDto, Boolean.class);

        // Send a GET request to retrieve all jobs by user
        ResponseEntity<List<JobView>> response = restTemplate.exchange(baseUrl.concat("/user/").concat(jobDto.getUsername()),
                HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<JobView> jobViews = response.getBody();
        assert jobViews != null;
        assertEquals(1, jobViews.size());
        assertEquals(jobDto.getJobName(), jobViews.get(0).getJobName());
        assertEquals(jobDto.getJobDescription(), jobViews.get(0).getJobDescription());
    }

    @Test
    void given_getAllJobsByUser_withWrongUsername_returnsNoData(){
        // Send a GET request to retrieve all jobs by user
        ResponseEntity<List<JobView>> response = restTemplate.exchange(baseUrl.concat("/user/").concat("Test"),
                HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<JobView> jobViews = response.getBody();
        assertNull(jobViews);
    }

    @Test
    void given_deleteJob_withCorrectJobId_returnsTrue(){
        // Create a new job
        AddJobDto jobDto = getAddJobDto();
        restTemplate.postForObject(baseUrl, jobDto, Boolean.class);
        // Send a GET request to retrieve all jobs
        ResponseEntity<List<JobView>> jobs = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        //Get Job id the response list
        UUID jobId = Objects.requireNonNull(jobs.getBody()).get(0).getJobId();


        //Send a Delete request to delete job
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl.concat("/").concat(jobId.toString()),
                HttpMethod.DELETE,
                null,
                Boolean.class);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
    }

    @Test
    void given_deleteJob_withWrongJobId_returnsFalse(){
        //Send a Delete request to delete job
        ResponseEntity<Boolean> response = restTemplate.exchange(baseUrl.concat("/")
                        .concat(UUID.randomUUID().toString()),
                HttpMethod.DELETE,
                null,
                Boolean.class);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.FALSE, response.getBody());
    }

    @Test
    void given_getJobById_withCorrectId_returnsJob(){
        // Create a new job
        AddJobDto jobDto = getAddJobDto();
        restTemplate.postForObject(baseUrl, jobDto, Boolean.class);
        // Send a GET request to retrieve all jobs
        ResponseEntity<List<JobView>> jobs = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});


        //Get Job id the response list
        UUID jobId = Objects.requireNonNull(jobs.getBody()).get(0).getJobId();


        //Send a GET request to get job
        ResponseEntity<JobView> response = restTemplate.exchange(baseUrl.concat("/").concat(jobId.toString()),
                HttpMethod.GET,
                null,
                JobView.class);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test", Objects.requireNonNull(response.getBody()).getJobName());
        assertEquals("Test", Objects.requireNonNull(response.getBody()).getJobDescription());
    }

    @Test
    void given_getJobById_withWrongId_returnsNull(){
        //Send a GET request to get job
        ResponseEntity<JobView> response = restTemplate.exchange(baseUrl.concat("/")
                        .concat(UUID.randomUUID().toString()),
                HttpMethod.GET,
                null,
                JobView.class);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    private AddJobDto getAddJobDto(){
        return new AddJobDto("Test", "Test", "Test", "Test",
                "Test", "Test", 9, "User");
    }

    private UpdateJobDto getUpdateJobDto(UUID jobId){
        return new UpdateJobDto(jobId,"Test", "Test", "Test", "Test",
                "Test", "Test", 9, "User");
    }
}