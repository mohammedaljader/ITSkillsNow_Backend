package com.itskillsnow.jobservice.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteJobView {
    private UUID favoriteId;
    private LocalDate favoriteDate;
    private LocalTime favoriteTime;
    private JobView jobView;
}
