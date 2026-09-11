import {useState} from "react"
import {useNavigate} from "react-router-dom"
import {apiUrl} from "../../api/apiConfig.js"
import EmailVerificationModal from "./EmailVerificationModal.jsx"

const Register = () => {
    const navigate = useNavigate()

    const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        age: "",
        username: "",
        password: "",
        email: ""
    })
    const [fieldErrors, setFieldErrors] = useState({})
    const [isRegistering, setIsRegistering] = useState(false)
    const [pendingData, setPendingData] = useState(null)
    const [isVerificationOpen, setIsVerificationOpen] = useState(false)
    const [verificationError, setVerificationError] = useState("")
    const [verificationSuccess, setVerificationSuccess] = useState("")
    const [isVerificationLoading, setIsVerificationLoading] = useState(false)

    const handleChange = (event) => {
        const {name, value} = event.target
        setFormData({...formData, [name]: value})
        setFieldErrors({...fieldErrors, [name]: ""})
    }

    const handleSubmit = async (event) => {
        event.preventDefault()
        if (isRegistering) {
            return
        }

        setFieldErrors({})
        const dataToSend = {
            ...formData,
            age: formData.age === "" ? null : Number(formData.age)
        }

        setIsRegistering(true)
        try {
            const response = await fetch(apiUrl("/api/clients/register"), {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(dataToSend)
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Registration failed"})
                return
            }

            setPendingData(dataToSend)
            setVerificationError("")
            setVerificationSuccess("")
            setIsVerificationOpen(true)
        } catch (error) {
            console.error("Error registering client:", error.message)
            setFieldErrors({form: "Registration failed"})
        } finally {
            setIsRegistering(false)
        }
    }

    const handleConfirmVerification = async (code) => {
        if (!pendingData) {
            setVerificationError("Registration data is missing")
            return
        }

        setIsVerificationLoading(true)
        setVerificationError("")
        setVerificationSuccess("")

        try {
            const response = await fetch(apiUrl("/api/clients/register/verify"), {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    client: pendingData,
                    email: pendingData.email,
                    code
                })
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                const errors = errorData?.fieldErrors ?? {}
                setVerificationError(errors.code ?? errors.form ?? "Verification failed")
                return
            }

            setVerificationSuccess("Email verified successfully")
            navigate("/login", {state: {successMessage: "Registration complete. You can sign in now."}})
        } catch (error) {
            console.error("Error verifying client email:", error.message)
            setVerificationError("Verification failed")
        } finally {
            setIsVerificationLoading(false)
        }
    }

    return (
        <main className="auth-shell">
            <div className="auth-card">
                <h1>Sign up</h1>
                <p className="auth-hint">Create a client account so you can rent cameras.</p>

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
                        <label htmlFor="email">Email</label>
                        <input id="email" name="email" type="email" value={formData.email} onChange={handleChange}/>
                        {fieldErrors.email && <p className="field-error">{fieldErrors.email}</p>}
                    </div>

                    <div className="auth-field">
                        <label htmlFor="username">Username</label>
                        <input id="username" name="username" type="text" value={formData.username} onChange={handleChange}/>
                        {fieldErrors.username && <p className="field-error">{fieldErrors.username}</p>}
                    </div>

                    <div className="auth-field">
                        <label htmlFor="password">Password</label>
                        <input id="password" name="password" type="password" value={formData.password} onChange={handleChange}/>
                        {fieldErrors.password && <p className="field-error">{fieldErrors.password}</p>}
                    </div>

                    {fieldErrors.form && <p className="form-error">{fieldErrors.form}</p>}

                    <button type="submit" className="auth-submit" disabled={isRegistering}>
                        {isRegistering ? "Sending..." : "Sign up"}
                    </button>
                </form>

                <p className="auth-switch-text">
                    Already have an account? <a href="/login" onClick={(event) => { event.preventDefault(); navigate("/login") }}>Sign in</a>
                </p>
            </div>

            {isVerificationOpen && (
                <EmailVerificationModal
                    email={pendingData?.email ?? formData.email}
                    isLoading={isVerificationLoading}
                    error={verificationError}
                    successMessage={verificationSuccess}
                    onConfirm={handleConfirmVerification}
                    onCancel={() => setIsVerificationOpen(false)}
                />
            )}
        </main>
    )
}

export default Register
