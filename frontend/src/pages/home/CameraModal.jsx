import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import {getCameraImage} from "../../utils/cameraImages.js"
import OccupancyCalendar from "./OccupancyCalendar.jsx"
import PaymentModal from "./PaymentModal.jsx"

const CameraModal = ({camera, loggedInUser, onClose, onBookingComplete}) => {
    const [occupancy, setOccupancy] = useState([])
    const [selectedStart, setSelectedStart] = useState(null)
    const [selectedEnd, setSelectedEnd] = useState(null)
    const [showPayment, setShowPayment] = useState(false)

    const isClient = loggedInUser?.userType === "CLIENT"

    useEffect(() => {
        const fetchOccupancy = async () => {
            try {
                const response = await fetch(apiUrl(`/api/cameras/${camera.id}/occupancy`))
                if (!response.ok) {
                    return
                }
                setOccupancy(await response.json())
            } catch (error) {
                console.error("Error fetching occupancy:", error.message)
            }
        }

        void fetchOccupancy()
    }, [camera.id])

    const handleSelect = (start, end) => {
        setSelectedStart(start)
        setSelectedEnd(end)
    }

    const handleBookingSuccess = (rental) => {
        setShowPayment(false)
        onBookingComplete(rental)
    }

    const canBook = isClient && selectedStart && selectedEnd

    return (
        <div className="modal-backdrop" role="presentation" onClick={onClose}>
            <section
                className="camera-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="camera-modal-title"
                onClick={(event) => event.stopPropagation()}
            >
                <button type="button" className="camera-modal-close" onClick={onClose} aria-label="Close">×</button>

                <div className="camera-modal-art">
                    <img src={getCameraImage(camera.category?.name)} alt={camera.category?.name || "Camera"}/>
                    {camera.category?.name && (
                        <span className="camera-card-category">{camera.category.name}</span>
                    )}
                    <span className={`camera-card-badge ${camera.availableForPeriod ? "available" : "unavailable"}`}>
                        {camera.availableForPeriod ? "Available" : "Unavailable"}
                    </span>
                </div>

                <div className="camera-modal-body">
                    <div className="camera-modal-info">
                        <p className="manufacturer">{camera.manufacturer?.name || "Unknown manufacturer"}</p>
                        <h2 id="camera-modal-title">
                            {camera.specification?.resolution ? `${camera.manufacturer?.name} · ${camera.specification.resolution}` : camera.manufacturer?.name}
                        </h2>

                        {camera.specification?.description && <p className="description">{camera.specification.description}</p>}

                        <div className="camera-modal-specs">
                            {camera.specification?.imageSensor && (
                                <div><span>Sensor</span><strong>{camera.specification.imageSensor}</strong></div>
                            )}
                            {camera.specification?.screen && (
                                <div><span>Screen</span><strong>{camera.specification.screen}</strong></div>
                            )}
                            {camera.specification?.imageSize && (
                                <div><span>Image resolution</span><strong>{camera.specification.imageSize}</strong></div>
                            )}
                            {camera.specification?.power && (
                                <div><span>Power</span><strong>{camera.specification.power}</strong></div>
                            )}
                            <div><span>Wi-Fi</span><strong>{camera.specification?.wifi ? "Da" : "Ne"}</strong></div>
                            {camera.purchaseDate && (
                                <div><span>Purchase date</span><strong>{new Date(camera.purchaseDate).toLocaleDateString("en-GB")}</strong></div>
                            )}
                            <div><span>Status</span><strong>{camera.available ? "In fleet" : "Out of service"}</strong></div>
                            {camera.note && (
                                <div><span>Note</span><strong>{camera.note}</strong></div>
                            )}
                        </div>
                    </div>

                    <div className="camera-modal-booking">
                        <h3>Availability calendar</h3>
                        <OccupancyCalendar
                            occupancy={occupancy}
                            readOnly={!isClient}
                            selectedStart={selectedStart}
                            selectedEnd={selectedEnd}
                            onSelect={handleSelect}
                        />

                        {!loggedInUser && (
                            <p className="calendar-hint guest">
                                Sign in as a client to pick a period and rent this camera.
                            </p>
                        )}
                        {loggedInUser && !isClient && (
                            <p className="calendar-hint guest">
                                Renting is available to signed-in clients only.
                            </p>
                        )}
                        {isClient && (
                            <p className="calendar-hint">
                                {selectedStart && selectedEnd
                                    ? `Selected period: ${selectedStart} - ${selectedEnd}`
                                    : "Pick a start date and an end date on the calendar."}
                            </p>
                        )}

                        {isClient && (
                            <button
                                type="button"
                                className="book-button"
                                disabled={!canBook || !camera.available}
                                onClick={() => setShowPayment(true)}
                            >
                                Rent
                            </button>
                        )}
                    </div>
                </div>
            </section>

            {showPayment && (
                <PaymentModal
                    camera={camera}
                    dateFrom={selectedStart}
                    dateTo={selectedEnd}
                    loggedInUser={loggedInUser}
                    onClose={() => setShowPayment(false)}
                    onSuccess={handleBookingSuccess}
                />
            )}
        </div>
    )
}

export default CameraModal
