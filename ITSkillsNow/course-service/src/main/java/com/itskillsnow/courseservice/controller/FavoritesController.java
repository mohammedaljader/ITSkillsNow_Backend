package com.itskillsnow.courseservice.controller;

import com.itskillsnow.courseservice.dto.request.favorites.AddCourseToFavoritesDto;
import com.itskillsnow.courseservice.dto.response.FavoriteCourseView;
import com.itskillsnow.courseservice.service.interfaces.FavoriteCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course/favorites")
@RequiredArgsConstructor
public class FavoritesController {

    private final FavoriteCourseService favoriteCourseService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean addCourseToFavorites(@RequestBody AddCourseToFavoritesDto addCourseToFavoritesDto){
        // TODO: change the the http status to bad request if username or courseId are wrong
        return favoriteCourseService.addCourseToFavorites(addCourseToFavoritesDto);
    }

    @DeleteMapping("/{favoriteId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteCourseFromFavorites(@PathVariable String favoriteId){
        return favoriteCourseService.deleteCourseFromFavorites(UUID.fromString(favoriteId));
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<FavoriteCourseView> getAllFavorites(@PathVariable String username){
        return favoriteCourseService.getAllFavorites(username);
    }

}
