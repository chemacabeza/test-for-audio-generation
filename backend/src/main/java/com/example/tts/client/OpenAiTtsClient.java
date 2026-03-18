package com.example.tts.client;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.example.tts.config.OpenAiProperties;
import com.example.tts.dto.TtsRequest;
import com.example.tts.exception.OpenAiException;

/**
 * Low-level HTTP client responsible solely for calling the OpenAI
 * audio/speech endpoint. All OpenAI-specific knowledge lives here,
 * making it easy to swap the underlying implementation later.
 */
@Component
public class OpenAiTtsClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiTtsClient.class);

    /** OpenAI TTS endpoint path (relative to base URL) */
    private static final String TTS_PATH = "/audio/speech";

    private final WebClient webClient;
    private final OpenAiProperties properties;

    public OpenAiTtsClient(WebClient openAiWebClient, OpenAiProperties properties) {
        this.webClient = openAiWebClient;
        this.properties = properties;
    }

    /**
     * Calls POST https://api.openai.com/v1/audio/speech and returns the raw
     * audio bytes. Falls back to defaults from application.yml when the
     * caller doesn't supply model/voice/responseFormat.
     *
     * @param request the TTS parameters from the frontend
     * @return audio binary (e.g. MP3)
     * @throws OpenAiException on non-2xx response or connection/timeout error
     */
    public byte[] generateSpeech(TtsRequest request) {
        // Build request body — use defaults from config when not supplied
        Map<String, String> body = new HashMap<>();
        body.put("model", resolveModel(request.getModel()));
        body.put("voice", resolveVoice(request.getVoice()));
        body.put("input", request.getText());
        body.put("response_format", resolveFormat(request.getResponseFormat()));

        // "instructions" is an optional field supported by newer TTS models
        if (request.getInstructions() != null && !request.getInstructions().isBlank()) {
            body.put("instructions", request.getInstructions());
        }

        log.debug("Calling OpenAI TTS – model={}, voice={}, inputLength={}",
                body.get("model"), body.get("voice"), request.getText().length());

        try {
            byte[] audioBytes = webClient.post()
                    .uri(TTS_PATH)
                    .bodyValue(body)
                    .retrieve()
                    // Wrap 4xx/5xx as OpenAiException with the upstream status code
                    .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
                            .map(errorBody -> new OpenAiException(
                                    "OpenAI returned 4xx: " + errorBody,
                                    response.statusCode().value())))
                    .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
                            .map(errorBody -> new OpenAiException(
                                    "OpenAI returned 5xx: " + errorBody,
                                    response.statusCode().value())))
                    .bodyToMono(byte[].class)
                    .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                    .block();

            if (audioBytes == null || audioBytes.length == 0) {
                throw new OpenAiException("OpenAI returned an empty audio response", 502);
            }

            log.info("OpenAI TTS succeeded – received {} bytes", audioBytes.length);
            return audioBytes;

        } catch (OpenAiException e) {
            throw e; // re-throw as-is
        } catch (WebClientRequestException e) {
            throw new OpenAiException("Connection to OpenAI failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new OpenAiException("Unexpected error calling OpenAI: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers – resolve value or fall back to application.yml defaults
    // -------------------------------------------------------------------------

    private String resolveModel(String requested) {
        return (requested != null && !requested.isBlank()) ? requested : properties.getModel();
    }

    private String resolveVoice(String requested) {
        return (requested != null && !requested.isBlank()) ? requested : properties.getDefaultVoice();
    }

    private String resolveFormat(String requested) {
        return (requested != null && !requested.isBlank()) ? requested : properties.getResponseFormat();
    }
}
