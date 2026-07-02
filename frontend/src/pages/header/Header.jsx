import {Container, Navbar} from "react-bootstrap"
import {Link, useNavigate} from "react-router-dom"
import "./Header.css"

const Header = ({loggedInUser, onLogout}) => {
    const navigate = useNavigate()
    const displayName = loggedInUser ? `${loggedInUser.ime ?? ""} ${loggedInUser.prezime ?? ""}`.trim() : ""
    const roleLabel = loggedInUser?.userType === "ZAPOSLENI" ? "Zaposleni" : "Klijent"

    const handleLogout = () => {
        onLogout()
        navigate("/")
    }

    return (
        <Navbar expand="lg" className="fr-navbar">
            <Container>
                <Navbar.Brand as={Link} to="/" className="fr-brand">
                    <span className="fr-brand-icon" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M4 8.5C4 7.67157 4.67157 7 5.5 7H8L9.2 5H14.8L16 7H18.5C19.3284 7 20 7.67157 20 8.5V17.5C20 18.3284 19.3284 19 18.5 19H5.5C4.67157 19 4 18.3284 4 17.5V8.5Z" stroke="currentColor" strokeWidth="1.6" strokeLinejoin="round"/>
                            <circle cx="12" cy="13" r="3.4" stroke="currentColor" strokeWidth="1.6"/>
                        </svg>
                    </span>
                    FotoRent
                </Navbar.Brand>

                <div className="ms-auto fr-nav-links">
                    {!loggedInUser && (
                        <Link className="fr-login-btn" to="/login">
                            Prijava
                        </Link>
                    )}

                    {loggedInUser?.userType === "KLIJENT" && (
                        <Link className="fr-login-btn" to="/moja-iznajmljivanja">
                            Moja iznajmljivanja
                        </Link>
                    )}

                    {loggedInUser?.userType === "ZAPOSLENI" && (
                        <Link className="fr-login-btn" to="/dodaj-aparat">
                            Dodaj aparat
                        </Link>
                    )}

                    {loggedInUser?.userType === "ZAPOSLENI" && (
                        <Link className="fr-login-btn" to="/klijenti">
                            Prikaz svih klijenata
                        </Link>
                    )}

                    {loggedInUser && (
                        <div className="fr-user-summary">
                            <span className="fr-user-avatar" aria-hidden="true">
                                {displayName.charAt(0) || loggedInUser.username?.charAt(0) || "K"}
                            </span>
                            <span className="fr-user-meta">
                                <span className="fr-user-name">{displayName || loggedInUser.username}</span>
                                <span className="fr-user-role">{roleLabel}</span>
                            </span>
                            <button type="button" className="fr-logout-btn" onClick={handleLogout}>
                                Odjava
                            </button>
                        </div>
                    )}
                </div>
            </Container>
        </Navbar>
    )
}

export default Header
