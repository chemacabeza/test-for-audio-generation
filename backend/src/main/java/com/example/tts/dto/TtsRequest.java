package com.example.tts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload sent by the React frontend to POST /api/tts/generate.
 * Validation annotations ensure the required fields are present before
 * we even talk to OpenAI.
 */
public class TtsRequest {

    /** Text to synthesize – required, max 4096 characters (OpenAI limit) */
    @NotBlank(message = "Text must not be blank")
    @Size(max = 4096, message = "Text must not exceed 4096 characters")
    private String text;

    /**
     * Voice to use. Supported by OpenAI: alloy, ash, ballad, coral,
     * echo, fable, onyx, nova, sage, shimmer, verse.
     * Defaults to "alloy" if not provided.
     */
    private String voice;

    /**
     * Optional personality / style instructions for the TTS model
     * (e.g. "Speak slowly and warmly").
     */
    private String instructions;

    /** TTS model – defaults to value in application.yml if not provided */
    private String model;

    /** Response format – defaults to value in application.yml if not provided */
    private String responseFormat;

    // --- Getters & Setters ---

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }
}
