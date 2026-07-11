import {useEffect, useMemo, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import {getCameraImage} from "../../utils/cameraImages.js"
import CameraModal from "./CameraModal.jsx"
import EditCameraModal from "../camera/EditCameraModal.jsx"
import "./Home.css"
import "../camera/CameraForm.css"

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
    const [searchTerm, setSearchTerm] = useState("")
    const [editingFotoaparat, setEditingFotoaparat] = useState(null)

    const isZaposleni = loggedInUser?.userType === "ZAPOSLENI"
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

    const filteredFotoaparati = useMemo(() => {
        const term = searchTerm.trim().toLowerCase()
        if (!term) {
            return fotoaparati
        }
        return fotoaparati.filter((fotoaparat) => {
            const haystack = [
                fotoaparat.proizvodjac?.name,
                fotoaparat.kategorija?.naziv,
                fotoaparat.specifikacija?.rezolucija,
                fotoaparat.specifikacija?.opis
            ].filter(Boolean).join(" ").toLowerCase()
            return haystack.includes(term)
        })
    }, [fotoaparati, searchTerm])

    const handleEditSaved = () => {
        setEditingFotoaparat(null)
        setRefreshToken((token) => token + 1)
    }

    const handleDelete = async (event, fotoaparat) => {
        event.stopPropagation()
        const displayName = `${fotoaparat.proizvodjac?.name ?? ""} ${fotoaparat.specifikacija?.rezolucija ?? ""}`.trim()
        if (!window.confirm(`Da li ste sigurni da želite da obrišete fotoaparat "${displayName}"?`)) {
            return
        }

        try {
            const response = await fetch(apiUrl(`/api/fotoaparati/${fotoaparat.id}`), {
                method: "DELETE",
                headers: {"Authorization": `Bearer ${loggedInUser.token}`}
            })
            if (!response.ok) {
                setError("Brisanje fotoaparata nije uspelo")
                return
            }
            setRefreshToken((token) => token + 1)
        } catch (error) {
            console.error("Error deleting fotoaparat:", error.message)
            setError("Brisanje fotoaparata nije uspelo")
        }
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

            <section className="search-bar" aria-label="Pretraga fotoaparata">
                <input
                    type="search"
                    className="search-input"
                    placeholder="Pretraži po nazivu, proizvođaču ili kategoriji..."
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                />
            </section>

            {bookingMessage && <p className="verification-success">{bookingMessage}</p>}
            {error && <p className="error-banner">{error}</p>}

            {!error && !loading && filteredFotoaparati.length === 0 && (
                <p className="empty-state">
                    {fotoaparati.length === 0 ? "Trenutno nema fotoaparata u ponudi." : "Nema fotoaparata koji odgovaraju pretrazi."}
                </p>
            )}

            <section className="camera-grid" aria-label="Lista fotoaparata">
                {filteredFotoaparati.map((fotoaparat) => (
                    <article
                        className="camera-card"
                        key={fotoaparat.id}
                        onClick={() => setSelectedFotoaparat(fotoaparat)}
                    >
                        <div className="camera-card-art">
                            <img src={getCameraImage(fotoaparat.kategorija?.naziv)} alt={fotoaparat.kategorija?.naziv || "Fotoaparat"}/>
                            {fotoaparat.kategorija?.naziv && (
                                <span className="camera-card-category">{fotoaparat.kategorija.naziv}</span>
                            )}
                            <span className={`camera-card-badge ${fotoaparat.dostupanZaPeriod ? "available" : "unavailable"}`}>
                                {fotoaparat.dostupanZaPeriod ? "Dostupan" : "Nije dostupan"}
                            </span>
                        </div>

                        <div className="camera-card-body">
                            <p className="manufacturer">{fotoaparat.proizvodjac?.name || "Nepoznat proizvođač"}</p>
                            <h2>{fotoaparat.specifikacija?.rezolucija ? `${fotoaparat.proizvodjac?.name} · ${fotoaparat.specifikacija.rezolucija}` : fotoaparat.proizvodjac?.name}</h2>

                            {fotoaparat.specifikacija?.opis && <p className="camera-card-desc">{fotoaparat.specifikacija.opis}</p>}

                            <div className="camera-spec-list">
                                {fotoaparat.specifikacija?.senzorSlike && (
                                    <div>
                                        <span>Senzor</span>
                                        <strong>{fotoaparat.specifikacija.senzorSlike}</strong>
                                    </div>
                                )}
                                {fotoaparat.specifikacija?.ekran && (
                                    <div>
                                        <span>Ekran</span>
                                        <strong>{fotoaparat.specifikacija.ekran}</strong>
                                    </div>
                                )}
                                {fotoaparat.specifikacija?.velicinaSlike && (
                                    <div>
                                        <span>Rezolucija slike</span>
                                        <strong>{fotoaparat.specifikacija.velicinaSlike}</strong>
                                    </div>
                                )}
                                {fotoaparat.specifikacija?.napajanje && (
                                    <div>
                                        <span>Napajanje</span>
                                        <strong>{fotoaparat.specifikacija.napajanje}</strong>
                                    </div>
                                )}
                            </div>

                            <div className="camera-card-footer">
                                {fotoaparat.specifikacija?.wifi ? (
                                    <span className="wifi-chip">Wi-Fi</span>
                                ) : <span/>}
                                {fotoaparat.napomena && <span className="note-text">{fotoaparat.napomena}</span>}
                            </div>

                            {isZaposleni && (
                                <div className="camera-card-manage">
                                    <button
                                        type="button"
                                        className="camera-card-edit-btn"
                                        onClick={(event) => {
                                            event.stopPropagation()
                                            setEditingFotoaparat(fotoaparat)
                                        }}
                                    >
                                        Izmeni
                                    </button>
                                    <button
                                        type="button"
                                        className="camera-card-delete-btn"
                                        onClick={(event) => handleDelete(event, fotoaparat)}
                                    >
                                        Obriši
                                    </button>
                                </div>
                            )}
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

            {editingFotoaparat && (
                <EditCameraModal
                    fotoaparat={editingFotoaparat}
                    loggedInUser={loggedInUser}
                    onClose={() => setEditingFotoaparat(null)}
                    onSaved={handleEditSaved}
                />
            )}
        </main>
    )
}

export default Home
