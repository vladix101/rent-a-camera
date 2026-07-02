import {useState} from "react"
import {useNavigate} from "react-router-dom"
import {apiUrl} from "../../api/apiConfig.js"
import EmailVerificationModal from "./EmailVerificationModal.jsx"

const Register = () => {
    const navigate = useNavigate()

    const [formData, setFormData] = useState({
        ime: "",
        prezime: "",
        starost: "",
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
            starost: formData.starost === "" ? null : Number(formData.starost)
        }

        setIsRegistering(true)
        try {
            const response = await fetch(apiUrl("/api/klijenti/register"), {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(dataToSend)
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Registracija nije uspela"})
                return
            }

            setPendingData(dataToSend)
            setVerificationError("")
            setVerificationSuccess("")
            setIsVerificationOpen(true)
        } catch (error) {
            console.error("Error registering klijent:", error.message)
            setFieldErrors({form: "Registracija nije uspela"})
        } finally {
            setIsRegistering(false)
        }
    }

    const handleConfirmVerification = async (code) => {
        if (!pendingData) {
            setVerificationError("Podaci za registraciju nedostaju")
            return
        }

        setIsVerificationLoading(true)
        setVerificationError("")
        setVerificationSuccess("")

        try {
            const response = await fetch(apiUrl("/api/klijenti/register/verify"), {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    klijent: pendingData,
                    email: pendingData.email,
                    code
                })
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                const errors = errorData?.fieldErrors ?? {}
                setVerificationError(errors.code ?? errors.form ?? "Verifikacija nije uspela")
                return
            }

            setVerificationSuccess("Email je uspešno verifikovan")
            navigate("/login", {state: {successMessage: "Registracija je uspešno završena. Sada se možete prijaviti."}})
        } catch (error) {
            console.error("Error verifying klijent email:", error.message)
            setVerificationError("Verifikacija nije uspela")
        } finally {
            setIsVerificationLoading(false)
        }
    }

    return (
        <main className="auth-shell">
            <div className="auth-card">
                <h1>Registracija</h1>
                <p className="auth-hint">Napravite klijentski nalog da biste mogli da iznajmljujete fotoaparate.</p>

                <form onSubmit={handleSubmit}>
                    <div className="auth-field">
                        <label htmlFor="ime">Ime</label>
                        <input id="ime" name="ime" type="text" value={formData.ime} onChange={handleChange}/>
                        {fieldErrors.ime && <p className="field-error">{fieldErrors.ime}</p>}
                    </div>

                    <div className="auth-field">
                        <label htmlFor="prezime">Prezime</label>
                        <input id="prezime" name="prezime" type="text" value={formData.prezime} onChange={handleChange}/>
                        {fieldErrors.prezime && <p className="field-error">{fieldErrors.prezime}</p>}
                    </div>

                    <div className="auth-field">
                        <label htmlFor="starost">Starost</label>
                        <input id="starost" name="starost" type="number" min="0" value={formData.starost} onChange={handleChange}/>
                    </div>

                    <div className="auth-field">
                        <label htmlFor="email">Email</label>
                        <input id="email" name="email" type="email" value={formData.email} onChange={handleChange}/>
                        {fieldErrors.email && <p className="field-error">{fieldErrors.email}</p>}
                    </div>

                    <div className="auth-field">
                        <label htmlFor="username">Korisničko ime</label>
                        <input id="username" name="username" type="text" value={formData.username} onChange={handleChange}/>
                        {fieldErrors.username && <p className="field-error">{fieldErrors.username}</p>}
                    </div>

                    <div className="auth-field">
                        <label htmlFor="password">Lozinka</label>
                        <input id="password" name="password" type="password" value={formData.password} onChange={handleChange}/>
                        {fieldErrors.password && <p className="field-error">{fieldErrors.password}</p>}
                    </div>

                    {fieldErrors.form && <p className="form-error">{fieldErrors.form}</p>}

                    <button type="submit" className="auth-submit" disabled={isRegistering}>
                        {isRegistering ? "Slanje..." : "Registruj se"}
                    </button>
                </form>

                <p className="auth-switch-text">
                    Već imate nalog? <a href="/login" onClick={(event) => { event.preventDefault(); navigate("/login") }}>Prijavite se</a>
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
