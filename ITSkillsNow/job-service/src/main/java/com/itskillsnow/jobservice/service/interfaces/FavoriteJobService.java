package com.itskillsnow.jobservice.service.interfaces;

import com.itskillsnow.jobservice.dto.request.FavoriteJob.AddJobToFavoritesDto;
import com.itskillsnow.jobservice.dto.response.FavoriteJobView;

import java.util.List;
import java.util.UUID;

public interface FavoriteJobService {

    Boolean addJobToFavorites(AddJobToFavoritesDto jobToFavoritesDto);

    Boolean deleteJobFromFavorites(UUID favoriteId);

    FavoriteJobView getFavoriteById(UUID favoriteId);

    List<FavoriteJobView> getAllFavoritesByUser(String username);

    Integer getFavoritesNumberByJob(UUID jobId);
}
