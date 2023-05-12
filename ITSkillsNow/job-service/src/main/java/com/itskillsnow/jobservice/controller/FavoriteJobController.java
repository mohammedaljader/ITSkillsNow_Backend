package com.itskillsnow.jobservice.controller;

import com.itskillsnow.jobservice.dto.request.FavoriteJob.AddJobToFavoritesDto;
import com.itskillsnow.jobservice.dto.response.FavoriteJobView;
import com.itskillsnow.jobservice.service.interfaces.FavoriteJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favoriteJob")
@RequiredArgsConstructor
public class FavoriteJobController {

    private final FavoriteJobService favoriteJobService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean addJobToFavorites(@RequestBody AddJobToFavoritesDto addJobToFavoritesDto)  {
        return favoriteJobService.addJobToFavorites(addJobToFavoritesDto);
    }


    @DeleteMapping("/{favoriteId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteJobFromFavorites(@PathVariable String favoriteId)  {
        return favoriteJobService.deleteJobFromFavorites(UUID.fromString(favoriteId));
    }

    @GetMapping("/{favoriteId}")
    @ResponseStatus(HttpStatus.OK)
    public FavoriteJobView getFavoriteJobById(@PathVariable String favoriteId)  {
        return favoriteJobService.getFavoriteById(UUID.fromString(favoriteId));
    }

    @GetMapping("/user/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<FavoriteJobView> getAllFavoritesByUser(@PathVariable String username)  {
        return favoriteJobService.getAllFavoritesByUser(username);
    }

    @GetMapping("/job/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    public Integer getFavoritesNumberByJob(@PathVariable String jobId)  {
        return favoriteJobService.getFavoritesNumberByJob(UUID.fromString(jobId));
    }
}
