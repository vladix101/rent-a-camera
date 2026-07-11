import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import {getCameraImage} from "../../utils/cameraImages.js"
import OccupancyCalendar from "./OccupancyCalendar.jsx"
import PaymentModal from "./PaymentModal.jsx"

const CameraModal = ({fotoaparat, loggedInUser, onClose, onBookingComplete}) => {
    const [zauzetost, setZauzetost] = useState([])
    const [selectedStart, setSelectedStart] = useState(null)
    const [selectedEnd, setSelectedEnd] = useState(null)
    const [showPayment, setShowPayment] = useState(false)

    const isKlijent = loggedInUser?.userType === "KLIJENT"

    useEffect(() => {
        const fetchZauzetost = async () => {
            try {
                const response = await fetch(apiUrl(`/api/fotoaparati/${fotoaparat.id}/zauzetost`))
                if (!response.ok) {
                    return
                }
                setZauzetost(await response.json())
            } catch (error) {
                console.error("Error fetching zauzetost:", error.message)
            }
        }

        void fetchZauzetost()
    }, [fotoaparat.id])

    const handleSelect = (start, end) => {
        setSelectedStart(start)
        setSelectedEnd(end)
    }

    const handleBookingSuccess = (iznajmljivanje) => {
        setShowPayment(false)
        onBookingComplete(iznajmljivanje)
    }

    const canBook = isKlijent && selectedStart && selectedEnd

    return (
        <div className="modal-backdrop" role="presentation" onClick={onClose}>
            <section
                className="camera-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="camera-modal-title"
                onClick={(event) => event.stopPropagation()}
            >
                <button type="button" className="camera-modal-close" onClick={onClose} aria-label="Zatvori">×</button>

                <div className="camera-modal-art">
                    <img src={getCameraImage(fotoaparat.kategorija?.naziv)} alt={fotoaparat.kategorija?.naziv || "Fotoaparat"}/>
                    {fotoaparat.kategorija?.naziv && (
                        <span className="camera-card-category">{fotoaparat.kategorija.naziv}</span>
                    )}
                    <span className={`camera-card-badge ${fotoaparat.dostupanZaPeriod ? "available" : "unavailable"}`}>
                        {fotoaparat.dostupanZaPeriod ? "Dostupan" : "Nije dostupan"}
                    </span>
                </div>

                <div className="camera-modal-body">
                    <div className="camera-modal-info">
                        <p className="manufacturer">{fotoaparat.proizvodjac?.name || "Nepoznat proizvođač"}</p>
                        <h2 id="camera-modal-title">
                            {fotoaparat.specifikacija?.rezolucija ? `${fotoaparat.proizvodjac?.name} · ${fotoaparat.specifikacija.rezolucija}` : fotoaparat.proizvodjac?.name}
                        </h2>

                        {fotoaparat.specifikacija?.opis && <p className="description">{fotoaparat.specifikacija.opis}</p>}

                        <div className="camera-modal-specs">
                            {fotoaparat.specifikacija?.senzorSlike && (
                                <div><span>Senzor</span><strong>{fotoaparat.specifikacija.senzorSlike}</strong></div>
                            )}
                            {fotoaparat.specifikacija?.ekran && (
                                <div><span>Ekran</span><strong>{fotoaparat.specifikacija.ekran}</strong></div>
                            )}
                            {fotoaparat.specifikacija?.velicinaSlike && (
                                <div><span>Rezolucija slike</span><strong>{fotoaparat.specifikacija.velicinaSlike}</strong></div>
                            )}
                            {fotoaparat.specifikacija?.napajanje && (
                                <div><span>Napajanje</span><strong>{fotoaparat.specifikacija.napajanje}</strong></div>
                            )}
                            <div><span>Wi-Fi</span><strong>{fotoaparat.specifikacija?.wifi ? "Da" : "Ne"}</strong></div>
                            {fotoaparat.datumKupovine && (
                                <div><span>Datum nabavke</span><strong>{new Date(fotoaparat.datumKupovine).toLocaleDateString("sr-Latn-RS")}</strong></div>
                            )}
                            <div><span>Status</span><strong>{fotoaparat.dostupan ? "U ponudi" : "Van upotrebe"}</strong></div>
                            {fotoaparat.napomena && (
                                <div><span>Napomena</span><strong>{fotoaparat.napomena}</strong></div>
                            )}
                        </div>
                    </div>

                    <div className="camera-modal-booking">
                        <h3>Kalendar dostupnosti</h3>
                        <OccupancyCalendar
                            zauzetost={zauzetost}
                            readOnly={!isKlijent}
                            selectedStart={selectedStart}
                            selectedEnd={selectedEnd}
                            onSelect={handleSelect}
                        />

                        {!loggedInUser && (
                            <p className="calendar-hint guest">
                                Prijavite se kao klijent da biste mogli da izaberete period i iznajmite fotoaparat.
                            </p>
                        )}
                        {loggedInUser && !isKlijent && (
                            <p className="calendar-hint guest">
                                Iznajmljivanje je dostupno samo prijavljenim klijentima.
                            </p>
                        )}
                        {isKlijent && (
                            <p className="calendar-hint">
                                {selectedStart && selectedEnd
                                    ? `Izabrani period: ${selectedStart} - ${selectedEnd}`
                                    : "Izaberite datum početka i datum završetka na kalendaru."}
                            </p>
                        )}

                        {isKlijent && (
                            <button
                                type="button"
                                className="book-button"
                                disabled={!canBook || !fotoaparat.dostupan}
                                onClick={() => setShowPayment(true)}
                            >
                                Iznajmi
                            </button>
                        )}
                    </div>
                </div>
            </section>

            {showPayment && (
                <PaymentModal
                    fotoaparat={fotoaparat}
                    datumOd={selectedStart}
                    datumDo={selectedEnd}
                    loggedInUser={loggedInUser}
                    onClose={() => setShowPayment(false)}
                    onSuccess={handleBookingSuccess}
                />
            )}
        </div>
    )
}

export default CameraModal
