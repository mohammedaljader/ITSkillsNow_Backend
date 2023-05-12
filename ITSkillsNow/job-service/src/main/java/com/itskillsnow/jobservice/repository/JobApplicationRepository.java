package com.itskillsnow.jobservice.repository;

import com.itskillsnow.jobservice.model.Job;
import com.itskillsnow.jobservice.model.JobApplication;
import com.itskillsnow.jobservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {
    List<JobApplication> findAllByUser(User user);
    List<JobApplication> findAllByJob(Job job);
}
