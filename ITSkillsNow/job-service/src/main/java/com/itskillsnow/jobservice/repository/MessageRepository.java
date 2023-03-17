package com.itskillsnow.jobservice.repository;

import com.itskillsnow.jobservice.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {
}
