package com.itskillsnow.jobservice.dto.request.FavoriteJob;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddJobToFavoritesDto {
    private String username;
    private UUID jobId;
}
