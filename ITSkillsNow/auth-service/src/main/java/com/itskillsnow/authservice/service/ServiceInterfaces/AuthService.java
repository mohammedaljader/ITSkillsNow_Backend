package com.itskillsnow.authservice.service.ServiceInterfaces;

import com.itskillsnow.authservice.dto.AuthResponse;
import com.itskillsnow.authservice.model.User;

public interface AuthService {
    String saveUser(User credential);
    AuthResponse generateToken(String username);
    void validateToken(String token);
}
