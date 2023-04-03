package com.itskillsnow.authservice.service;


import com.itskillsnow.authservice.dto.AuthResponse;
import com.itskillsnow.authservice.entity.Role;
import com.itskillsnow.authservice.entity.User;
import com.itskillsnow.authservice.event.AuthEvent;
import com.itskillsnow.authservice.event.UserPayload;
import com.itskillsnow.authservice.repository.UserRepository;
import com.itskillsnow.authservice.service.ServiceInterfaces.AuthService;
import com.itskillsnow.authservice.service.ServiceInterfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final RabbitTemplate rabbitTemplate;


    @Override
    public String saveUser(User user) {
        boolean existsByEmailOrUsername = userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername());
        if(existsByEmailOrUsername){
            return "Email or username is already taken!";
        }else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.USER);
            User userSaved = userRepository.save(user);
            // Send payload to User Service
            UserPayload payload = new UserPayload(userSaved.getId().toString(), userSaved.getFullName(),
                    userSaved.getUsername(), userSaved.getEmail());
            AuthEvent event = new AuthEvent(payload, "create");
            rabbitTemplate.convertAndSend("auth.exchange", "auth.create", event);
            return "user added to the system";
        }
    }

    @Override
    public AuthResponse generateToken(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            return null;
        }
        return new AuthResponse(jwtService.generateToken(user.get(),username));
    }

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

}
