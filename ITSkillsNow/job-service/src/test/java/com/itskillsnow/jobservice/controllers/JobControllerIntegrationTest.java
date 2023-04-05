package com.itskillsnow.jobservice.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.itskillsnow.jobservice.dto.request.AddJobDto;
import com.itskillsnow.jobservice.dto.response.JobView;
import com.itskillsnow.jobservice.service.interfaces.JobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.fasterxml.jackson.core.type.TypeReference;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JobControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private JobService jobService;


    @Test
    public void testAddJob() throws Exception {
        AddJobDto jobDto = new AddJobDto("Test");

        doNothing().when(jobService).addJob(jobDto);


        mockMvc.perform(post("/api/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(jobDto)))
                .andExpect(status().isCreated());

        verify(jobService, times(1)).addJob(jobDto);
    }


    @Test
    public void testGetAllJobs() throws Exception{
        // Create some sample JobView objects to return from the JobService
        UUID jobId1 = UUID.randomUUID();
        UUID jobId2 = UUID.randomUUID();
        List<JobView> jobViews = new ArrayList<>();
        jobViews.add(new JobView(jobId1, "Job 1"));
        jobViews.add(new JobView(jobId2, "Job 2"));

        // Mock the behavior of the JobService to return the sample JobViews
        when(jobService.getAllJobs()).thenReturn(jobViews);

        MvcResult result = mockMvc.perform(get("/api/job"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].jobId", is(jobId1.toString())))
                .andExpect(jsonPath("$[0].jobName", is("Job 1")))
                .andExpect(jsonPath("$[1].jobId", is(jobId2.toString())))
                .andExpect(jsonPath("$[1].jobName", is("Job 2")))
                .andReturn();

        // Verify that the JobService was called exactly once
        verify(jobService, times(1)).getAllJobs();


        // Extract the response content as a String
        String responseContent = result.getResponse().getContentAsString();

        // Parse the response content as a List of JobView objects
        ObjectMapper objectMapper = new ObjectMapper();
        List<JobView> actualJobViews = objectMapper.readValue(responseContent, new TypeReference<>() {});

        // Verify that the actual JobViews match the expected JobViews
        assertEquals(jobViews, actualJobViews);
    }
}
