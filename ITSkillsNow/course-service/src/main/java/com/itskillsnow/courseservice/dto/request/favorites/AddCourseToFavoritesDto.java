package com.itskillsnow.courseservice.dto.request.favorites;

import lombok.Data;

import java.util.UUID;

@Data
public class AddCourseToFavoritesDto {
    private UUID courseId;
    private String username;
}
