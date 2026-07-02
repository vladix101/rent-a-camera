import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"

const CATEGORY_GRADIENTS = {
    "DSLR": "linear-gradient(135deg, #7c3aed, #4f46e5)",
    "Bezogledalni (Mirrorless)": "linear-gradient(135deg, #ec4899, #db2777)",
    "Kompaktni": "linear-gradient(135deg, #06b6d4, #0284c7)",
    "Akciona kamera": "linear-gradient(135deg, #f59e0b, #ea580c)",
    "Instant": "linear-gradient(135deg, #10b981, #059669)",
    "Video kamera": "linear-gradient(135deg, #ef4444, #db2777)",
}

const DEFAULT_GRADIENT = "linear-gradient(135deg, #8b5cf6, #6366f1)"

const CameraIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M3 8.5C3 7.11929 4.11929 6 5.5 6H7.5L8.7 4H15.3L16.5 6H18.5C19.8807 6 21 7.11929 21 8.5V17.5C21 18.8807 19.8807 20 18.5 20H5.5C4.11929 20 3 18.8807 3 17.5V8.5Z" stroke="white" strokeWidth="1.6" strokeLinejoin="round"/>
        <circle cx="12" cy="13" r="3.6" stroke="white" strokeWidth="1.6"/>
    </svg>
)

const todayIso = () => new Date().toISOString().slice(0, 10)
const tomorrowIso = () => {
    const date = new Date()
    date.setDate(date.getDate() + 1)
    return date.toISOString().slice(0, 10)
}

const Home = () => {
    const [datumOd, setDatumOd] = useState(todayIso)
    const [datumDo, setDatumDo] = useState(tomorrowIso)
    const [fotoaparati, setFotoaparati] = useState([])
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(true)

    const rangeInvalid = datumOd && datumDo && datumDo <= datumOd

    useEffect(() => {
        if (rangeInvalid) {
            return
        }

        const fetchFotoaparati = async () => {
            setLoading(true)
            setError("")
            try {
                const response = await fetch(apiUrl(`/api/fotoaparati?datumOd=${datumOd}&datumDo=${datumDo}`))
                if (!response.ok) {
                    setError("Fotoaparati ne mogu biti učitani")
                    return
                }
                setFotoaparati(await response.json())
            } catch (error) {
                console.error("Error fetching fotoaparati:", error.message)
                setError("Fotoaparati ne mogu biti učitani")
            } finally {
                setLoading(false)
            }
        }

        void fetchFotoaparati()
    }, [datumOd, datumDo, rangeInvalid])

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

            {error && <p className="error-banner">{error}</p>}

            {!error && !loading && fotoaparati.length === 0 && (
                <p className="empty-state">Trenutno nema fotoaparata u ponudi.</p>
            )}

            <section className="camera-grid" aria-label="Lista fotoaparata">
                {fotoaparati.map((fotoaparat) => (
                    <article className="camera-card" key={fotoaparat.id}>
                        <div
                            className="camera-card-art"
                            style={{background: CATEGORY_GRADIENTS[fotoaparat.kategorijaNaziv] || DEFAULT_GRADIENT}}
                        >
                            {fotoaparat.kategorijaNaziv && (
                                <span className="camera-card-category">{fotoaparat.kategorijaNaziv}</span>
                            )}
                            <span className={`camera-card-badge ${fotoaparat.dostupanZaPeriod ? "available" : "unavailable"}`}>
                                {fotoaparat.dostupanZaPeriod ? "Dostupan" : "Nije dostupan"}
                            </span>
                            <CameraIcon/>
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
        </main>
    )
}

export default Home
