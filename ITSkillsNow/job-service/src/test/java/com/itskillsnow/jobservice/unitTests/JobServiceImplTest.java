package com.itskillsnow.jobservice.unitTests;

import com.itskillsnow.jobservice.dto.request.job.AddJobDto;
import com.itskillsnow.jobservice.dto.request.job.UpdateJobDto;
import com.itskillsnow.jobservice.dto.response.JobView;
import com.itskillsnow.jobservice.exception.UserNotFoundException;
import com.itskillsnow.jobservice.model.Job;
import com.itskillsnow.jobservice.model.User;
import com.itskillsnow.jobservice.repository.JobRepository;
import com.itskillsnow.jobservice.repository.UserRepository;
import com.itskillsnow.jobservice.service.JobServiceImpl;
import com.itskillsnow.jobservice.service.interfaces.BlobService;
import com.itskillsnow.jobservice.service.interfaces.JobService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {


    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BlobService blobService;

    private JobService jobService;



    @BeforeEach
    void setUp() {
        jobService = new JobServiceImpl(jobRepository, userRepository, blobService);
    }

    @Test
    public void given_addJob_withCorrectData_returnsTrue() {
        // Prepare test data
        AddJobDto jobDto = new AddJobDto();
        jobDto.setJobName("Test Job");
        jobDto.setJobDescription("This is a test Job");
        jobDto.setUsername("Username");

        User user = new User("Username");

        when(userRepository.findByUsername("Username")).thenReturn(Optional.of(user));
        when(jobRepository.save(any(Job.class))).thenReturn(new Job());

        // Call the addJob function
        boolean result = jobService.addJob(jobDto);

        // Verify the result
        assertTrue(result);

        // Verify that the Job was saved
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    public void given_addJob_withWrongData_returnsFalse() {
        // Prepare test data
        AddJobDto jobDto = new AddJobDto();
        jobDto.setJobName("Test Job");
        jobDto.setJobDescription("This is a test Job");
        jobDto.setUsername("Username");

        when(userRepository.findByUsername("Username")).thenReturn(Optional.empty());

        // Call the addJob function
        boolean result = jobService.addJob(jobDto);

        // Verify the result
        assertFalse(result);

        // Verify that the job was not saved
        verify(jobRepository, never()).save(any(Job.class));
    }

    @Test
    public void given_updateJob_withCorrectData_returnsTrue() {
        // Prepare test data
        UUID jobId = UUID.randomUUID();
        UpdateJobDto jobDto = new UpdateJobDto();
        jobDto.setJobId(jobId);
        jobDto.setJobName("New Job Name");
        jobDto.setJobDescription("New Job description");

        Job existingJob = new Job();
        existingJob.setJobId(jobId);
        existingJob.setJobName("Old Job Name");
        existingJob.setJobDescription("Old Job description");

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(existingJob));
        when(jobRepository.save(any(Job.class))).thenReturn(new Job());

        // Call the updateJob function
        boolean result = jobService.updateJob(jobDto);

        // Verify the result
        assertTrue(result);

        // Verify that the Job was updated
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    public void given_updateJob_withWrongData_returnsFalse() {
        // Prepare test data
        UUID jobId = UUID.randomUUID();
        UpdateJobDto jobDto = new UpdateJobDto();
        jobDto.setJobId(jobId);
        jobDto.setJobName("New Job Name");
        jobDto.setJobDescription("New Job description");

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        // Call the updateJob function
        boolean result = jobService.updateJob(jobDto);

        // Verify the result
        assertFalse(result);

        // Verify that the Job was not updated
        verify(jobRepository, never()).save(any(Job.class));
    }

    @Test
    public void given_deleteJob_withCorrectData_returnsTrue() {
        // Prepare test data
        UUID jobId = UUID.randomUUID();
        Job job = new Job();
        job.setJobId(jobId);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        // Call the deleteJob function
        boolean result = jobService.deleteJob(jobId);

        // Verify the result
        assertTrue(result);

        // Verify that the Job was deleted
        verify(jobRepository, times(1)).delete(job);
    }

    @Test
    public void given_deleteJob_withWrongData_returnsFalse() {
        // Prepare test data
        UUID jobId = UUID.randomUUID();

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        // Call the deleteJob function
        boolean result = jobService.deleteJob(jobId);

        // Verify the result
        assertFalse(result);

        // Verify that the job was not deleted
        verify(jobRepository, never()).delete(any(Job.class));
    }

    @Test
    public void given_getJobById_withCorrectData_returnsJob() {
        // Prepare test data
        UUID jobId = UUID.randomUUID();
        Job job = new Job();
        job.setJobId(jobId);
        job.setUser(new User("Username"));

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        // Call the getJobById function
        JobView result = jobService.getJobByJobId(jobId);

        // Verify the result
        assertNotNull(result);
        assertEquals(jobId, result.getJobId());

        // Verify that the job was retrieved from the database
        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    public void given_getJobById_withWrongData_returnsNull() {
        // Prepare test data
        UUID jobId = UUID.randomUUID();

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        // Call the getJobById function
        JobView result = jobService.getJobByJobId(jobId);

        // Verify the result
        assertNull(result);

        // Verify that the job was not retrieved from the database
        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    public void given_getAllJobsByUser_withCorrectData_returnsListOfJobs() {
        // Prepare test data
        String username = "Username";
        User user = new User(username);
        Job job1 = new Job();
        job1.setUser(user);
        Job job2 = new Job();
        job2.setUser(user);
        List<Job> jobs = new ArrayList<>();
        jobs.add(job1);
        jobs.add(job2);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jobRepository.findAllByUser(user)).thenReturn(jobs);

        // Call the getAllJobsByUser function
        List<JobView> result = jobService.getAllJobsByUserId(username);

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify that the jobs were retrieved from the database
        verify(userRepository, times(1)).findByUsername(username);
        verify(jobRepository, times(1)).findAllByUser(user);
    }

    @Test
    public void given_getAllJobsByUser_withWrongData_returnsNull() {
        // Prepare test data
        String username = "Username";
        String expected = "User was not found!";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Call the getAllJobsByUser function
        UserNotFoundException actual = Assertions.assertThrows(UserNotFoundException.class, () ->
                jobService.getAllJobsByUserId(username)
        );

        // Verify the result
        assertEquals(expected, actual.getMessage());

        // Verify that the user was not retrieved from the database
        verify(userRepository, times(1)).findByUsername(username);
        // Verify that the jobs were not retrieved from the database
        verify(jobRepository, never()).findAllByUser(any());
    }

    @Test
    void given_getAllJobs_returnsAllJobs(){
        User user = new User("test");
        Job job = Job.builder()
                .jobId(UUID.randomUUID())
                .jobName("Test")
                .user(user)
                .build();
        when(jobRepository.findAll()).thenReturn(List.of(job));

        List<JobView> jobs = jobService.getAllJobs();
        int expected = 1;

        assertEquals(jobs.size(), expected);
    }
}