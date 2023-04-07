package com.itskillsnow.jobservice.repository;

import com.itskillsnow.jobservice.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {
}
