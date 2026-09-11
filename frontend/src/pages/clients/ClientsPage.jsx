import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import EditClientModal from "./EditClientModal.jsx"
import "../camera/CameraForm.css"
import "./ClientsPage.css"

const ClientsPage = ({loggedInUser}) => {
    const [clients, setClients] = useState([])
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(true)
    const [refreshToken, setRefreshToken] = useState(0)
    const [editingClient, setEditingClient] = useState(null)

    useEffect(() => {
        let cancelled = false

        const fetchClients = async () => {
            setLoading(true)
            setError("")
            try {
                const response = await fetch(apiUrl("/api/clients"), {
                    headers: {"Authorization": `Bearer ${loggedInUser.token}`}
                })
                if (!response.ok) {
                    if (!cancelled) setError("Clients could not be loaded")
                    return
                }
                const data = await response.json()
                if (!cancelled) setClients(data)
            } catch (error) {
                console.error("Error fetching clients:", error.message)
                if (!cancelled) setError("Clients could not be loaded")
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void fetchClients()
        return () => {
            cancelled = true
        }
    }, [loggedInUser.token, refreshToken])

    const handleEditSaved = () => {
        setEditingClient(null)
        setRefreshToken((token) => token + 1)
    }

    const handleDelete = async (client) => {
        if (!window.confirm(`Are you sure you want to delete client "${client.firstName} ${client.lastName}"?`)) {
            return
        }

        try {
            const response = await fetch(apiUrl(`/api/clients/${client.id}`), {
                method: "DELETE",
                headers: {"Authorization": `Bearer ${loggedInUser.token}`}
            })
            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setError(errorData?.fieldErrors?.form ?? "Failed to delete the client")
                return
            }
            setRefreshToken((token) => token + 1)
        } catch (error) {
            console.error("Error deleting client:", error.message)
            setError("Failed to delete the client")
        }
    }

    return (
        <main className="main-content">
            <h1 className="page-title">All clients</h1>
            <p className="page-subtitle">Browse and manage client accounts.</p>

            {error && <p className="error-banner">{error}</p>}

            {!error && !loading && clients.length === 0 && (
                <p className="empty-state">There are no registered clients yet.</p>
            )}

            <section className="client-grid" aria-label="Client list">
                {clients.map((client) => (
                    <article className="client-card" key={client.id}>
                        <div className="client-card-avatar" aria-hidden="true">
                            {(client.firstName?.charAt(0) || client.username?.charAt(0) || "K").toUpperCase()}
                        </div>
                        <div className="client-card-body">
                            <h2>{client.firstName} {client.lastName}</h2>
                            <p className="client-card-username">@{client.username}</p>
                            <div className="client-card-details">
                                <div><span>Email</span><strong>{client.email}</strong></div>
                                {client.age != null && <div><span>Age</span><strong>{client.age}</strong></div>}
                            </div>
                            <div className="camera-card-manage">
                                <button type="button" className="camera-card-edit-btn" onClick={() => setEditingClient(client)}>
                                    Edit
                                </button>
                                <button type="button" className="camera-card-delete-btn" onClick={() => handleDelete(client)}>
                                    Delete
                                </button>
                            </div>
                        </div>
                    </article>
                ))}
            </section>

            {editingClient && (
                <EditClientModal
                    client={editingClient}
                    loggedInUser={loggedInUser}
                    onClose={() => setEditingClient(null)}
                    onSaved={handleEditSaved}
                />
            )}
        </main>
    )
}

export default ClientsPage
