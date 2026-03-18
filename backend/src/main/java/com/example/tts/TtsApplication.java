package com.example.tts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.tts.config.OpenAiProperties;

@SpringBootApplication
@EnableConfigurationProperties(OpenAiProperties.class)
public class TtsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtsApplication.class, args);
    }
}
