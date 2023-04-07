package com.itskillsnow.jobservice.listener;

import com.itskillsnow.jobservice.event.UserEvent;
import com.itskillsnow.jobservice.model.User;
import com.itskillsnow.jobservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserListener {


    private final UserRepository userRepository;


    @RabbitListener(queues = "user.queue")
    public void userEventListener(UserEvent event){
        if(event.getEventType().equals("delete")){
            handleUserDelete(event.getUsername());
        }

        if(event.getEventType().equals("create")){
            handleUserCreate(event.getUsername());
        }
    }


    private void handleUserDelete(String username){
        Optional<User> user = userRepository.findByUsername(username);
        user.ifPresent(userRepository::delete);
        log.info("User:JobService deleted successfully!");
    }

    private void handleUserCreate(String username){
        userRepository.save(new User(username));
        log.info("User:JobService created successfully!");
    }
}
