package com.itskillsnow.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String username;

    private String fullName;

    private String email;

    private String address;

    private String profileImage;

    private String phoneNumber;

    private String profession;
}
