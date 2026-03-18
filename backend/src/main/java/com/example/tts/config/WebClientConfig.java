package com.example.tts.config;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Creates the WebClient bean pre-configured for OpenAI API calls.
 * The Authorization header is set here using the injected API key.
 * No secret is hard-coded.
 */
@Configuration
public class WebClientConfig {

    private final OpenAiProperties openAiProperties;

    public WebClientConfig(OpenAiProperties openAiProperties) {
        this.openAiProperties = openAiProperties;
    }

    /**
     * WebClient bean dedicated to OpenAI.
     * Buffer size is raised to 10 MB to accommodate large audio responses.
     */
    @Bean
    public WebClient openAiWebClient() {
        // Increase max in-memory buffer size to 10 MB for audio payloads
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl(Objects.requireNonNullElse(openAiProperties.getBaseUrl(), "https://api.openai.com/v1"))
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        "Bearer " + Objects.requireNonNullElse(openAiProperties.getApiKey(), ""))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(strategies)
                .build();
    }
}
