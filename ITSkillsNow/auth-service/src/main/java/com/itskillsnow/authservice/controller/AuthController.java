package com.itskillsnow.authservice.controller;


import com.itskillsnow.authservice.dto.AddUserDto;
import com.itskillsnow.authservice.dto.AuthRequest;
import com.itskillsnow.authservice.dto.AuthResponse;
import com.itskillsnow.authservice.model.User;
import com.itskillsnow.authservice.service.ServiceInterfaces.AuthService;
import lombok.RequiredArgsConstructor;
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

}
