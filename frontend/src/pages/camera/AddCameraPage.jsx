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
            const response = await fetch(apiUrl("/api/fotoaparati"), {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify(payload)
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Dodavanje fotoaparata nije uspelo"})
                return
            }

            navigate("/")
        } catch (error) {
            console.error("Error creating fotoaparat:", error.message)
            setFieldErrors({form: "Dodavanje fotoaparata nije uspelo"})
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <main className="main-content camera-form-page">
            <h1 className="page-title">Dodaj fotoaparat</h1>
            <p className="page-subtitle">Unesite podatke o novom fotoaparatu koji ulazi u ponudu.</p>

            <div className="auth-card camera-form-card">
                <CameraForm
                    fotoaparat={null}
                    submitLabel="Dodaj fotoaparat"
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
