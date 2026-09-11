import {useEffect, useState} from "react"

const EmailVerificationModal = ({email, isLoading, error, successMessage, onConfirm, onCancel}) => {
    const [code, setCode] = useState("")
    const [secondsLeft, setSecondsLeft] = useState(600)
    const isExpired = secondsLeft <= 0

    useEffect(() => {
        if (secondsLeft <= 0) {
            return
        }
        const timerId = window.setInterval(() => {
            setSecondsLeft((currentSeconds) => Math.max(currentSeconds - 1, 0))
        }, 1000)
        return () => window.clearInterval(timerId)
    }, [secondsLeft])

    const minutes = String(Math.floor(secondsLeft / 60)).padStart(2, "0")
    const seconds = String(secondsLeft % 60).padStart(2, "0")

    const handleCodeChange = (event) => {
        setCode(event.target.value.replace(/\D/g, "").slice(0, 6))
    }

    const handleSubmit = (event) => {
        event.preventDefault()
        if (!isExpired && code.length === 6) {
            onConfirm(code)
        }
    }

    return (
        <div className="modal-backdrop" role="presentation">
            <section className="verification-modal" role="dialog" aria-modal="true" aria-labelledby="verification-title">
                <h2 id="verification-title">Email verification</h2>
                <p className="verification-copy">
                    A verification code was sent to <strong>{email}</strong>. Enter the 6-digit code to complete your registration.
                </p>

                <div className={`verification-timer ${isExpired ? "timer-expired" : ""}`}>
                    {isExpired ? "Code expired" : `${minutes}:${seconds}`}
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="auth-field">
                        <label htmlFor="verificationCode">Verification code</label>
                        <input
                            id="verificationCode"
                            type="text"
                            inputMode="numeric"
                            value={code}
                            onChange={handleCodeChange}
                            placeholder="123456"
                            disabled={isLoading}
                        />
                    </div>

                    {error && <p className="form-error">{error}</p>}
                    {successMessage && <p className="verification-success">{successMessage}</p>}

                    <div className="verification-actions">
                        <button type="button" className="btn-secondary" onClick={onCancel} disabled={isLoading}>
                            Back
                        </button>
                        <button
                            type="submit"
                            className="auth-submit"
                            style={{width: "auto", padding: "0 20px"}}
                            disabled={isLoading || isExpired || code.length !== 6}
                        >
                            Confirm
                        </button>
                    </div>
                </form>
            </section>
        </div>
    )
}

export default EmailVerificationModal
