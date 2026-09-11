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
            let response = await attemptLogin("/api/clients/login", formData)

            if (!response.ok) {
                response = await attemptLogin("/api/employees/login", formData)
            }

            if (!response.ok) {
                setError("Incorrect username or password")
                return
            }

            const loggedInUser = await response.json()
            onLogin(loggedInUser)
            navigate("/")
        } catch (error) {
            console.error("Error logging in:", error.message)
            setError("Sign-in failed")
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <main className="auth-shell">
            <div className="auth-card">
                <h1>Sign in</h1>
                <p className="auth-hint">Sign in as a client or an employee.</p>

                {successMessage && <p className="verification-success">{successMessage}</p>}

                <form onSubmit={handleSubmit}>
                    <div className="auth-field">
                        <label htmlFor="username">Username</label>
                        <input
                            id="username"
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleInputChange}
                        />
                    </div>

                    <div className="auth-field">
                        <label htmlFor="password">Password</label>
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
                        {isSubmitting ? "Signing in..." : "Sign in"}
                    </button>
                </form>

                <p className="auth-switch-text">
                    Don't have an account? <a href="/register" onClick={(event) => { event.preventDefault(); navigate("/register") }}>Sign up</a>
                </p>

                
            </div>
        </main>
    )
}

export default Login
