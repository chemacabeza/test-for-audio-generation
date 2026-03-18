/**
 * TTS API service — the ONLY place in the frontend that knows about the backend URL.
 * The OpenAI API key is NEVER present here; all AI calls happen on the backend.
 */

// In development, Vite's proxy forwards /api/* to http://localhost:8080.
// In production, set VITE_API_BASE_URL to your deployed backend URL.
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

/**
 * Calls POST /api/tts/generate and returns a Blob URL for the audio.
 *
 * @param {Object} payload - { text, voice, instructions, model, responseFormat }
 * @returns {Promise<string>} - Blob URL suitable for an <audio> element src
 * @throws {Error} - with a user-friendly message if the request fails
 */
export async function generateSpeech(payload) {
    const response = await fetch(`${API_BASE_URL}/api/tts/generate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
    });

    if (!response.ok) {
        // Try to parse the ErrorResponse JSON from the backend
        let message = `Request failed with status ${response.status}`;
        try {
            const errorData = await response.json();
            message = errorData.message ?? message;
        } catch {
            // Ignore JSON parse failure — use default message
        }
        throw new Error(message);
    }

    // Convert the audio bytes to a Blob URL the browser can play
    const audioBlob = await response.blob();
    return URL.createObjectURL(audioBlob);
}
