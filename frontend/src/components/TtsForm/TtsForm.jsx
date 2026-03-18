import { useState } from 'react'
import styles from './TtsForm.module.css'

/**
 * Available OpenAI TTS voices.
 * Update this list if OpenAI adds new voices.
 */
const VOICES = [
    { value: 'alloy', label: 'Alloy — Neutral & balanced' },
    { value: 'ash', label: 'Ash — Clear & composed' },
    { value: 'ballad', label: 'Ballad — Warm & emotive' },
    { value: 'coral', label: 'Coral — Friendly & bright' },
    { value: 'echo', label: 'Echo — Smooth & expressive' },
    { value: 'fable', label: 'Fable — Storytelling' },
    { value: 'nova', label: 'Nova — Energetic & lively' },
    { value: 'onyx', label: 'Onyx — Deep & authoritative' },
    { value: 'sage', label: 'Sage — Calm & thoughtful' },
    { value: 'shimmer', label: 'Shimmer — Airy & uplifting' },
    { value: 'verse', label: 'Verse — Versatile & natural' },
]

const DEFAULT_MODEL = 'gpt-4o-mini-tts'

/**
 * Form component for collecting TTS parameters.
 * Calls onSubmit(payload) when the user clicks Generate.
 *
 * Props:
 *   onSubmit(payload)  — callback called with the form data
 *   isLoading          — disables the form while a request is in progress
 */
function TtsForm({ onSubmit, isLoading }) {
    const [text, settext] = useState('')
    const [voice, setVoice] = useState('alloy')
    const [instructions, setInstructions] = useState('')
    const [model, setModel] = useState(DEFAULT_MODEL)

    const handleSubmit = (e) => {
        e.preventDefault()
        if (!text.trim()) return

        onSubmit({
            text: text.trim(),
            voice,
            instructions: instructions.trim() || undefined,
            model,
            responseFormat: 'mp3',
        })
    }

    const charCount = text.length
    const isOverLimit = charCount > 4096
    const canSubmit = !isLoading && text.trim().length > 0 && !isOverLimit

    return (
        <form onSubmit={handleSubmit} className={styles.form} aria-label="Text-to-speech generator">
            <h2 className={styles.formTitle}>Generate Speech</h2>

            {/* ── Text input ── */}
            <div className={styles.field}>
                <label htmlFor="tts-text" className={styles.label}>
                    Text to Speak
                    <span className={styles.required} aria-hidden="true">*</span>
                </label>
                <textarea
                    id="tts-text"
                    className={`${styles.textarea} ${isOverLimit ? styles.textareaError : ''}`}
                    rows={6}
                    placeholder="Type or paste the text you want to convert to speech…"
                    value={text}
                    onChange={(e) => settext(e.target.value)}
                    disabled={isLoading}
                    required
                    aria-describedby="tts-text-count"
                />
                <span
                    id="tts-text-count"
                    className={`${styles.charCount} ${isOverLimit ? styles.charCountError : ''}`}
                    aria-live="polite"
                >
                    {charCount.toLocaleString()} / 4,096
                </span>
            </div>

            {/* ── Voice selector ── */}
            <div className={styles.row}>
                <div className={styles.field}>
                    <label htmlFor="tts-voice" className={styles.label}>Voice</label>
                    <select
                        id="tts-voice"
                        className={styles.select}
                        value={voice}
                        onChange={(e) => setVoice(e.target.value)}
                        disabled={isLoading}
                    >
                        {VOICES.map(({ value, label }) => (
                            <option key={value} value={value}>{label}</option>
                        ))}
                    </select>
                </div>

                {/* ── Model selector ── */}
                <div className={styles.field}>
                    <label htmlFor="tts-model" className={styles.label}>Model</label>
                    <select
                        id="tts-model"
                        className={styles.select}
                        value={model}
                        onChange={(e) => setModel(e.target.value)}
                        disabled={isLoading}
                    >
                        <option value="gpt-4o-mini-tts">gpt-4o-mini-tts (recommended)</option>
                        <option value="tts-1">tts-1 (fast)</option>
                        <option value="tts-1-hd">tts-1-hd (high quality)</option>
                    </select>
                </div>
            </div>

            {/* ── Instructions ── */}
            <div className={styles.field}>
                <label htmlFor="tts-instructions" className={styles.label}>
                    Voice Instructions
                    <span className={styles.optional}>(optional)</span>
                </label>
                <textarea
                    id="tts-instructions"
                    className={styles.textarea}
                    rows={3}
                    placeholder="e.g. Speak slowly and warmly, as if telling a bedtime story…"
                    value={instructions}
                    onChange={(e) => setInstructions(e.target.value)}
                    disabled={isLoading}
                    aria-describedby="tts-instructions-hint"
                />
                <p id="tts-instructions-hint" className={styles.hint}>
                    Guide the voice style, emotion, or pace. Only supported by compatible models.
                </p>
            </div>

            {/* ── Submit button ── */}
            <button
                type="submit"
                id="generate-button"
                className={styles.button}
                disabled={!canSubmit}
                aria-busy={isLoading}
            >
                {isLoading ? (
                    <>
                        <span className={styles.buttonSpinner} aria-hidden="true" />
                        Generating…
                    </>
                ) : (
                    <>
                        <span aria-hidden="true">✨</span>
                        Generate Speech
                    </>
                )}
            </button>
        </form>
    )
}

export default TtsForm
