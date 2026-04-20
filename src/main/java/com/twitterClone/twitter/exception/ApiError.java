package com.twitterClone.twitter.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
public class ApiError {

    private final Instant timestamp;
    private final int status;
    private final String message;

    private ApiError(Instant timestamp, int status, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }

    public static ApiError of(HttpStatus status, String message) {
        return new ApiError(Instant.now(), status.value(), message);
    }

    public static ApiError of(HttpStatus status, String message, String ignored) {
        return of(status, message);
    }

    public static ApiError of(HttpStatus status, String message, Object ignored) {
        return of(status, message);
    }
}

