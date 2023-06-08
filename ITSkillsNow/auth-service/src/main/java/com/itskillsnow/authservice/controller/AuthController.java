package com.itskillsnow.authservice.controller;


import com.itskillsnow.authservice.dto.AddUserDto;
import com.itskillsnow.authservice.dto.AuthRequest;
import com.itskillsnow.authservice.dto.AuthResponse;
import com.itskillsnow.authservice.dto.request.AddRoleDto;
import com.itskillsnow.authservice.dto.request.DeleteUserDto;
import com.itskillsnow.authservice.dto.request.LoginWithMultiFactorDto;
import com.itskillsnow.authservice.exception.InvalidAccessException;
import com.itskillsnow.authservice.model.User;
import com.itskillsnow.authservice.service.LoginAttemptService;
import com.itskillsnow.authservice.service.ServiceInterfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    private final LoginAttemptService loginAttemptService;

    @PostMapping("/register")
    public String addNewUser(@RequestBody AddUserDto user) {
        return authService.saveUser(new User(user.getFullName(), user.getUsername(), user.getEmail(), user.getPassword()));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            return authService.generateToken(authRequest.getUsername());
        } else {
            throw new RuntimeException("invalid access");
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        authService.validateToken(token);
        return "Token is valid";
    }

    @PostMapping("/refresh/{refreshToken}")
    public AuthResponse refreshToken(@PathVariable String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/deleteMe")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteMe(@RequestBody DeleteUserDto userDto){
        return authService.deleteMe(userDto.getUsername(), userDto.getPassword());
    }

    @PostMapping("/addRole")
    @ResponseStatus(HttpStatus.OK)
    public boolean addRole(@RequestBody AddRoleDto addRoleDto){
        return authService.addRole(addRoleDto.getRole(), addRoleDto.getUsername());
    }

    @PostMapping("/login-multiFactor")
    public String loginWithMultiFactor(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                        authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            return authService.createOtpCode(authRequest.getUsername());
        } else {
            throw new InvalidAccessException("invalid access");
        }
    }

    @PostMapping("/check-multiFactor")
    public ResponseEntity<?> checkMultiFactor(@RequestBody LoginWithMultiFactorDto dto) {
        if (loginAttemptService.isBlocked(dto.getUsername())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many login attempts. Please try again later.");
        }
        if (authService.checkOtpCode(dto.getCode())) {
            loginAttemptService.resetAttempts(dto.getUsername());
            return ResponseEntity.ok(authService.generateTokenWithOtpCode(dto.getCode()));
        } else {
            loginAttemptService.loginFailed(dto.getUsername());
            if (loginAttemptService.isBlocked(dto.getUsername())) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("Too many login attempts. Please try again later.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid code.");
        }
    }
}
