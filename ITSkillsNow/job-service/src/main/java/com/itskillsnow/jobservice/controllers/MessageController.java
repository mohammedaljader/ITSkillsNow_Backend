package com.itskillsnow.jobservice.controllers;

import com.itskillsnow.jobservice.models.Message;
import com.itskillsnow.jobservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository messageRepository;


    @GetMapping("/messages")
    @ResponseStatus(HttpStatus.OK)
    public List<Message> getAllMessages(){
        return messageRepository.findAll();
    }
}
