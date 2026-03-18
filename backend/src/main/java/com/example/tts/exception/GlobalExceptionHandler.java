package com.example.tts.exception;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.tts.dto.ErrorResponse;

/**
 * Centralised exception handler for all REST controllers.
 * Returns a consistent {@link ErrorResponse} JSON body for every error case.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles Bean Validation failures (e.g. blank text field in TtsRequest).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", message);
        return ResponseEntity.badRequest().body(new ErrorResponse(400, message));
    }

    /**
     * Handles failures when communicating with the OpenAI API.
     */
    @ExceptionHandler(OpenAiException.class)
    public ResponseEntity<ErrorResponse> handleOpenAiException(OpenAiException ex) {
        log.error("OpenAI API error (upstream {}): {}", ex.getUpstreamStatus(), ex.getMessage());
        int status = ex.getUpstreamStatus() == 429 ? 429 : HttpStatus.BAD_GATEWAY.value();
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status, "OpenAI API error: " + ex.getMessage()));
    }

    /**
     * Catch-all for any unexpected runtime exception.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(500, "An unexpected error occurred. Please try again."));
    }
}
