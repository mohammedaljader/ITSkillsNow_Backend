package com.itskillsnow.courseservice.service.interfaces;

import com.itskillsnow.courseservice.dto.request.favorites.AddCourseToFavoritesDto;
import com.itskillsnow.courseservice.dto.response.FavoriteCourseView;

import java.util.List;
import java.util.UUID;

public interface FavoriteCourseService {

    boolean addCourseToFavorites(AddCourseToFavoritesDto addCourseToFavoritesDto);

    boolean deleteCourseFromFavorites(UUID favoriteId);

    List<FavoriteCourseView> getAllFavorites(String username);
}
