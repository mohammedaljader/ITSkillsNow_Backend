package com.itskillsnow.jobservice.dto.request.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddJobDto {
    private String jobName;

    private String jobDescription;

    private String jobAddress;

    private String jobCategory;

    private String jobEducationLevel;

    private String jobType;

    private Integer jobHours;

    private String username;
}
