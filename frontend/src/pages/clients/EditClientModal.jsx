import {useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"

const EditClientModal = ({client, loggedInUser, onClose, onSaved}) => {
    const [formData, setFormData] = useState({
        firstName: client.firstName ?? "",
        lastName: client.lastName ?? "",
        age: client.age ?? "",
        username: client.username ?? "",
        email: client.email ?? ""
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
            const response = await fetch(apiUrl(`/api/clients/${client.id}`), {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify({
                    ...formData,
                    age: formData.age === "" ? null : Number(formData.age)
                })
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Failed to update the client"})
                return
            }

            onSaved()
        } catch (error) {
            console.error("Error updating client:", error.message)
            setFieldErrors({form: "Failed to update the client"})
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
                <h2 id="edit-client-title">Edit client</h2>

                <form onSubmit={handleSubmit}>
                    <div className="auth-field">
                        <label htmlFor="firstName">First name</label>
                        <input id="firstName" name="firstName" type="text" value={formData.firstName} onChange={handleChange}/>
                        {fieldErrors.firstName && <p className="field-error">{fieldErrors.firstName}</p>}
                    </div>

                    <div className="auth-field">
                        <label htmlFor="lastName">Last name</label>
                        <input id="lastName" name="lastName" type="text" value={formData.lastName} onChange={handleChange}/>
                        {fieldErrors.lastName && <p className="field-error">{fieldErrors.lastName}</p>}
                    </div>

                    <div className="auth-field">
                        <label htmlFor="age">Age</label>
                        <input id="age" name="age" type="number" min="0" value={formData.age} onChange={handleChange}/>
                    </div>

                    <div className="auth-field">
                        <label htmlFor="username">Username</label>
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
                            Cancel
                        </button>
                        <button type="submit" className="auth-submit" disabled={isSubmitting} style={{width: "auto", padding: "0 20px"}}>
                            {isSubmitting ? "Saving..." : "Save changes"}
                        </button>
                    </div>
                </form>
            </section>
        </div>
    )
}

export default EditClientModal
