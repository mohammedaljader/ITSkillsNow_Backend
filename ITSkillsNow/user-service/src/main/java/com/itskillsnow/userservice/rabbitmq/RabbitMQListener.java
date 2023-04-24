package com.itskillsnow.userservice.rabbitmq;

import com.itskillsnow.userservice.event.AuthEvent;
import com.itskillsnow.userservice.event.UserEvent;
import com.itskillsnow.userservice.event.UserPayload;
import com.itskillsnow.userservice.model.User;
import com.itskillsnow.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQListener {

    private final UserRepository userRepository;
    private final RabbitMQSender rabbitMQSender;

    @RabbitListener(queues = "auth.queue")
    public void createUser(AuthEvent event){

        UserPayload payload = event.getUserPayload();

        User user = new User(payload.getUserId(), payload.getUsername(),
                payload.getFullName(), payload.getEmail(), null);

        User userSaved = userRepository.save(user);
        log.info("User sent successfully!");
        UserEvent CreatedUserEvent = new UserEvent(userSaved.getUsername(), "create");
        try {
            rabbitMQSender.sendMessage("user.exchange",
                    "user.create",
                    CreatedUserEvent);
        }catch (Exception exception){
            log.info(exception.getMessage());
        }
    }
}
