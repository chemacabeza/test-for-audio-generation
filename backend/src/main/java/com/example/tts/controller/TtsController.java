package com.example.tts.controller;

import jakarta.validation.Valid;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tts.dto.TtsRequest;
import com.example.tts.service.TtsService;

/**
 * REST controller exposing the text-to-speech generation endpoint.
 *
 * <p>
 * Endpoint: {@code POST /api/tts/generate}
 *
 * <p>
 * Request body: JSON matching {@link TtsRequest}.
 * Response: raw MP3 audio bytes with appropriate Content-Type header.
 */
@RestController
@RequestMapping("/api/tts")
public class TtsController {

    private static final Logger log = LoggerFactory.getLogger(TtsController.class);

    private final TtsService ttsService;

    public TtsController(TtsService ttsService) {
        this.ttsService = ttsService;
    }

    /**
     * Generates speech audio from the provided text and voice parameters.
     *
     * @param request validated TTS parameters from the client
     * @return 200 OK with audio/mpeg body, or an error response
     */
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generate(@Valid @RequestBody TtsRequest request) {
        log.info("POST /api/tts/generate – voice={}", request.getVoice());

        byte[] audioBytes = ttsService.generateSpeech(request);

        // Determine Content-Type from the response format requested
        String contentType = resolveContentType(request.getResponseFormat());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(Objects.requireNonNullElse(contentType, "audio/mpeg")));
        headers.setContentLength(audioBytes.length);
        // Suggest a download filename for clients that prefer to download
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"speech.mp3\"");

        return new ResponseEntity<>(audioBytes, headers, HttpStatus.OK);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String resolveContentType(String responseFormat) {
        if (responseFormat == null)
            return "audio/mpeg";
        return switch (responseFormat.toLowerCase()) {
            case "opus" -> "audio/ogg; codecs=opus";
            case "aac" -> "audio/aac";
            case "flac" -> "audio/flac";
            case "wav" -> "audio/wav";
            case "pcm" -> "audio/pcm";
            default -> "audio/mpeg"; // mp3
        };
    }
}
