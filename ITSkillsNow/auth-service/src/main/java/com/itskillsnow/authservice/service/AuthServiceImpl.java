package com.itskillsnow.authservice.service;


import com.itskillsnow.authservice.dto.AuthResponse;
import com.itskillsnow.authservice.model.Role;
import com.itskillsnow.authservice.model.User;
import com.itskillsnow.authservice.event.AuthEvent;
import com.itskillsnow.authservice.event.UserPayload;
import com.itskillsnow.authservice.repository.UserRepository;
import com.itskillsnow.authservice.service.ServiceInterfaces.AuthService;
import com.itskillsnow.authservice.service.ServiceInterfaces.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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
            user.setRoles(List.of(Role.USER));
            User userSaved = userRepository.save(user);

            // Send payload to User Service
            try {
                UserPayload payload = new UserPayload(userSaved.getId().toString(), userSaved.getFullName(),
                        userSaved.getUsername(), userSaved.getEmail());

                AuthEvent event = new AuthEvent(payload, "create");
                rabbitTemplate.convertAndSend("auth.exchange", "auth.create", event);
                log.info("User sent successfully!");
            }catch (Exception ex){
                log.error(ex.getMessage());
                log.info("Error while sending user");
            }
            return "user added to the system";
        }
    }

    @Override
    public AuthResponse generateToken(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            return null;
        }
        Map<String, String> tokens = jwtService.generateTokens(user.get(), username);
        return new AuthResponse(tokens.get("accessToken"), tokens.get("refreshToken"));
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.getUsernameFromToken(refreshToken);
        return generateToken(username);
    }

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

}
