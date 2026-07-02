import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import {getCameraImage} from "../../utils/cameraImages.js"
import CameraModal from "./CameraModal.jsx"
import "./Home.css"

const todayIso = () => new Date().toISOString().slice(0, 10)
const tomorrowIso = () => {
    const date = new Date()
    date.setDate(date.getDate() + 1)
    return date.toISOString().slice(0, 10)
}

const Home = ({loggedInUser}) => {
    const [datumOd, setDatumOd] = useState(todayIso)
    const [datumDo, setDatumDo] = useState(tomorrowIso)
    const [fotoaparati, setFotoaparati] = useState([])
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(true)
    const [selectedFotoaparat, setSelectedFotoaparat] = useState(null)
    const [bookingMessage, setBookingMessage] = useState("")
    const [refreshToken, setRefreshToken] = useState(0)

    const rangeInvalid = datumOd && datumDo && datumDo <= datumOd

    useEffect(() => {
        if (rangeInvalid) {
            return
        }

        let cancelled = false

        const fetchFotoaparati = async () => {
            setLoading(true)
            setError("")
            try {
                const response = await fetch(apiUrl(`/api/fotoaparati?datumOd=${datumOd}&datumDo=${datumDo}`))
                if (!response.ok) {
                    if (!cancelled) setError("Fotoaparati ne mogu biti učitani")
                    return
                }
                const data = await response.json()
                if (!cancelled) setFotoaparati(data)
            } catch (error) {
                console.error("Error fetching fotoaparati:", error.message)
                if (!cancelled) setError("Fotoaparati ne mogu biti učitani")
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void fetchFotoaparati()
        return () => {
            cancelled = true
        }
    }, [datumOd, datumDo, rangeInvalid, refreshToken])

    const handleDatumOdChange = (event) => {
        const value = event.target.value
        setDatumOd(value)
        if (datumDo && value && datumDo <= value) {
            const next = new Date(value)
            next.setDate(next.getDate() + 1)
            setDatumDo(next.toISOString().slice(0, 10))
        }
    }

    const brojNoci = (() => {
        if (rangeInvalid || !datumOd || !datumDo) {
            return null
        }
        const diff = (new Date(datumDo) - new Date(datumOd)) / (1000 * 60 * 60 * 24)
        return Math.round(diff)
    })()

    const handleBookingComplete = () => {
        setSelectedFotoaparat(null)
        setBookingMessage("Iznajmljivanje je uspešno potvrđeno! Proverite email za PDF potvrdu.")
        setRefreshToken((token) => token + 1)
        window.setTimeout(() => setBookingMessage(""), 6000)
    }

    return (
        <main className="main-content">
            <h1 className="page-title">Ponuda fotoaparata</h1>
            <p className="page-subtitle">Pronađi i rezerviši opremu za svoj sledeći kadar.</p>

            <section className="filter-bar" aria-label="Filter perioda iznajmljivanja">
                <div className="filter-field">
                    <label htmlFor="datumOd">Datum početka</label>
                    <input
                        id="datumOd"
                        type="date"
                        value={datumOd}
                        onChange={handleDatumOdChange}
                    />
                </div>
                <div className="filter-field">
                    <label htmlFor="datumDo">Datum završetka</label>
                    <input
                        id="datumDo"
                        type="date"
                        min={datumOd}
                        value={datumDo}
                        onChange={(event) => setDatumDo(event.target.value)}
                    />
                </div>

                {rangeInvalid ? (
                    <p className="filter-error">Datum završetka mora biti posle datuma početka.</p>
                ) : (
                    brojNoci !== null && (
                        <span className="filter-summary">
                            {brojNoci} {brojNoci === 1 ? "dan" : "dana"} iznajmljivanja
                        </span>
                    )
                )}
            </section>

            {bookingMessage && <p className="verification-success">{bookingMessage}</p>}
            {error && <p className="error-banner">{error}</p>}

            {!error && !loading && fotoaparati.length === 0 && (
                <p className="empty-state">Trenutno nema fotoaparata u ponudi.</p>
            )}

            <section className="camera-grid" aria-label="Lista fotoaparata">
                {fotoaparati.map((fotoaparat) => (
                    <article
                        className="camera-card"
                        key={fotoaparat.id}
                        onClick={() => setSelectedFotoaparat(fotoaparat)}
                    >
                        <div className="camera-card-art">
                            <img src={getCameraImage(fotoaparat.kategorijaNaziv)} alt={fotoaparat.kategorijaNaziv || "Fotoaparat"}/>
                            {fotoaparat.kategorijaNaziv && (
                                <span className="camera-card-category">{fotoaparat.kategorijaNaziv}</span>
                            )}
                            <span className={`camera-card-badge ${fotoaparat.dostupanZaPeriod ? "available" : "unavailable"}`}>
                                {fotoaparat.dostupanZaPeriod ? "Dostupan" : "Nije dostupan"}
                            </span>
                        </div>

                        <div className="camera-card-body">
                            <p className="manufacturer">{fotoaparat.proizvodjacNaziv || "Nepoznat proizvođač"}</p>
                            <h2>{fotoaparat.rezolucija ? `${fotoaparat.proizvodjacNaziv} · ${fotoaparat.rezolucija}` : fotoaparat.proizvodjacNaziv}</h2>

                            {fotoaparat.opis && <p className="camera-card-desc">{fotoaparat.opis}</p>}

                            <div className="camera-spec-list">
                                {fotoaparat.senzorSlike && (
                                    <div>
                                        <span>Senzor</span>
                                        <strong>{fotoaparat.senzorSlike}</strong>
                                    </div>
                                )}
                                {fotoaparat.ekran && (
                                    <div>
                                        <span>Ekran</span>
                                        <strong>{fotoaparat.ekran}</strong>
                                    </div>
                                )}
                                {fotoaparat.velicinaSlike && (
                                    <div>
                                        <span>Rezolucija slike</span>
                                        <strong>{fotoaparat.velicinaSlike}</strong>
                                    </div>
                                )}
                                {fotoaparat.napajanje && (
                                    <div>
                                        <span>Napajanje</span>
                                        <strong>{fotoaparat.napajanje}</strong>
                                    </div>
                                )}
                            </div>

                            <div className="camera-card-footer">
                                {fotoaparat.wifi ? (
                                    <span className="wifi-chip">Wi-Fi</span>
                                ) : <span/>}
                                {fotoaparat.napomena && <span className="note-text">{fotoaparat.napomena}</span>}
                            </div>
                        </div>
                    </article>
                ))}
            </section>

            {selectedFotoaparat && (
                <CameraModal
                    fotoaparat={selectedFotoaparat}
                    loggedInUser={loggedInUser}
                    onClose={() => setSelectedFotoaparat(null)}
                    onBookingComplete={handleBookingComplete}
                />
            )}
        </main>
    )
}

export default Home
