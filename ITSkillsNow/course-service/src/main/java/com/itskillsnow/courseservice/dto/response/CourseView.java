package com.itskillsnow.courseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseView {
    private UUID courseId;
    private String courseName;
    private String courseDescription;
    private String courseImage;
    private Double coursePrice;
    private String courseType;
    private String courseLanguage;
    private String username;
}
