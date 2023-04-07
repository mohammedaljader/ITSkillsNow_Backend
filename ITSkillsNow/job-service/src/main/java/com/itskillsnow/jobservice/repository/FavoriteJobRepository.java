package com.itskillsnow.jobservice.repository;

import com.itskillsnow.jobservice.model.FavoriteJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FavoriteJobRepository extends JpaRepository<FavoriteJob, UUID> {
}
