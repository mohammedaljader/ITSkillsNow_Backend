package com.itskillsnow.courseservice.dto.request.favorites;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AddCourseToFavoritesDto {
    private UUID courseId;
    private String username;
}
