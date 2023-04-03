package com.itskillsnow.jobservice.listener;

import com.itskillsnow.jobservice.cofig.RabbitMQConfig;
import com.itskillsnow.jobservice.models.Message;
import com.itskillsnow.jobservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private final MessageRepository messageRepository;


    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void listener(CustomMessage message) {
        messageRepository.save(new Message(message.getMessageId(), message.getMessage(), message.getMessageDate()));
        log.info("Message received successfully!");
    }
}
