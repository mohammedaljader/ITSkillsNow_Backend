package com.itskillsnow.userservice.service;

import com.itskillsnow.userservice.dto.UpdateProfileDto;
import com.itskillsnow.userservice.dto.UpdateProfileImageDto;
import com.itskillsnow.userservice.dto.UserDto;
import com.itskillsnow.userservice.exception.UserNotFoundException;
import com.itskillsnow.userservice.model.User;
import com.itskillsnow.userservice.repository.UserRepository;
import com.itskillsnow.userservice.service.interfaces.BlobService;
import com.itskillsnow.userservice.service.interfaces.UserService;
import com.itskillsnow.userservice.util.FileNamingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BlobService blobService;

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
    public String addProfileImage(UpdateProfileImageDto profileImageDto) throws IOException {
        User user = userRepository.findByUsername(profileImageDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        String profileImage = blobService.storeFile(profileImageDto.getProfileImage().getOriginalFilename(),
                profileImageDto.getProfileImage().getInputStream(),
                profileImageDto.getProfileImage().getSize());

        //delete the old profile image from blob storage
        if(user.getProfileImage() != null){
            String blobFileName = FileNamingUtils.getBlobFilename(user.getProfileImage());
            blobService.deleteFile(blobFileName);
        }

        User newUser = mapUpdateProfileImageToModel(user, profileImage);
        userRepository.save(newUser);
        return profileImage;
    }

    @Override
    public Boolean deleteProfileImage(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        if(user.getProfileImage() != null){
            //delete the old profile image from blob storage
            String blobFileName = FileNamingUtils.getBlobFilename(user.getProfileImage());
            blobService.deleteFile(blobFileName);
            User newUser = mapUpdateProfileImageToModel(user, null);
            userRepository.save(newUser);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public UserDto updateProfile(UpdateProfileDto updateProfileDto) {
        User user = userRepository.findByUsername(updateProfileDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        User updatedUser = mapUpdateProfileToModel(user, updateProfileDto);
        User savedUser = userRepository.save(updatedUser);
        return mapModelToDto(savedUser);
    }

    private User mapUpdateProfileImageToModel(User user, String profileImage){
        return new User(user.getUserId(), user.getUsername(),user.getFullName(),
                user.getEmail(), user.getAddress(), profileImage, user.getPhoneNumber(),
                user.getProfession());
    }

    private User mapUpdateProfileToModel(User user, UpdateProfileDto profileDto){
        return new User(user.getUserId(), user.getUsername(),user.getFullName(),
                user.getEmail(), profileDto.getAddress(), user.getProfileImage(), profileDto.getPhoneNumber(),
                profileDto.getProfession());
    }

    private UserDto mapModelToDto(User user){
        return new UserDto(user.getUsername(), user.getFullName(), user.getEmail(),
                user.getAddress(), user.getProfileImage(), user.getPhoneNumber(),
                user.getProfession());
    }
}
