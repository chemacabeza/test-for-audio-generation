import styles from './ErrorAlert.module.css'

/**
 * Displays an error message returned from the backend or a network failure.
 *
 * Props:
 *   message     — human-readable error string
 *   onDismiss() — called when the user closes the alert
 */
function ErrorAlert({ message, onDismiss }) {
    if (!message) return null

    return (
        <div
            className={styles.container}
            role="alert"
            aria-live="assertive"
            aria-label="Error"
        >
            <span className={styles.icon} aria-hidden="true">⚠️</span>
            <div className={styles.body}>
                <p className={styles.title}>Something went wrong</p>
                <p className={styles.message}>{message}</p>
            </div>
            <button
                type="button"
                id="dismiss-error-button"
                className={styles.dismissButton}
                onClick={onDismiss}
                aria-label="Dismiss error"
            >
                ✕
            </button>
        </div>
    )
}

export default ErrorAlert
