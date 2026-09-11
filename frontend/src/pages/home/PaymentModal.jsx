import {useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"

const formatDate = (iso) => new Date(iso).toLocaleDateString("en-GB")

const PaymentModal = ({camera, dateFrom, dateTo, loggedInUser, onClose, onSuccess}) => {
    const [formData, setFormData] = useState({
        cardNumber: "",
        cardExpiry: "",
        cvc: ""
    })
    const [fieldErrors, setFieldErrors] = useState({})
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [success, setSuccess] = useState(false)

    const clientName = `${loggedInUser?.firstName ?? ""} ${loggedInUser?.lastName ?? ""}`.trim() || loggedInUser?.username

    const handleChange = (event) => {
        const {name, value} = event.target
        setFormData({...formData, [name]: value})
        setFieldErrors({...fieldErrors, [name]: ""})
    }

    const validate = () => {
        const errors = {}
        const cardNumber = formData.cardNumber.replace(/\s+/g, "")

        if (!/^\d{12,19}$/.test(cardNumber)) {
            errors.cardNumber = "Enter a valid card number"
        }
        if (!formData.cardExpiry.trim()) {
            errors.cardExpiry = "Expiry date is required"
        }
        if (!/^\d{3,4}$/.test(formData.cvc.trim())) {
            errors.cvc = "CVC must be 3 or 4 digits"
        }

        setFieldErrors(errors)
        return Object.keys(errors).length === 0
    }

    const handleSubmit = async (event) => {
        event.preventDefault()
        if (!validate() || isSubmitting) {
            return
        }

        setIsSubmitting(true)

        try {
            const response = await fetch(apiUrl("/api/rentals"), {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify({
                    cameraId: camera.id,
                    dateFrom,
                    dateTo,
                    ...formData
                })
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Payment failed"})
                return
            }

            const rental = await response.json()
            setSuccess(true)
            setTimeout(() => onSuccess(rental), 1400)
        } catch (error) {
            console.error("Error creating rental:", error.message)
            setFieldErrors({form: "Payment failed. Please try again."})
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <div className="modal-backdrop payment-backdrop" role="presentation" onClick={onClose}>
            <section
                className="payment-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="payment-title"
                onClick={(event) => event.stopPropagation()}
            >
                <h2 id="payment-title">Payment</h2>

                <p className="payment-summary">
                    <strong>{camera.manufacturer?.name}</strong>{camera.specification?.resolution ? ` · ${camera.specification.resolution}` : ""}<br/>
                    {formatDate(dateFrom)} - {formatDate(dateTo)}<br/>
                    Client: <strong>{clientName}</strong>
                </p>

                {success ? (
                    <p className="payment-success">Payment successful! Your rental is confirmed and a PDF confirmation has been sent to your email.</p>
                ) : (
                    <form onSubmit={handleSubmit}>
                        <div className="auth-field">
                            <label htmlFor="cardNumber">Card number</label>
                            <input
                                id="cardNumber"
                                name="cardNumber"
                                type="text"
                                inputMode="numeric"
                                placeholder="4111 1111 1111 1111"
                                value={formData.cardNumber}
                                onChange={handleChange}
                            />
                            {fieldErrors.cardNumber && <p className="field-error">{fieldErrors.cardNumber}</p>}
                        </div>

                        <div className="payment-row">
                            <div className="auth-field">
                                <label htmlFor="cardExpiry">Expiry date</label>
                                <input
                                    id="cardExpiry"
                                    name="cardExpiry"
                                    type="text"
                                    placeholder="MM/YY"
                                    value={formData.cardExpiry}
                                    onChange={handleChange}
                                />
                                {fieldErrors.cardExpiry && <p className="field-error">{fieldErrors.cardExpiry}</p>}
                            </div>

                            <div className="auth-field">
                                <label htmlFor="cvc">CVC</label>
                                <input
                                    id="cvc"
                                    name="cvc"
                                    type="text"
                                    inputMode="numeric"
                                    placeholder="123"
                                    value={formData.cvc}
                                    onChange={handleChange}
                                />
                                {fieldErrors.cvc && <p className="field-error">{fieldErrors.cvc}</p>}
                            </div>
                        </div>

                        {fieldErrors.form && <p className="form-error">{fieldErrors.form}</p>}
                        {fieldErrors.cameraId && <p className="form-error">{fieldErrors.cameraId}</p>}
                        {fieldErrors.dateFrom && <p className="form-error">{fieldErrors.dateFrom}</p>}

                        <div className="verification-actions">
                            <button type="button" className="btn-secondary" onClick={onClose} disabled={isSubmitting}>
                                Cancel
                            </button>
                            <button type="submit" className="auth-submit" disabled={isSubmitting} style={{width: "auto", padding: "0 20px"}}>
                                {isSubmitting ? "Processing..." : "Confirm payment"}
                            </button>
                        </div>
                    </form>
                )}
            </section>
        </div>
    )
}

export default PaymentModal
