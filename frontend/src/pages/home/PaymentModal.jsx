import {useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"

const formatDateSrb = (iso) => new Date(iso).toLocaleDateString("sr-Latn-RS")

const PaymentModal = ({fotoaparat, datumOd, datumDo, loggedInUser, onClose, onSuccess}) => {
    const [formData, setFormData] = useState({
        brojKartice: "",
        datumIstekaKartice: "",
        cvc: ""
    })
    const [fieldErrors, setFieldErrors] = useState({})
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [success, setSuccess] = useState(false)

    const klijentIme = `${loggedInUser?.ime ?? ""} ${loggedInUser?.prezime ?? ""}`.trim() || loggedInUser?.username

    const handleChange = (event) => {
        const {name, value} = event.target
        setFormData({...formData, [name]: value})
        setFieldErrors({...fieldErrors, [name]: ""})
    }

    const validate = () => {
        const errors = {}
        const brojKartice = formData.brojKartice.replace(/\s+/g, "")

        if (!/^\d{12,19}$/.test(brojKartice)) {
            errors.brojKartice = "Unesite validan broj kartice"
        }
        if (!formData.datumIstekaKartice.trim()) {
            errors.datumIstekaKartice = "Datum isteka je obavezan"
        }
        if (!/^\d{3,4}$/.test(formData.cvc.trim())) {
            errors.cvc = "CVC mora imati 3 ili 4 cifre"
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
            const response = await fetch(apiUrl("/api/iznajmljivanja"), {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify({
                    fotoaparatId: fotoaparat.id,
                    datumOd,
                    datumDo,
                    ...formData
                })
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Plaćanje nije uspelo"})
                return
            }

            const iznajmljivanje = await response.json()
            setSuccess(true)
            setTimeout(() => onSuccess(iznajmljivanje), 1400)
        } catch (error) {
            console.error("Error creating iznajmljivanje:", error.message)
            setFieldErrors({form: "Plaćanje nije uspelo. Pokušajte ponovo."})
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
                <h2 id="payment-title">Plaćanje</h2>

                <p className="payment-summary">
                    <strong>{fotoaparat.proizvodjacNaziv}</strong>{fotoaparat.rezolucija ? ` · ${fotoaparat.rezolucija}` : ""}<br/>
                    {formatDateSrb(datumOd)} - {formatDateSrb(datumDo)}<br/>
                    Klijent: <strong>{klijentIme}</strong>
                </p>

                {success ? (
                    <p className="payment-success">Plaćanje uspešno! Iznajmljivanje je potvrđeno, a PDF potvrda je poslata na vaš email.</p>
                ) : (
                    <form onSubmit={handleSubmit}>
                        <div className="auth-field">
                            <label htmlFor="brojKartice">Broj kartice</label>
                            <input
                                id="brojKartice"
                                name="brojKartice"
                                type="text"
                                inputMode="numeric"
                                placeholder="4111 1111 1111 1111"
                                value={formData.brojKartice}
                                onChange={handleChange}
                            />
                            {fieldErrors.brojKartice && <p className="field-error">{fieldErrors.brojKartice}</p>}
                        </div>

                        <div className="payment-row">
                            <div className="auth-field">
                                <label htmlFor="datumIstekaKartice">Datum isteka</label>
                                <input
                                    id="datumIstekaKartice"
                                    name="datumIstekaKartice"
                                    type="text"
                                    placeholder="MM/GG"
                                    value={formData.datumIstekaKartice}
                                    onChange={handleChange}
                                />
                                {fieldErrors.datumIstekaKartice && <p className="field-error">{fieldErrors.datumIstekaKartice}</p>}
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
                        {fieldErrors.fotoaparatId && <p className="form-error">{fieldErrors.fotoaparatId}</p>}
                        {fieldErrors.datumOd && <p className="form-error">{fieldErrors.datumOd}</p>}

                        <div className="verification-actions">
                            <button type="button" className="btn-secondary" onClick={onClose} disabled={isSubmitting}>
                                Otkaži
                            </button>
                            <button type="submit" className="auth-submit" disabled={isSubmitting} style={{width: "auto", padding: "0 20px"}}>
                                {isSubmitting ? "Obrada..." : "Potvrdi plaćanje"}
                            </button>
                        </div>
                    </form>
                )}
            </section>
        </div>
    )
}

export default PaymentModal
