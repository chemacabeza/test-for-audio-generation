package com.example.tts.dto;

import java.time.Instant;

/**
 * Unified error response returned by GlobalExceptionHandler for any
 * non-successful API call. Keeps error structure consistent across endpoints.
 */
public class ErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String message;

    public ErrorResponse(int status, String message) {
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
