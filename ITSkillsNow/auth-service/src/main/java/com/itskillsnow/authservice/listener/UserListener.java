package com.itskillsnow.authservice.listener;

import com.itskillsnow.authservice.model.User;
import com.itskillsnow.authservice.event.UserEvent;
import com.itskillsnow.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserListener {


    private final UserRepository userRepository;


    @RabbitListener(queues = "user.queue")
    public void deleteUser(UserEvent event){
        if(event.getEventType().equals("delete")){
            handleUserDelete(event.getUsername());
        }
    }


    private void handleUserDelete(String username){
        Optional<User> user = userRepository.findByUsername(username);
        user.ifPresent(userRepository::delete);
        log.info("User deleted successfully!");
    }
}
