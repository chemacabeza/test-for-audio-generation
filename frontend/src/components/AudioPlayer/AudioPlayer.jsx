import styles from './AudioPlayer.module.css'

/**
 * Displays a success banner and an HTML5 audio player.
 *
 * Props:
 *   audioUrl  — Blob URL returned from ttsApi.generateSpeech()
 */
function AudioPlayer({ audioUrl }) {
    if (!audioUrl) return null

    return (
        <div className={styles.container} role="region" aria-label="Generated audio">
            <div className={styles.header}>
                <span className={styles.checkmark} aria-hidden="true">✅</span>
                <div>
                    <p className={styles.title}>Audio Ready!</p>
                    <p className={styles.subtitle}>Your speech has been generated successfully.</p>
                </div>
            </div>

            {/* Native HTML5 audio player — no OpenAI calls needed here */}
            <audio
                controls
                autoPlay
                className={styles.player}
                src={audioUrl}
                aria-label="Generated speech audio"
            >
                Your browser does not support the audio element.
            </audio>

            {/* Download link */}
            <a
                href={audioUrl}
                download="generated-speech.mp3"
                className={styles.downloadLink}
                aria-label="Download the generated audio as an MP3 file"
            >
                ⬇ Download MP3
            </a>
        </div>
    )
}

export default AudioPlayer
