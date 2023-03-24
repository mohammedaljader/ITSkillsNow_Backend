package com.itskillsnow.authservice.service;


import com.itskillsnow.authservice.dto.AuthResponse;
import com.itskillsnow.authservice.entity.Role;
import com.itskillsnow.authservice.entity.UserCredential;
import com.itskillsnow.authservice.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public String saveUser(UserCredential credential) {
        Optional<UserCredential> userCredential = repository.findAll().stream()
                .filter(x -> x.getEmail().equals(credential.getEmail()))
                .findAny();
        if(userCredential.isPresent()){
            return "Email is already taken!";
        }else {
            credential.setPassword(passwordEncoder.encode(credential.getPassword()));
            credential.setRole(Role.USER);
            repository.save(credential);
            return "user added to the system";
        }
    }

    public AuthResponse generateToken(String username) {
        return new AuthResponse(jwtService.generateToken(username));
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }


}
