package com.itskillsnow.userservice.service.interfaces;

import com.itskillsnow.userservice.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserByUsername(String username);
}
