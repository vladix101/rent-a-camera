import {useState} from "react"
import {useNavigate} from "react-router-dom"
import {apiUrl} from "../../api/apiConfig.js"
import "../camera/CameraForm.css"

const AddCategoryPage = ({loggedInUser}) => {
    const navigate = useNavigate()
    const [naziv, setNaziv] = useState("")
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [fieldErrors, setFieldErrors] = useState({})
    const [successMessage, setSuccessMessage] = useState("")

    const handleSubmit = async (event) => {
        event.preventDefault()
        setIsSubmitting(true)
        setFieldErrors({})
        setSuccessMessage("")

        try {
            const response = await fetch(apiUrl("/api/kategorije"), {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify({naziv})
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Dodavanje kategorije nije uspelo"})
                return
            }

            setNaziv("")
            setSuccessMessage("Kategorija je uspešno dodata.")
        } catch (error) {
            console.error("Error creating kategorija:", error.message)
            setFieldErrors({form: "Dodavanje kategorije nije uspelo"})
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <main className="main-content camera-form-page">
            <h1 className="page-title">Dodaj kategoriju</h1>
            <p className="page-subtitle">Unesite naziv nove kategorije fotoaparata.</p>

            <div className="auth-card camera-form-card">
                <form className="camera-form" onSubmit={handleSubmit}>
                    <div className="auth-field">
                        <label htmlFor="naziv">Naziv kategorije</label>
                        <input
                            id="naziv"
                            name="naziv"
                            type="text"
                            placeholder="npr. DSLR"
                            value={naziv}
                            onChange={(event) => setNaziv(event.target.value)}
                        />
                        {fieldErrors?.naziv && <p className="field-error">{fieldErrors.naziv}</p>}
                    </div>

                    {successMessage && <p className="verification-success">{successMessage}</p>}
                    {fieldErrors?.form && <p className="form-error">{fieldErrors.form}</p>}

                    <div className="verification-actions">
                        <button type="button" className="btn-secondary" onClick={() => navigate("/")} disabled={isSubmitting}>
                            Nazad
                        </button>
                        <button type="submit" className="auth-submit" disabled={isSubmitting} style={{width: "auto", padding: "0 20px"}}>
                            {isSubmitting ? "Čuvanje..." : "Dodaj kategoriju"}
                        </button>
                    </div>
                </form>
            </div>
        </main>
    )
}

export default AddCategoryPage
