import {useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import CameraForm from "./CameraForm.jsx"
import "./CameraForm.css"

const EditCameraModal = ({camera, loggedInUser, onClose, onSaved}) => {
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [fieldErrors, setFieldErrors] = useState({})

    const handleSubmit = async (payload) => {
        setIsSubmitting(true)
        setFieldErrors({})

        try {
            const response = await fetch(apiUrl(`/api/cameras/${camera.id}`), {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify(payload)
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Failed to update the camera"})
                return
            }

            onSaved()
        } catch (error) {
            console.error("Error updating camera:", error.message)
            setFieldErrors({form: "Failed to update the camera"})
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
                <h2 id="edit-camera-title">Edit camera</h2>
                <CameraForm
                    camera={camera}
                    submitLabel="Save changes"
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
