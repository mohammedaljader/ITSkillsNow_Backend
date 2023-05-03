package com.itskillsnow.courseservice.dto.request.enrollment;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AddEnrollmentDto {
    private UUID courseId;
    private String username;
}
