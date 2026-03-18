import styles from './LoadingSpinner.module.css'

/**
 * Full-width loading indicator shown while the TTS request is in flight.
 */
function LoadingSpinner() {
    return (
        <div className={styles.container} role="status" aria-live="polite" aria-label="Generating speech, please wait">
            <div className={styles.ring} aria-hidden="true">
                <div className={styles.inner} />
            </div>
            <div className={styles.text}>
                <p className={styles.primary}>Generating your audio…</p>
                <p className={styles.secondary}>This may take a few seconds</p>
            </div>
        </div>
    )
}

export default LoadingSpinner
