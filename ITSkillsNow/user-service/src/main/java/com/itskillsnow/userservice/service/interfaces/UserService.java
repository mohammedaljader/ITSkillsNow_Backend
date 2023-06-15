package com.itskillsnow.userservice.service.interfaces;

import com.itskillsnow.userservice.dto.UpdateProfileDto;
import com.itskillsnow.userservice.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserByUsername(String username);

    Boolean addProfileImage(MultipartFile file);

    Boolean updateProfileImage(MultipartFile file);

    Boolean updateProfile(UpdateProfileDto updateProfileDto);
}
