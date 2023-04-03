package com.itskillsnow.userservice.service;

import com.itskillsnow.userservice.dto.UserDto;
import com.itskillsnow.userservice.models.User;
import com.itskillsnow.userservice.repository.UserRepository;
import com.itskillsnow.userservice.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserDto(user.getUsername(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getAddress()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(x -> new UserDto(x.getUsername(), x.getFullName(), x.getEmail(), x.getAddress()))
                .orElse(null);
    }
}
