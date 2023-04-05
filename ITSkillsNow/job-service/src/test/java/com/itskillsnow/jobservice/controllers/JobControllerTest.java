package com.itskillsnow.jobservice.controllers;

import com.itskillsnow.jobservice.dto.request.AddJobDto;
import com.itskillsnow.jobservice.dto.response.JobView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobControllerTest {

   @LocalServerPort
   private int port;

   private String baseUrl = "http://localhost";

   private static RestTemplate restTemplate;

   @BeforeAll
   public static void init(){
       restTemplate = new RestTemplate();
   }

   @BeforeEach
   public void setUp(){
       baseUrl = baseUrl.concat(":").concat(port+ "").concat("/api/job");
   }


    @Test
    void testAddAndGetJobs() {
        // Create a new job
        AddJobDto jobDto = new AddJobDto("Test");

        // Send a POST request to add the job
        restTemplate.postForObject(baseUrl, jobDto, AddJobDto.class);

        // Send a GET request to retrieve all jobs
        ResponseEntity<List<JobView>> response = restTemplate.exchange(baseUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<JobView>>() {});

        // Verify that the new job is present in the response
        boolean jobAdded = Objects.requireNonNull(response.getBody()).stream()
                .anyMatch(job -> job.getJobName().equals(jobDto.getJobName()));
        assertTrue(jobAdded);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}