import {useState} from "react"
import {useNavigate} from "react-router-dom"
import {apiUrl} from "../../api/apiConfig.js"
import "../camera/CameraForm.css"

const emptyForm = {
    resolution: "",
    imageSensor: "",
    wifi: false,
    screen: "",
    power: "",
    imageSize: "",
    description: ""
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
            const response = await fetch(apiUrl("/api/specifications"), {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify(formData)
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Failed to add the specification"})
                return
            }

            setFormData(emptyForm)
            setSuccessMessage("Specification added successfully.")
        } catch (error) {
            console.error("Error creating specification:", error.message)
            setFieldErrors({form: "Failed to add the specification"})
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <main className="main-content camera-form-page">
            <h1 className="page-title">Add specification</h1>
            <p className="page-subtitle">Enter the technical specification that can be assigned to a camera.</p>

            <div className="auth-card camera-form-card">
                <form className="camera-form" onSubmit={handleSubmit}>
                    <div className="camera-form-grid">
                        <div className="auth-field">
                            <label htmlFor="resolution">Resolution</label>
                            <input id="resolution" name="resolution" type="text" placeholder="e.g. 24MP" value={formData.resolution} onChange={handleChange}/>
                            {fieldErrors?.resolution && <p className="field-error">{fieldErrors.resolution}</p>}
                        </div>

                        <div className="auth-field">
                            <label htmlFor="imageSensor">Image sensor</label>
                            <input id="imageSensor" name="imageSensor" type="text" placeholder="e.g. APS-C CMOS" value={formData.imageSensor} onChange={handleChange}/>
                            {fieldErrors?.imageSensor && <p className="field-error">{fieldErrors.imageSensor}</p>}
                        </div>

                        <div className="auth-field">
                            <label htmlFor="screen">Screen</label>
                            <input id="screen" name="screen" type="text" placeholder='e.g. 3.0" LCD' value={formData.screen} onChange={handleChange}/>
                        </div>

                        <div className="auth-field">
                            <label htmlFor="power">Power</label>
                            <input id="power" name="power" type="text" placeholder="e.g. Li-ion battery" value={formData.power} onChange={handleChange}/>
                        </div>

                        <div className="auth-field">
                            <label htmlFor="imageSize">Image resolution</label>
                            <input id="imageSize" name="imageSize" type="text" placeholder="e.g. 6000x4000" value={formData.imageSize} onChange={handleChange}/>
                        </div>
                    </div>

                    <div className="auth-field">
                        <label htmlFor="description">Description</label>
                        <textarea id="description" name="description" rows="4" placeholder="Detailed description of the specification" value={formData.description} onChange={handleChange}/>
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
                            Back
                        </button>
                        <button type="submit" className="auth-submit" disabled={isSubmitting} style={{width: "auto", padding: "0 20px"}}>
                            {isSubmitting ? "Saving..." : "Add specification"}
                        </button>
                    </div>
                </form>
            </div>
        </main>
    )
}

export default AddSpecificationPage
