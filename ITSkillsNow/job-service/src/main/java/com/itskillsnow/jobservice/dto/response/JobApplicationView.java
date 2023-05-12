package com.itskillsnow.jobservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobApplicationView {
    private UUID applicationId;
    private String applicationCv;
    private String applicationMotivation;
    private LocalDate applicationDate;
    private LocalTime applicationTime;
    private JobView jobView;
}
