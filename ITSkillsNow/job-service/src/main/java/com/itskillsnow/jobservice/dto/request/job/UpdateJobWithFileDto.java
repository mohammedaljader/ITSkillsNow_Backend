package com.itskillsnow.jobservice.dto.request.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateJobWithFileDto {
    private UUID jobId;

    private String jobName;

    private String jobDescription;

    private MultipartFile jobImage;

    private String jobAddress;

    private String jobCategory;

    private String jobEducationLevel;

    private String jobType;

    private Integer jobHours;

    private String username;
}
