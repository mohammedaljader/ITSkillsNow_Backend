package com.itskillsnow.courseservice.dto.request.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class AddEnrollmentDto {
    private UUID courseId;
    private String username;
}
