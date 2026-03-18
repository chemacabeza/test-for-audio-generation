package com.example.tts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Strongly-typed binding for all "openai.*" properties in application.yml.
 * The API key is injected from the OPENAI_API_KEY environment variable — never
 * hardcoded.
 */
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    /** OpenAI API key — loaded from environment variable OPENAI_API_KEY */
    private String apiKey;

    /** Base URL for OpenAI REST API */
    private String baseUrl = "https://api.openai.com/v1";

    /** TTS model to use (e.g. gpt-4o-mini-tts) */
    private String model = "gpt-4o-mini-tts";

    /** Default voice when none is selected by the user */
    private String defaultVoice = "alloy";

    /** Audio response format (mp3, opus, aac, flac, wav, pcm) */
    private String responseFormat = "mp3";

    /** HTTP timeout in seconds for calls to OpenAI */
    private int timeoutSeconds = 60;

    // --- Getters & Setters ---

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDefaultVoice() {
        return defaultVoice;
    }

    public void setDefaultVoice(String defaultVoice) {
        this.defaultVoice = defaultVoice;
    }

    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
