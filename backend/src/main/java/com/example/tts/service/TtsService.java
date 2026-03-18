package com.example.tts.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.tts.client.OpenAiTtsClient;
import com.example.tts.dto.TtsRequest;

/**
 * Application service layer for text-to-speech generation.
 * Orchestrates the request, delegates to {@link OpenAiTtsClient},
 * and is the right place to add caching, rate-limiting, or
 * usage tracking in the future.
 */
@Service
public class TtsService {

    private static final Logger log = LoggerFactory.getLogger(TtsService.class);

    private final OpenAiTtsClient openAiTtsClient;

    public TtsService(OpenAiTtsClient openAiTtsClient) {
        this.openAiTtsClient = openAiTtsClient;
    }

    /**
     * Generates speech audio for the given TTS request.
     *
     * @param request validated request from the controller
     * @return raw audio bytes (MP3 or other format as configured)
     */
    public byte[] generateSpeech(TtsRequest request) {
        log.info("TTS request received – voice={}, model={}, textLength={}",
                request.getVoice(), request.getModel(), request.getText().length());

        byte[] audio = openAiTtsClient.generateSpeech(request);

        log.info("TTS generation complete – {} bytes produced", audio.length);
        return audio;
    }
}
