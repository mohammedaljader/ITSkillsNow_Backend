package com.itskillsnow.userservice.service;

import com.itskillsnow.userservice.dto.UpdateProfileDto;
import com.itskillsnow.userservice.dto.UserDto;
import com.itskillsnow.userservice.exception.UserNotFoundException;
import com.itskillsnow.userservice.model.User;
import com.itskillsnow.userservice.repository.UserRepository;
import com.itskillsnow.userservice.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        return users.stream().map(user -> new UserDto(
                        user.getUsername(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getAddress(),
                        user.getProfileImage(),
                        user.getPhoneNumber(),
                        user.getProfession()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(x -> new UserDto(x.getUsername(),
                        x.getFullName(),
                        x.getEmail(),
                        x.getAddress(),
                        x.getProfileImage(),
                        x.getPhoneNumber(),
                        x.getProfession()))
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));
    }

    @Override
    public Boolean addProfileImage(MultipartFile file) {
        return null;
    }

    @Override
    public Boolean updateProfileImage(MultipartFile file) {
        return null;
    }

    @Override
    public Boolean updateProfile(UpdateProfileDto updateProfileDto) {
        return null;
    }
}
