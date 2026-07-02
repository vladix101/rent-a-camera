import {useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"

const EditClientModal = ({klijent, loggedInUser, onClose, onSaved}) => {
    const [formData, setFormData] = useState({
        ime: klijent.ime ?? "",
        prezime: klijent.prezime ?? "",
        starost: klijent.starost ?? "",
        username: klijent.username ?? "",
        email: klijent.email ?? ""
    })
    const [fieldErrors, setFieldErrors] = useState({})
    const [isSubmitting, setIsSubmitting] = useState(false)

    const handleChange = (event) => {
        const {name, value} = event.target
        setFormData({...formData, [name]: value})
        setFieldErrors({...fieldErrors, [name]: ""})
    }

    const handleSubmit = async (event) => {
        event.preventDefault()
        if (isSubmitting) {
            return
        }

        setIsSubmitting(true)
        setFieldErrors({})

        try {
            const response = await fetch(apiUrl(`/api/klijenti/${klijent.id}`), {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify({
                    ...formData,
                    starost: formData.starost === "" ? null : Number(formData.starost)
                })
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Izmena klijenta nije uspela"})
                return
            }

            onSaved()
        } catch (error) {
            console.error("Error updating klijent:", error.message)
            setFieldErrors({form: "Izmena klijenta nije uspela"})
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <div className="modal-backdrop payment-backdrop" role="presentation" onClick={onClose}>
            <section
                className="verification-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="edit-client-title"
                onClick={(event) => event.stopPropagation()}
            >
                <h2 id="edit-client-title">Izmeni klijenta</h2>

                <form onSubmit={handleSubmit}>
                    <div className="auth-field">
                        <label htmlFor="ime">Ime</label>
                        <input id="ime" name="ime" type="text" value={formData.ime} onChange={handleChange}/>
                        {fieldErrors.ime && <p className="field-error">{fieldErrors.ime}</p>}
                    </div>

                    <div className="auth-field">
                        <label htmlFor="prezime">Prezime</label>
                        <input id="prezime" name="prezime" type="text" value={formData.prezime} onChange={handleChange}/>
                        {fieldErrors.prezime && <p className="field-error">{fieldErrors.prezime}</p>}
                    </div>

                    <div className="auth-field">
                        <label htmlFor="starost">Starost</label>
                        <input id="starost" name="starost" type="number" min="0" value={formData.starost} onChange={handleChange}/>
                    </div>

                    <div className="auth-field">
                        <label htmlFor="username">Korisničko ime</label>
                        <input id="username" name="username" type="text" value={formData.username} onChange={handleChange}/>
                        {fieldErrors.username && <p className="field-error">{fieldErrors.username}</p>}
                    </div>

                    <div className="auth-field">
                        <label htmlFor="email">Email</label>
                        <input id="email" name="email" type="email" value={formData.email} onChange={handleChange}/>
                        {fieldErrors.email && <p className="field-error">{fieldErrors.email}</p>}
                    </div>

                    {fieldErrors.form && <p className="form-error">{fieldErrors.form}</p>}

                    <div className="verification-actions">
                        <button type="button" className="btn-secondary" onClick={onClose} disabled={isSubmitting}>
                            Otkaži
                        </button>
                        <button type="submit" className="auth-submit" disabled={isSubmitting} style={{width: "auto", padding: "0 20px"}}>
                            {isSubmitting ? "Čuvanje..." : "Sačuvaj izmene"}
                        </button>
                    </div>
                </form>
            </section>
        </div>
    )
}

export default EditClientModal
