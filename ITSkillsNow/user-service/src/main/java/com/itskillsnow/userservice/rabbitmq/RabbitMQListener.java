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

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQListener {

    private final UserRepository userRepository;
    private final RabbitMQSender rabbitMQSender;

    @RabbitListener(queues = "auth.queue")
    public void createUser(AuthEvent event){
        UserPayload payload = event.getUserPayload();

        if(event.getEventType().equals("create")){
            User user = new User(UUID.fromString(payload.getUserId()), payload.getUsername(),
                    payload.getFullName(), payload.getEmail(), null);
            createUserEvent(user);
        }

        if(event.getEventType().equals("delete")){
            deleteUserEvent(payload.getUsername());
        }
    }


    private void createUserEvent(User user){
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

    private void deleteUserEvent(String username){
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            log.info("User is Empty! cannot delete it");
            return;
        }

        userRepository.delete(user.get());

        log.info("User sent successfully!");
        UserEvent deleteUserEvent = new UserEvent(username, "delete");
        try {
            rabbitMQSender.sendMessage("user.exchange",
                    "user.delete",
                    deleteUserEvent);
        }catch (Exception exception){
            log.info(exception.getMessage());
        }
    }
}
