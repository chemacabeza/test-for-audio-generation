package com.example.tts.exception;

/**
 * Thrown when the OpenAI API returns a non-2xx response or times out.
 * Carries the upstream HTTP status so it can be surfaced to the caller.
 */
public class OpenAiException extends RuntimeException {

    private final int upstreamStatus;

    public OpenAiException(String message, int upstreamStatus) {
        super(message);
        this.upstreamStatus = upstreamStatus;
    }

    public OpenAiException(String message, Throwable cause) {
        super(message, cause);
        this.upstreamStatus = 502;
    }

    public int getUpstreamStatus() {
        return upstreamStatus;
    }
}
