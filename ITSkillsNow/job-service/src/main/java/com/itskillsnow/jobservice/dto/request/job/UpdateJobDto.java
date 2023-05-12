package com.itskillsnow.jobservice.dto.request.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateJobDto {
    private UUID jobId;

    private String jobName;

    private String jobDescription;

    private String jobImage;

    private String jobAddress;

    private String jobCategory;

    private String jobEducationLevel;

    private String jobType;

    private Integer jobHours;

    private String username;
}
