package com.itskillsnow.jobservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobView {
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
