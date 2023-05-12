package com.itskillsnow.jobservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FavoriteJobNotFoundException extends RuntimeException{
    public FavoriteJobNotFoundException(String message) {
        super(message);
    }
}
