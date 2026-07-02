import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import EditClientModal from "./EditClientModal.jsx"
import "../camera/CameraForm.css"
import "./ClientsPage.css"

const ClientsPage = ({loggedInUser}) => {
    const [klijenti, setKlijenti] = useState([])
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(true)
    const [refreshToken, setRefreshToken] = useState(0)
    const [editingKlijent, setEditingKlijent] = useState(null)

    useEffect(() => {
        let cancelled = false

        const fetchKlijenti = async () => {
            setLoading(true)
            setError("")
            try {
                const response = await fetch(apiUrl("/api/klijenti"), {
                    headers: {"Authorization": `Bearer ${loggedInUser.token}`}
                })
                if (!response.ok) {
                    if (!cancelled) setError("Klijenti ne mogu biti učitani")
                    return
                }
                const data = await response.json()
                if (!cancelled) setKlijenti(data)
            } catch (error) {
                console.error("Error fetching klijenti:", error.message)
                if (!cancelled) setError("Klijenti ne mogu biti učitani")
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void fetchKlijenti()
        return () => {
            cancelled = true
        }
    }, [loggedInUser.token, refreshToken])

    const handleEditSaved = () => {
        setEditingKlijent(null)
        setRefreshToken((token) => token + 1)
    }

    const handleDelete = async (klijent) => {
        if (!window.confirm(`Da li ste sigurni da želite da obrišete klijenta "${klijent.ime} ${klijent.prezime}"?`)) {
            return
        }

        try {
            const response = await fetch(apiUrl(`/api/klijenti/${klijent.id}`), {
                method: "DELETE",
                headers: {"Authorization": `Bearer ${loggedInUser.token}`}
            })
            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setError(errorData?.fieldErrors?.form ?? "Brisanje klijenta nije uspelo")
                return
            }
            setRefreshToken((token) => token + 1)
        } catch (error) {
            console.error("Error deleting klijent:", error.message)
            setError("Brisanje klijenta nije uspelo")
        }
    }

    return (
        <main className="main-content">
            <h1 className="page-title">Prikaz svih klijenata</h1>
            <p className="page-subtitle">Pregled i upravljanje klijentskim nalozima.</p>

            {error && <p className="error-banner">{error}</p>}

            {!error && !loading && klijenti.length === 0 && (
                <p className="empty-state">Trenutno nema registrovanih klijenata.</p>
            )}

            <section className="client-grid" aria-label="Lista klijenata">
                {klijenti.map((klijent) => (
                    <article className="client-card" key={klijent.id}>
                        <div className="client-card-avatar" aria-hidden="true">
                            {(klijent.ime?.charAt(0) || klijent.username?.charAt(0) || "K").toUpperCase()}
                        </div>
                        <div className="client-card-body">
                            <h2>{klijent.ime} {klijent.prezime}</h2>
                            <p className="client-card-username">@{klijent.username}</p>
                            <div className="client-card-details">
                                <div><span>Email</span><strong>{klijent.email}</strong></div>
                                {klijent.starost != null && <div><span>Starost</span><strong>{klijent.starost}</strong></div>}
                            </div>
                            <div className="camera-card-manage">
                                <button type="button" className="camera-card-edit-btn" onClick={() => setEditingKlijent(klijent)}>
                                    Izmeni
                                </button>
                                <button type="button" className="camera-card-delete-btn" onClick={() => handleDelete(klijent)}>
                                    Obriši
                                </button>
                            </div>
                        </div>
                    </article>
                ))}
            </section>

            {editingKlijent && (
                <EditClientModal
                    klijent={editingKlijent}
                    loggedInUser={loggedInUser}
                    onClose={() => setEditingKlijent(null)}
                    onSaved={handleEditSaved}
                />
            )}
        </main>
    )
}

export default ClientsPage
