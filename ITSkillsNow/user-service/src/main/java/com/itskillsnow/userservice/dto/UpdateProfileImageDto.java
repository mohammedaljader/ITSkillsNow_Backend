package com.itskillsnow.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileImageDto {
    private String username;
    private MultipartFile profileImage;
}
