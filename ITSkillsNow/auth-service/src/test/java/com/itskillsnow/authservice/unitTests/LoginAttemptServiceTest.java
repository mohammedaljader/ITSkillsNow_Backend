package com.itskillsnow.authservice.unitTests;

import com.itskillsnow.authservice.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginAttemptServiceTest {
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    public void setUp() {
        loginAttemptService = new LoginAttemptService();
    }

    @Test
    public void testLoginFailed() {
        String username = "testUser";

        // Simulate login failures
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);

        assertTrue(loginAttemptService.isBlocked(username));
    }

    @Test
    public void testResetAttempts() {
        String username = "testUser";

        // Simulate login failures
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);

        assertTrue(loginAttemptService.isBlocked(username));

        // Reset attempts
        loginAttemptService.resetAttempts(username);

        assertFalse(loginAttemptService.isBlocked(username));
    }

    @Test
    public void testIsBlocked() {
        String username1 = "user1";
        String username2 = "user2";

        // Simulate login failures for username1
        loginAttemptService.loginFailed(username1);
        loginAttemptService.loginFailed(username1);

        assertFalse(loginAttemptService.isBlocked(username1));
        assertFalse(loginAttemptService.isBlocked(username2));

        // Simulate login failure for username1 (third attempt)
        loginAttemptService.loginFailed(username1);

        assertTrue(loginAttemptService.isBlocked(username1));
        assertFalse(loginAttemptService.isBlocked(username2));
    }
}
