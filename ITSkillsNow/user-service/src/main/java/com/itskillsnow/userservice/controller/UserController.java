package com.itskillsnow.userservice.controller;

import com.itskillsnow.userservice.dto.UpdateProfileDto;
import com.itskillsnow.userservice.dto.UpdateProfileImageDto;
import com.itskillsnow.userservice.dto.UserDto;
import com.itskillsnow.userservice.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers(){
        return userService.getAllUsers();
    }


    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserByUsername(@PathVariable String username){
        return userService.getUserByUsername(username);
    }

    @PostMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public Boolean addProfileImage(@RequestParam String username,
                                         @RequestParam("profileImage") MultipartFile profileImage) throws IOException {

        UpdateProfileImageDto profileImageDto = new UpdateProfileImageDto(username, profileImage);
        return userService.addProfileImage(profileImageDto);
    }

    @DeleteMapping("/profile/{username}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteUser(@PathVariable String username){
        return userService.deleteProfileImage(username);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateCourseWithImage(@RequestBody UpdateProfileDto updateProfileDto){
        return userService.updateProfile(updateProfileDto);
    }

}
