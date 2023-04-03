package com.itskillsnow.userservice.listener;

import com.itskillsnow.userservice.event.AuthEvent;
import com.itskillsnow.userservice.event.UserEvent;
import com.itskillsnow.userservice.event.UserPayload;
import com.itskillsnow.userservice.models.User;
import com.itskillsnow.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthListener {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "auth.queue")
    public void createUser(AuthEvent event){

        UserPayload payload = event.getUserPayload();

        User user = new User(payload.getUserId(), payload.getUsername(),
                payload.getFullName(), payload.getEmail(), null);

        User userSaved = userRepository.save(user);
        log.info("User sent successfully!");
        UserEvent CreatedUserEvent = new UserEvent(userSaved.getUserId(), "create");
        rabbitTemplate.convertAndSend("user.exchange", "user.create", CreatedUserEvent);
        log.info("User info sent to all services.");
    }
}
