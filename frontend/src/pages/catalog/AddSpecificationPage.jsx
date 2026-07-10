import {useState} from "react"
import {useNavigate} from "react-router-dom"
import {apiUrl} from "../../api/apiConfig.js"
import "../camera/CameraForm.css"

const emptyForm = {
    rezolucija: "",
    senzorSlike: "",
    wifi: false,
    ekran: "",
    napajanje: "",
    velicinaSlike: "",
    opis: ""
}

const AddSpecificationPage = ({loggedInUser}) => {
    const navigate = useNavigate()
    const [formData, setFormData] = useState(emptyForm)
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [fieldErrors, setFieldErrors] = useState({})
    const [successMessage, setSuccessMessage] = useState("")

    const handleChange = (event) => {
        const {name, value, type, checked} = event.target
        setFormData({...formData, [name]: type === "checkbox" ? checked : value})
    }

    const handleSubmit = async (event) => {
        event.preventDefault()
        setIsSubmitting(true)
        setFieldErrors({})
        setSuccessMessage("")

        try {
            const response = await fetch(apiUrl("/api/specifikacije"), {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify(formData)
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Dodavanje specifikacije nije uspelo"})
                return
            }

            setFormData(emptyForm)
            setSuccessMessage("Specifikacija je uspešno dodata.")
        } catch (error) {
            console.error("Error creating specifikacija:", error.message)
            setFieldErrors({form: "Dodavanje specifikacije nije uspelo"})
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <main className="main-content camera-form-page">
            <h1 className="page-title">Dodaj specifikaciju</h1>
            <p className="page-subtitle">Unesite tehničke specifikacije koje se mogu dodeliti fotoaparatu.</p>

            <div className="auth-card camera-form-card">
                <form className="camera-form" onSubmit={handleSubmit}>
                    <div className="camera-form-grid">
                        <div className="auth-field">
                            <label htmlFor="rezolucija">Rezolucija</label>
                            <input id="rezolucija" name="rezolucija" type="text" placeholder="npr. 24MP" value={formData.rezolucija} onChange={handleChange}/>
                            {fieldErrors?.rezolucija && <p className="field-error">{fieldErrors.rezolucija}</p>}
                        </div>

                        <div className="auth-field">
                            <label htmlFor="senzorSlike">Senzor slike</label>
                            <input id="senzorSlike" name="senzorSlike" type="text" placeholder="npr. APS-C CMOS" value={formData.senzorSlike} onChange={handleChange}/>
                            {fieldErrors?.senzorSlike && <p className="field-error">{fieldErrors.senzorSlike}</p>}
                        </div>

                        <div className="auth-field">
                            <label htmlFor="ekran">Ekran</label>
                            <input id="ekran" name="ekran" type="text" placeholder='npr. 3.0" LCD' value={formData.ekran} onChange={handleChange}/>
                        </div>

                        <div className="auth-field">
                            <label htmlFor="napajanje">Napajanje</label>
                            <input id="napajanje" name="napajanje" type="text" placeholder="npr. Li-ion baterija" value={formData.napajanje} onChange={handleChange}/>
                        </div>

                        <div className="auth-field">
                            <label htmlFor="velicinaSlike">Rezolucija slike</label>
                            <input id="velicinaSlike" name="velicinaSlike" type="text" placeholder="npr. 6000x4000" value={formData.velicinaSlike} onChange={handleChange}/>
                        </div>
                    </div>

                    <div className="auth-field">
                        <label htmlFor="opis">Opis</label>
                        <textarea id="opis" name="opis" rows="4" placeholder="Detaljan opis specifikacije" value={formData.opis} onChange={handleChange}/>
                    </div>

                    <div className="camera-form-toggles">
                        <label className="camera-form-checkbox">
                            <input type="checkbox" name="wifi" checked={formData.wifi} onChange={handleChange}/>
                            Wi-Fi
                        </label>
                    </div>

                    {successMessage && <p className="verification-success">{successMessage}</p>}
                    {fieldErrors?.form && <p className="form-error">{fieldErrors.form}</p>}

                    <div className="verification-actions">
                        <button type="button" className="btn-secondary" onClick={() => navigate("/")} disabled={isSubmitting}>
                            Nazad
                        </button>
                        <button type="submit" className="auth-submit" disabled={isSubmitting} style={{width: "auto", padding: "0 20px"}}>
                            {isSubmitting ? "Čuvanje..." : "Dodaj specifikaciju"}
                        </button>
                    </div>
                </form>
            </div>
        </main>
    )
}

export default AddSpecificationPage
