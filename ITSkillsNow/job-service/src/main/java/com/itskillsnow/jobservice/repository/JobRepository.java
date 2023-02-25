package com.itskillsnow.jobservice.repository;

import com.itskillsnow.jobservice.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
}
