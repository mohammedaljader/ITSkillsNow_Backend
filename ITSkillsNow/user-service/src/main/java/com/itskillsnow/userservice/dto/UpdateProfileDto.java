package com.itskillsnow.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDto {
    private String username;
    private String address;
    private String phoneNumber;
    private String profession;
}
