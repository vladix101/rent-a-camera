import {useState} from "react"
import {useNavigate} from "react-router-dom"
import {apiUrl} from "../../api/apiConfig.js"
import "../camera/CameraForm.css"

const AddCategoryPage = ({loggedInUser}) => {
    const navigate = useNavigate()
    const [name, setName] = useState("")
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [fieldErrors, setFieldErrors] = useState({})
    const [successMessage, setSuccessMessage] = useState("")

    const handleSubmit = async (event) => {
        event.preventDefault()
        setIsSubmitting(true)
        setFieldErrors({})
        setSuccessMessage("")

        try {
            const response = await fetch(apiUrl("/api/categories"), {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify({name})
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Failed to add the category"})
                return
            }

            setName("")
            setSuccessMessage("Category added successfully.")
        } catch (error) {
            console.error("Error creating category:", error.message)
            setFieldErrors({form: "Failed to add the category"})
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <main className="main-content camera-form-page">
            <h1 className="page-title">Add category</h1>
            <p className="page-subtitle">Enter the name of a new camera category.</p>

            <div className="auth-card camera-form-card">
                <form className="camera-form" onSubmit={handleSubmit}>
                    <div className="auth-field">
                        <label htmlFor="name">Category name</label>
                        <input
                            id="name"
                            name="name"
                            type="text"
                            placeholder="e.g. DSLR"
                            value={name}
                            onChange={(event) => setName(event.target.value)}
                        />
                        {fieldErrors?.name && <p className="field-error">{fieldErrors.name}</p>}
                    </div>

                    {successMessage && <p className="verification-success">{successMessage}</p>}
                    {fieldErrors?.form && <p className="form-error">{fieldErrors.form}</p>}

                    <div className="verification-actions">
                        <button type="button" className="btn-secondary" onClick={() => navigate("/")} disabled={isSubmitting}>
                            Back
                        </button>
                        <button type="submit" className="auth-submit" disabled={isSubmitting} style={{width: "auto", padding: "0 20px"}}>
                            {isSubmitting ? "Saving..." : "Add category"}
                        </button>
                    </div>
                </form>
            </div>
        </main>
    )
}

export default AddCategoryPage
