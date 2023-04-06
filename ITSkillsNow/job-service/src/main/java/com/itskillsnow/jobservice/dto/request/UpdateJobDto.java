package com.itskillsnow.jobservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateJobDto {
    private UUID jobId;

    private String jobName;

    private String jobDescription;

    private String jobAddress;

    private String jobCategory;

    private String jobEducationLevel;

    private String jobType;

    private Integer jobHours;

    private String username;
}
