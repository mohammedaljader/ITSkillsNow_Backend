package com.itskillsnow.courseservice.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class FavoriteCourseView {
    private UUID favoriteId;
    private CourseView courseView;
}
