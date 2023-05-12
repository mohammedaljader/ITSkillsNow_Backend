package com.itskillsnow.jobservice.dto.request.jobApplication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddJobApplication {
    private MultipartFile applicationCv;
    private String applicationMotivation;
    private String username;
    private UUID jobId;
}
