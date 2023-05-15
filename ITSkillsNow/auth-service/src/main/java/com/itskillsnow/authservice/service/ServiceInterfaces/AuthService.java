package com.itskillsnow.authservice.service.ServiceInterfaces;

import com.itskillsnow.authservice.dto.AuthResponse;
import com.itskillsnow.authservice.model.User;

public interface AuthService {
    String saveUser(User credential);
    AuthResponse generateToken(String username);
    void validateToken(String token);
    AuthResponse refreshToken(String refreshToken);
    boolean deleteMe(String username, String password);
    boolean addRole(String role, String username);
}
