package com.itskillsnow.courseservice.dto.request.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCourseWithFileDto {
    private String courseName;
    private String courseDescription;
    private MultipartFile courseImage;
    private Double coursePrice;
    private String courseType;
    private String courseLanguage;
    private boolean isPublished;
    private String username;
}
