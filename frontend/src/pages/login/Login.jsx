import {useState} from "react"
import {useNavigate} from "react-router-dom"
import {apiUrl} from "../../api/apiConfig.js"

const Login = ({onLogin}) => {
    const navigate = useNavigate()
    const [formData, setFormData] = useState({
        username: "",
        password: ""
    })
    const [error, setError] = useState("")

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

        try {
            const response = await fetch(apiUrl("/api/login"), {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(formData)
            })

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
        }
    }

    return (
        <main className="auth-shell">
            <div className="auth-card">
                <h1>Prijava</h1>
                <p className="auth-hint">Prijavi se kao klijent ili zaposleni.</p>

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

                    <button type="submit" className="auth-submit">Prijavi se</button>
                </form>

                <div className="auth-hint-box">
                    <strong>Test klijent:</strong> klijent / klijent123<br/>
                    <strong>Test zaposleni:</strong> zaposleni / zaposleni123
                </div>
            </div>
        </main>
    )
}

export default Login
