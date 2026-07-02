import {useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import CameraForm from "./CameraForm.jsx"
import "./CameraForm.css"

const EditCameraModal = ({fotoaparat, loggedInUser, onClose, onSaved}) => {
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [fieldErrors, setFieldErrors] = useState({})

    const handleSubmit = async (payload) => {
        setIsSubmitting(true)
        setFieldErrors({})

        try {
            const response = await fetch(apiUrl(`/api/fotoaparati/${fotoaparat.id}`), {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify(payload)
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Izmena fotoaparata nije uspela"})
                return
            }

            onSaved()
        } catch (error) {
            console.error("Error updating fotoaparat:", error.message)
            setFieldErrors({form: "Izmena fotoaparata nije uspela"})
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <div className="modal-backdrop payment-backdrop" role="presentation" onClick={onClose}>
            <section
                className="camera-form-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="edit-camera-title"
                onClick={(event) => event.stopPropagation()}
            >
                <h2 id="edit-camera-title">Izmeni fotoaparat</h2>
                <CameraForm
                    fotoaparat={fotoaparat}
                    submitLabel="Sačuvaj izmene"
                    isSubmitting={isSubmitting}
                    fieldErrors={fieldErrors}
                    onSubmit={handleSubmit}
                    onCancel={onClose}
                />
            </section>
        </div>
    )
}

export default EditCameraModal
