import {useState} from "react"
import {useLocation, useNavigate} from "react-router-dom"
import {apiUrl} from "../../api/apiConfig.js"

const attemptLogin = async (path, formData) => {
    const response = await fetch(apiUrl(path), {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(formData)
    })
    return response
}

const Login = ({onLogin}) => {
    const navigate = useNavigate()
    const location = useLocation()
    const successMessage = location.state?.successMessage
    const [formData, setFormData] = useState({
        username: "",
        password: ""
    })
    const [error, setError] = useState("")
    const [isSubmitting, setIsSubmitting] = useState(false)

    const handleInputChange = (event) => {
        const {name, value} = event.target
        setFormData({
            ...formData,
            [name]: value
        })
    }

    const handleSubmit = async (event) => {
        event.preventDefault()
        setError("")
        setIsSubmitting(true)

        try {
            let response = await attemptLogin("/api/klijenti/login", formData)

            if (!response.ok) {
                response = await attemptLogin("/api/zaposleni/login", formData)
            }

            if (!response.ok) {
                setError("Pogrešno korisničko ime ili lozinka")
                return
            }

            const loggedInUser = await response.json()
            onLogin(loggedInUser)
            navigate("/")
        } catch (error) {
            console.error("Error logging in:", error.message)
            setError("Prijava nije uspela")
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <main className="auth-shell">
            <div className="auth-card">
                <h1>Prijava</h1>
                <p className="auth-hint">Prijavi se kao klijent ili zaposleni.</p>

                {successMessage && <p className="verification-success">{successMessage}</p>}

                <form onSubmit={handleSubmit}>
                    <div className="auth-field">
                        <label htmlFor="username">Korisničko ime</label>
                        <input
                            id="username"
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleInputChange}
                        />
                    </div>

                    <div className="auth-field">
                        <label htmlFor="password">Lozinka</label>
                        <input
                            id="password"
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleInputChange}
                        />
                    </div>

                    {error && <p className="auth-error">{error}</p>}

                    <button type="submit" className="auth-submit" disabled={isSubmitting}>
                        {isSubmitting ? "Prijavljivanje..." : "Prijavi se"}
                    </button>
                </form>

                <p className="auth-switch-text">
                    Nemate nalog? <a href="/register" onClick={(event) => { event.preventDefault(); navigate("/register") }}>Registruj se</a>
                </p>

                
            </div>
        </main>
    )
}

export default Login
