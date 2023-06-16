package com.itskillsnow.userservice.service.interfaces;

import com.itskillsnow.userservice.dto.UpdateProfileDto;
import com.itskillsnow.userservice.dto.UpdateProfileImageDto;
import com.itskillsnow.userservice.dto.UserDto;

import java.io.IOException;
import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserByUsername(String username);

    String addProfileImage(UpdateProfileImageDto profileImageDto) throws IOException;

    Boolean deleteProfileImage(String username);

    UserDto updateProfile(UpdateProfileDto updateProfileDto);
}
