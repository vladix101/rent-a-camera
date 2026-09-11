import {useState} from "react"
import {useNavigate} from "react-router-dom"
import {apiUrl} from "../../api/apiConfig.js"
import CameraForm from "./CameraForm.jsx"
import "./CameraForm.css"

const AddCameraPage = ({loggedInUser}) => {
    const navigate = useNavigate()
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [fieldErrors, setFieldErrors] = useState({})

    const handleSubmit = async (payload) => {
        setIsSubmitting(true)
        setFieldErrors({})

        try {
            const response = await fetch(apiUrl("/api/cameras"), {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify(payload)
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Failed to add the camera"})
                return
            }

            navigate("/")
        } catch (error) {
            console.error("Error creating camera:", error.message)
            setFieldErrors({form: "Failed to add the camera"})
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <main className="main-content camera-form-page">
            <h1 className="page-title">Add camera</h1>
            <p className="page-subtitle">Enter the details of the new camera being added to the fleet.</p>

            <div className="auth-card camera-form-card">
                <CameraForm
                    camera={null}
                    submitLabel="Add camera"
                    isSubmitting={isSubmitting}
                    fieldErrors={fieldErrors}
                    onSubmit={handleSubmit}
                    onCancel={() => navigate("/")}
                />
            </div>
        </main>
    )
}

export default AddCameraPage
