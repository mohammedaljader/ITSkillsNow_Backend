package com.itskillsnow.authservice.unitTests;

import com.itskillsnow.authservice.dto.AuthResponse;
import com.itskillsnow.authservice.event.AuthEvent;
import com.itskillsnow.authservice.event.UserPayload;
import com.itskillsnow.authservice.exception.OtpCodeNotFoundException;
import com.itskillsnow.authservice.exception.UserNotFoundException;
import com.itskillsnow.authservice.model.OTPCode;
import com.itskillsnow.authservice.model.Role;
import com.itskillsnow.authservice.model.User;
import com.itskillsnow.authservice.rabbitmq.RabbitMQSender;
import com.itskillsnow.authservice.repository.OTPCodeRepository;
import com.itskillsnow.authservice.repository.UserRepository;
import com.itskillsnow.authservice.service.AuthServiceImpl;
import com.itskillsnow.authservice.service.ServiceInterfaces.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalTime;
import java.util.List;
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
    private RabbitMQSender rabbitMQSender;

    @Mock
    private OTPCodeRepository otpCodeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtService, rabbitMQSender, otpCodeRepository);
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
        verify(rabbitMQSender, times(1))
                .sendMessage("auth.exchange", "auth.create", expectedEvent);
    }

    @Test
    void given_addRole_withCorrectData_shouldReturnsTrue() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setFullName("Test User");
        user.setRoles(List.of(Role.USER));

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        Boolean result = authService.addRole("ADMIN", user.getUsername());

        assertEquals(Boolean.TRUE, result);
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void given_addRole_RoleAlreadyExists_shouldReturnsFalse() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setFullName("Test User");
        user.setRoles(List.of(Role.USER));

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        Boolean result = authService.addRole("USER", user.getUsername());

        assertEquals(Boolean.FALSE, result);
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }


    @Test
    void given_addRole_UserNotFound_shouldReturnsException() {
        String username = "Wrong_Username";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());


        UserNotFoundException actual = Assertions.assertThrows(UserNotFoundException.class, () ->
                authService.addRole("USER", username)
        );

        assertEquals("User was not found!", actual.getMessage());
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
        String fullName = "fullName";


        when(jwtService.getUsernameFromToken(refreshToken)).thenReturn(username);

        User user = new User(UUID.randomUUID().toString(),username, "some_password", "USER");
        user.setFullName(fullName);
        user.setRoles(List.of(Role.USER));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        String accessToken = "some_access_token";
        String newRefreshToken = "new_some_refresh_token";
        AuthResponse expectedResponse = new AuthResponse(accessToken, newRefreshToken, username, fullName, user.getRoles());
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

    @Test
    public void given_DeleteMeWithCorrectUser_shouldReturnTrue() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encoded_password");
        UserPayload userPayload = new UserPayload();
        userPayload.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded_password")).thenReturn(true);
        Mockito.doNothing().when(userRepository).delete(any());
        Mockito.doNothing().when(rabbitMQSender).sendMessage(any(), any(), any());


        boolean result = authService.deleteMe("testUser", "password");


        Assertions.assertTrue(result);
        verify(userRepository).delete(user);
        verify(rabbitMQSender).sendMessage("auth.exchange", "auth.delete",
                new AuthEvent(userPayload, "delete"));
    }

    @Test
    public void given_DeleteMeWithIncorrectPassword_shouldReturnFalse() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encoded_password");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_password", user.getPassword())).thenReturn(false);


        boolean result = authService.deleteMe("testUser", "wrong_password");


        Assertions.assertFalse(result);
        verify(userRepository, never()).delete(user);
        verify(rabbitMQSender, never()).sendMessage(any(), any(), any());
    }

    @Test
    public void given_DeleteMeUserNotFound_shouldReturnFalse() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());


        boolean result = authService.deleteMe("testUser", "password");


        Assertions.assertFalse(result);
        verify(userRepository, never()).delete(any());
        verify(rabbitMQSender, never()).sendMessage(any(), any(), any());
    }

    @Test
    public void given_createOtpCode_withCorrectUsername_shouldReturnMessage() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encoded_password");
        user.setEmail("test@test.com");
        String expected = "Code sent to your email for multi factor authentication";

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        String actual = authService.createOtpCode("testUser");


        Assertions.assertEquals(expected ,actual);


        verify(userRepository, times(1)).findByUsername("testUser");
        verify(otpCodeRepository, times(1)).save(any(OTPCode.class));
        verify(rabbitMQSender, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    public void given_createOtpCode_withWrongUsername_shouldException() {
        String expected = "User was not found!";

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        UserNotFoundException actual = Assertions.assertThrows(UserNotFoundException.class, () ->
                authService.createOtpCode("testUser")
        );

        Assertions.assertEquals(expected ,actual.getMessage());

        verify(userRepository, times(1)).findByUsername("testUser");
        verify(otpCodeRepository, never()).save(any(OTPCode.class));
        verify(rabbitMQSender, never()).sendMessage(any(), any(), any());
    }


    @Test
    public void given_checkOtpCode_withCorrectOtpCode_shouldTrue() {
        OTPCode otpCode = OTPCode.builder()
                .username("username")
                .otpCode("231234")
                .createdAt(LocalTime.now())
                .build();

        when(otpCodeRepository.findByOtpCode("231234")).thenReturn(Optional.of(otpCode));

        boolean actual = authService.checkOtpCode("231234");


        assertTrue(actual);

        verify(otpCodeRepository, times(1)).findByOtpCode("231234");
    }


    @Test
    public void given_checkOtpCode_withWrongOtpCode_shouldFalse() {
        when(otpCodeRepository.findByOtpCode("212334")).thenReturn(Optional.empty());

        boolean actual = authService.checkOtpCode("212334");

        assertFalse(actual);

        verify(otpCodeRepository, times(1)).findByOtpCode("212334");
    }

    @Test
    public void given_generateTokenWithOtpCode_withCorrectOtpCode_shouldReturnTokens() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setFullName("Test User");

        OTPCode otpCode = OTPCode.builder()
                .username("testUser")
                .otpCode("231234")
                .createdAt(LocalTime.now())
                .build();

        when(otpCodeRepository.findByOtpCode("231234")).thenReturn(Optional.of(otpCode));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateTokens(any(User.class), anyString())).thenReturn(Map.of("accessToken",
                "some_access_token", "refreshToken", "some_refresh_token"));


        AuthResponse actual = authService.generateTokenWithOtpCode("231234");


        Assertions.assertEquals("some_access_token" ,actual.getAccessToken());


        verify(otpCodeRepository, times(1)).findByOtpCode("231234");
        verify(otpCodeRepository, times(1)).delete(any(OTPCode.class));
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(jwtService, times(1)).generateTokens(any(User.class), anyString());
    }

    @Test
    public void given_generateTokenWithOtpCode_withWrongOtpCode_shouldException() {
        String expected = "Code was not found!";

        when(otpCodeRepository.findByOtpCode("123456")).thenReturn(Optional.empty());

        OtpCodeNotFoundException actual = Assertions.assertThrows(OtpCodeNotFoundException.class, () ->
                authService.generateTokenWithOtpCode("123456")
        );

        Assertions.assertEquals(expected ,actual.getMessage());

        verify(otpCodeRepository, times(1)).findByOtpCode("123456");
        verify(otpCodeRepository, times(0)).delete(any(OTPCode.class));
        verify(userRepository, times(0)).findByUsername("testUser");
        verify(jwtService, times(0)).generateTokens(any(User.class), anyString());
    }
}