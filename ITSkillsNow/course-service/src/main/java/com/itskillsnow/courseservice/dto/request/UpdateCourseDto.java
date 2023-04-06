package com.itskillsnow.courseservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCourseDto {
    private UUID courseId;
    private String courseName;
    private String courseDescription;
    private String courseImage;
    private Double coursePrice;
    private String courseType;
    private String courseLanguage;
    private boolean isPublished;
    private String username;
}
