package com.itskillsnow.authservice.unitTests;

import com.itskillsnow.authservice.dto.AuthResponse;
import com.itskillsnow.authservice.event.AuthEvent;
import com.itskillsnow.authservice.event.UserPayload;
import com.itskillsnow.authservice.model.User;
import com.itskillsnow.authservice.repository.UserRepository;
import com.itskillsnow.authservice.service.AuthServiceImpl;
import com.itskillsnow.authservice.service.ServiceInterfaces.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtService, rabbitTemplate);
    }

    @Test
    void given_saveUser_withCorrectData_shouldReturnSuccessMessage() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setFullName("Test User");

        when(userRepository.existsByEmailOrUsername(anyString(), anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = authService.saveUser(user);

        assertEquals("user added to the system", result);
        verify(userRepository, times(1)).existsByEmailOrUsername(anyString(), anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void given_saveUser_withExistingEmail_shouldReturnErrorMessage() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setFullName("Test User");

        when(userRepository.existsByEmailOrUsername(anyString(), anyString())).thenReturn(true);

        String result = authService.saveUser(user);

        assertEquals("Email or username is already taken!", result);
        verify(userRepository, times(1)).existsByEmailOrUsername(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void given_saveUser_withCorrectData_ShouldSendUserToRabbitMQ() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setFullName("John Doe");
        user.setUsername("johnDoe");
        user.setEmail("johndoe@example.com");

        when(userRepository.save(any(User.class))).thenReturn(user);


        authService.saveUser(user);

        UserPayload expectedPayload = new UserPayload(userId.toString(), "John Doe", "johnDoe", "johndoe@example.com");
        AuthEvent expectedEvent = new AuthEvent(expectedPayload, "create");
        verify(rabbitTemplate, times(1))
                .convertAndSend("auth.exchange", "auth.create", expectedEvent);
    }


    @Test
    void given_generateToken_withExistingUser_shouldReturnAuthResponse() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setFullName("Test User");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateTokens(any(User.class), anyString())).thenReturn(Map.of("accessToken", "some_access_token", "refreshToken", "some_refresh_token"));

        AuthResponse authResponse = authService.generateToken("testUser");

        assertEquals("some_access_token", authResponse.getAccessToken());
        assertEquals("some_refresh_token", authResponse.getRefreshToken());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(jwtService, times(1)).generateTokens(any(User.class), anyString());
    }

    @Test
    void given_generateToken_withNonExistingUser_shouldReturnNull() {
        String nonExistingUsername = "non_existing_user";
        when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());


        AuthResponse result = authService.generateToken(nonExistingUsername);


        assertNull(result);
        verify(jwtService, never()).generateTokens(any(User.class), anyString());
    }

    @Test
    void given_validateToken_withValidToken_shouldNotThrowException() {
        String token = "valid_token";
        Mockito.doNothing().when(jwtService).validateToken(token);

        assertDoesNotThrow(() -> authService.validateToken(token));
    }

    @Test
    void given_validateToken_withInvalidToken_shouldThrowException() {
        String token = "invalid_token";
        Mockito.doThrow(new JwtException("Invalid token")).when(jwtService).validateToken(token);

        assertThrows(JwtException.class, () -> authService.validateToken(token));
    }

    @Test
    public void given_RefreshToken_withCorrectRefreshToken_shouldReturnTokens() {
        String refreshToken = "some_refresh_token";
        String username = "some_username";


        when(jwtService.getUsernameFromToken(refreshToken)).thenReturn(username);

        User user = new User(UUID.randomUUID().toString(),username, "some_password", "USER");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        String accessToken = "some_access_token";
        String newRefreshToken = "new_some_refresh_token";
        AuthResponse expectedResponse = new AuthResponse(accessToken, newRefreshToken);
        when(jwtService.generateTokens(user, username)).thenReturn(
                Map.of("accessToken", accessToken, "refreshToken", newRefreshToken)
        );

        AuthResponse actualResponse = authService.refreshToken(refreshToken);

        assertEquals(expectedResponse.getAccessToken(), actualResponse.getAccessToken());
        assertEquals(expectedResponse.getRefreshToken(), actualResponse.getRefreshToken());
    }

    @Test
    public void given_RefreshTokenWithInvalidToken_shouldReturnNull() {
        String refreshToken = "some_refresh_token";

        when(jwtService.getUsernameFromToken(refreshToken)).thenReturn(null);

        AuthResponse actualResponse = authService.refreshToken(refreshToken);

        assertNull(actualResponse);
    }

    @Test
    public void given_RefreshTokenWithInvalidUser_shouldReturnNull() {
        String refreshToken = "some_refresh_token";
        String username = "username";

        when(jwtService.getUsernameFromToken(refreshToken)).thenReturn(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        AuthResponse actualResponse = authService.refreshToken(refreshToken);

        assertNull(actualResponse);
    }
}