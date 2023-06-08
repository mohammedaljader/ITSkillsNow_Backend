package com.itskillsnow.authservice.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginAttemptService {
    private final Map<String, Integer> attemptsMap;

    public LoginAttemptService() {
        attemptsMap = new HashMap<>();
    }

    public void loginFailed(String username) {
        int attempts = attemptsMap.getOrDefault(username, 0);
        attemptsMap.put(username, attempts + 1);
    }

    public boolean isBlocked(String username) {
        return attemptsMap.getOrDefault(username, 0) >= 3;
    }

    public void resetAttempts(String username) {
        attemptsMap.remove(username);
    }
}
