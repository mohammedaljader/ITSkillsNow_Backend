package com.itskillsnow.authservice.service.ServiceInterfaces;

public interface JwtService {
    String generateToken(String userName);
    void validateToken(final String token);
}
