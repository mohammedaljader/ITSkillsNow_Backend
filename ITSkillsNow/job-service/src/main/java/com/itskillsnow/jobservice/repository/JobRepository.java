package com.itskillsnow.jobservice.repository;

import com.itskillsnow.jobservice.model.Job;
import com.itskillsnow.jobservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findAllByUser(User user);
}
