package com.itskillsnow.authservice.service;


import com.itskillsnow.authservice.dto.AuthResponse;
import com.itskillsnow.authservice.event.MessageEvent;
import com.itskillsnow.authservice.exception.OtpCodeNotFoundException;
import com.itskillsnow.authservice.exception.UserNotFoundException;
import com.itskillsnow.authservice.model.OTPCode;
import com.itskillsnow.authservice.model.Role;
import com.itskillsnow.authservice.model.User;
import com.itskillsnow.authservice.event.AuthEvent;
import com.itskillsnow.authservice.event.UserPayload;
import com.itskillsnow.authservice.rabbitmq.RabbitMQSender;
import com.itskillsnow.authservice.repository.OTPCodeRepository;
import com.itskillsnow.authservice.repository.UserRepository;
import com.itskillsnow.authservice.service.ServiceInterfaces.AuthService;
import com.itskillsnow.authservice.service.ServiceInterfaces.JwtService;
import com.itskillsnow.authservice.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
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

    private final RabbitMQSender rabbitMQSender;

    private final OTPCodeRepository otpCodeRepository;


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
                rabbitMQSender.sendMessage("auth.exchange", "auth.create", event);
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
        return new AuthResponse(tokens.get("accessToken"),
                tokens.get("refreshToken"), username,
                user.get().getFullName(), user.get().getRoles());
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.getUsernameFromToken(refreshToken);
        return generateToken(username);
    }

    @Override
    public boolean deleteMe(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            return false;
        }

        if(passwordEncoder.matches(password, user.get().getPassword())){
            userRepository.delete(user.get());
            try {
                UserPayload payload = new UserPayload();
                payload.setUsername(user.get().getUsername());

                AuthEvent event = new AuthEvent(payload, "delete");
                rabbitMQSender.sendMessage("auth.exchange", "auth.delete", event);
                log.info("User sent successfully!");
            }catch (Exception ex){
                log.error(ex.getMessage());
                log.info("Error while sending user");
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean addRole(String role, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        List<Role> roles = new ArrayList<>(user.getRoles());

        if (roles.contains(Role.valueOf(role))) {
            return false;
        }

        roles.add(Role.valueOf(role));
        user.setRoles(roles);
        return true;
    }

    @Override
    public String createOtpCode(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        String generatedOTP = OtpGenerator.generateOTP();

        OTPCode otpCode = OTPCode.builder()
                .otpCode(generatedOTP)
                .username(username)
                .createdAt(LocalTime.now())
                .build();

        // SEND the code it to mail service
        try {
            MessageEvent messageEvent = new MessageEvent(user.getFullName(),
                    user.getEmail(), generatedOTP,
                    "login to ITSkillsNow", "Your code to log in to ITSkillsNow is");

            rabbitMQSender.sendMessage("auth_message_exchange", "auth_message_routingKey",
                    messageEvent);
            log.info("Message sent successfully!");
        }catch (Exception ex){
            log.error(ex.getMessage());
            log.info("Error while sending message");
        }

        //Save it
        otpCodeRepository.save(otpCode);
        return "Code sent to your email for multi factor authentication";
    }

    @Override
    public void deleteOtpCode(String username) {
        otpCodeRepository.deleteAllByUsername(username);
    }

    @Override
    public boolean checkOtpCode(String otpCode) {
        Optional<OTPCode> code = otpCodeRepository.findByOtpCode(otpCode);
        return code.isPresent();
    }

    @Override
    public AuthResponse generateTokenWithOtpCode(String otpCode) {
        OTPCode code = otpCodeRepository.findByOtpCode(otpCode)
                .orElseThrow(() -> new OtpCodeNotFoundException("Code was not found!"));

        otpCodeRepository.deleteAllByUsername(code.getUsername());
        return this.generateToken(code.getUsername());
    }

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

}
