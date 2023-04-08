package com.itskillsnow.authservice.service.ServiceInterfaces;

import com.itskillsnow.authservice.model.User;

import java.util.Map;

public interface JwtService {
    String generateToken(User user, String userName);
    void validateToken(final String token);
    Map<String, String> generateTokens(User user, String userName);
    String getUsernameFromToken(String refreshToken);
}
