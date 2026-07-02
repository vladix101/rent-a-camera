import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import {getCameraImage} from "../../utils/cameraImages.js"
import "../home/Home.css"

const formatDateSrb = (iso) => new Date(iso).toLocaleDateString("sr-Latn-RS")

const MyRentals = ({loggedInUser}) => {
    const [iznajmljivanja, setIznajmljivanja] = useState([])
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const fetchMyRentals = async () => {
            try {
                const response = await fetch(apiUrl("/api/iznajmljivanja/moja"), {
                    headers: {"Authorization": `Bearer ${loggedInUser.token}`}
                })
                if (!response.ok) {
                    setError("Iznajmljivanja ne mogu biti učitana")
                    return
                }
                setIznajmljivanja(await response.json())
            } catch (error) {
                console.error("Error fetching moja iznajmljivanja:", error.message)
                setError("Iznajmljivanja ne mogu biti učitana")
            } finally {
                setLoading(false)
            }
        }

        void fetchMyRentals()
    }, [loggedInUser.token])

    return (
        <main className="main-content">
            <h1 className="page-title">Moja iznajmljivanja</h1>
            <p className="page-subtitle">Pregled svih fotoaparata koje ste iznajmili.</p>

            {error && <p className="error-banner">{error}</p>}

            {!error && !loading && iznajmljivanja.length === 0 && (
                <p className="empty-state">Još uvek nemate iznajmljivanja.</p>
            )}

            <section className="rental-grid" aria-label="Moja iznajmljivanja">
                {iznajmljivanja.map((iznajmljivanje) => (
                    <article className="rental-card" key={iznajmljivanje.id}>
                        <div className="rental-card-art">
                            <img src={getCameraImage(iznajmljivanje.kategorijaNaziv)} alt={iznajmljivanje.kategorijaNaziv || "Fotoaparat"}/>
                        </div>
                        <div className="rental-card-body">
                            <p className="manufacturer">{iznajmljivanje.kategorijaNaziv || "Fotoaparat"}</p>
                            <h2>
                                {iznajmljivanje.rezolucija
                                    ? `${iznajmljivanje.proizvodjacNaziv} · ${iznajmljivanje.rezolucija}`
                                    : iznajmljivanje.proizvodjacNaziv}
                            </h2>
                            {iznajmljivanje.opis && <p className="camera-card-desc">{iznajmljivanje.opis}</p>}
                            <span className="rental-period">
                                {formatDateSrb(iznajmljivanje.datumPocetka)} - {formatDateSrb(iznajmljivanje.datumKraja)}
                            </span>
                        </div>
                    </article>
                ))}
            </section>
        </main>
    )
}

export default MyRentals
