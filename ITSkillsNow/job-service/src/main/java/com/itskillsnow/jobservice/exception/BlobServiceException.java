package com.itskillsnow.jobservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BlobServiceException extends RuntimeException{
    public BlobServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
