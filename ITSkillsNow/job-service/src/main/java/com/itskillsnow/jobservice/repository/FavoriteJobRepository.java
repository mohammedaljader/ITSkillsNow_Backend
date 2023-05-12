package com.itskillsnow.jobservice.repository;

import com.itskillsnow.jobservice.model.FavoriteJob;
import com.itskillsnow.jobservice.model.Job;
import com.itskillsnow.jobservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FavoriteJobRepository extends JpaRepository<FavoriteJob, UUID> {
    List<FavoriteJob> findAllByUser(User user);

    Integer countAllByJob(Job job);
}
