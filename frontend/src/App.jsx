import { useState, useRef, useCallback } from 'react'
import TtsForm from './components/TtsForm/TtsForm'
import AudioPlayer from './components/AudioPlayer/AudioPlayer'
import ErrorAlert from './components/ErrorAlert/ErrorAlert'
import LoadingSpinner from './components/LoadingSpinner/LoadingSpinner'
import { generateSpeech } from './services/ttsApi'
import styles from './App.module.css'

/**
 * Root application component.
 * Manages global state: loading, error, and the generated audio URL.
 */
function App() {
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState(null)
    const [audioUrl, setAudioUrl] = useState(null)

    // Keep track of the last blob URL so we can revoke it (memory cleanup)
    const prevAudioUrl = useRef(null)

    const handleGenerate = useCallback(async (formData) => {
        // Clear previous result and error
        setError(null)
        setAudioUrl(null)

        // Revoke the previous blob URL to free browser memory
        if (prevAudioUrl.current) {
            URL.revokeObjectURL(prevAudioUrl.current)
            prevAudioUrl.current = null
        }

        setIsLoading(true)
        try {
            const url = await generateSpeech(formData)
            prevAudioUrl.current = url
            setAudioUrl(url)
        } catch (err) {
            setError(err.message ?? 'An unexpected error occurred.')
        } finally {
            setIsLoading(false)
        }
    }, [])

    return (
        <div className={styles.app}>
            {/* ── Header ── */}
            <header className={styles.header}>
                <div className={styles.headerInner}>
                    <div className={styles.logo}>
                        <span className={styles.logoIcon}>🎙️</span>
                        <span className={styles.logoText}>AI Voice Studio</span>
                    </div>
                    <p className={styles.tagline}>Transform text into natural-sounding speech with OpenAI voices</p>
                </div>
            </header>

            {/* ── Main content ── */}
            <main className={styles.main}>
                <div className={styles.card}>
                    <TtsForm onSubmit={handleGenerate} isLoading={isLoading} />
                </div>

                {/* Loading state */}
                {isLoading && (
                    <div className={styles.feedbackSection}>
                        <LoadingSpinner />
                    </div>
                )}

                {/* Error state */}
                {!isLoading && error && (
                    <div className={styles.feedbackSection}>
                        <ErrorAlert message={error} onDismiss={() => setError(null)} />
                    </div>
                )}

                {/* Success state */}
                {!isLoading && audioUrl && (
                    <div className={styles.feedbackSection}>
                        <AudioPlayer audioUrl={audioUrl} />
                    </div>
                )}
            </main>

            {/* ── Footer ── */}
            <footer className={styles.footer}>
                <p>Powered by OpenAI TTS · API key lives only on the server</p>
            </footer>
        </div>
    )
}

export default App
