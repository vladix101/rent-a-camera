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
    const [dateFrom, setDateFrom] = useState(todayIso)
    const [dateTo, setDateTo] = useState(tomorrowIso)
    const [cameras, setCameras] = useState([])
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(true)
    const [selectedCamera, setSelectedCamera] = useState(null)
    const [bookingMessage, setBookingMessage] = useState("")
    const [refreshToken, setRefreshToken] = useState(0)
    const [searchTerm, setSearchTerm] = useState("")
    const [editingCamera, setEditingCamera] = useState(null)

    const isEmployee = loggedInUser?.userType === "EMPLOYEE"
    const rangeInvalid = dateFrom && dateTo && dateTo <= dateFrom

    useEffect(() => {
        if (rangeInvalid) {
            return
        }

        let cancelled = false

        const fetchCameras = async () => {
            setLoading(true)
            setError("")
            try {
                const response = await fetch(apiUrl(`/api/cameras?dateFrom=${dateFrom}&dateTo=${dateTo}`))
                if (!response.ok) {
                    if (!cancelled) setError("Cameras could not be loaded")
                    return
                }
                const data = await response.json()
                if (!cancelled) setCameras(data)
            } catch (error) {
                console.error("Error fetching cameras:", error.message)
                if (!cancelled) setError("Cameras could not be loaded")
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void fetchCameras()
        return () => {
            cancelled = true
        }
    }, [dateFrom, dateTo, rangeInvalid, refreshToken])

    const handleDateFromChange = (event) => {
        const value = event.target.value
        setDateFrom(value)
        if (dateTo && value && dateTo <= value) {
            const next = new Date(value)
            next.setDate(next.getDate() + 1)
            setDateTo(next.toISOString().slice(0, 10))
        }
    }

    const nightCount = (() => {
        if (rangeInvalid || !dateFrom || !dateTo) {
            return null
        }
        const diff = (new Date(dateTo) - new Date(dateFrom)) / (1000 * 60 * 60 * 24)
        return Math.round(diff)
    })()

    const handleBookingComplete = () => {
        setSelectedCamera(null)
        setBookingMessage("Your rental is confirmed! Check your email for the PDF confirmation.")
        setRefreshToken((token) => token + 1)
        window.setTimeout(() => setBookingMessage(""), 6000)
    }

    const filteredCameras = useMemo(() => {
        const term = searchTerm.trim().toLowerCase()
        if (!term) {
            return cameras
        }
        return cameras.filter((camera) => {
            const haystack = [
                camera.manufacturer?.name,
                camera.category?.name,
                camera.specification?.resolution,
                camera.specification?.description
            ].filter(Boolean).join(" ").toLowerCase()
            return haystack.includes(term)
        })
    }, [cameras, searchTerm])

    const handleEditSaved = () => {
        setEditingCamera(null)
        setRefreshToken((token) => token + 1)
    }

    const handleDelete = async (event, camera) => {
        event.stopPropagation()
        const displayName = `${camera.manufacturer?.name ?? ""} ${camera.specification?.resolution ?? ""}`.trim()
        if (!window.confirm(`Are you sure you want to delete camera "${displayName}"?`)) {
            return
        }

        try {
            const response = await fetch(apiUrl(`/api/cameras/${camera.id}`), {
                method: "DELETE",
                headers: {"Authorization": `Bearer ${loggedInUser.token}`}
            })
            if (!response.ok) {
                setError("Failed to delete the camera")
                return
            }
            setRefreshToken((token) => token + 1)
        } catch (error) {
            console.error("Error deleting camera:", error.message)
            setError("Failed to delete the camera")
        }
    }

    return (
        <main className="main-content">
            <h1 className="page-title">Camera fleet</h1>
            <p className="page-subtitle">Find and book the gear for your next shot.</p>

            <section className="filter-bar" aria-label="Rental period filter">
                <div className="filter-field">
                    <label htmlFor="dateFrom">Start date</label>
                    <input
                        id="dateFrom"
                        type="date"
                        value={dateFrom}
                        onChange={handleDateFromChange}
                    />
                </div>
                <div className="filter-field">
                    <label htmlFor="dateTo">End date</label>
                    <input
                        id="dateTo"
                        type="date"
                        min={dateFrom}
                        value={dateTo}
                        onChange={(event) => setDateTo(event.target.value)}
                    />
                </div>

                {rangeInvalid ? (
                    <p className="filter-error">End date must be after the start date.</p>
                ) : (
                    nightCount !== null && (
                        <span className="filter-summary">
                            {nightCount} {nightCount === 1 ? "day" : "days"} of rental
                        </span>
                    )
                )}
            </section>

            <section className="search-bar" aria-label="Camera search">
                <input
                    type="search"
                    className="search-input"
                    placeholder="Search by name, manufacturer or category..."
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                />
            </section>

            {bookingMessage && <p className="verification-success">{bookingMessage}</p>}
            {error && <p className="error-banner">{error}</p>}

            {!error && !loading && filteredCameras.length === 0 && (
                <p className="empty-state">
                    {cameras.length === 0 ? "There are no cameras in the fleet yet." : "No cameras match your search."}
                </p>
            )}

            <section className="camera-grid" aria-label="Camera list">
                {filteredCameras.map((camera) => (
                    <article
                        className="camera-card"
                        key={camera.id}
                        onClick={() => setSelectedCamera(camera)}
                    >
                        <div className="camera-card-art">
                            <img src={getCameraImage(camera.category?.name)} alt={camera.category?.name || "Camera"}/>
                            {camera.category?.name && (
                                <span className="camera-card-category">{camera.category.name}</span>
                            )}
                            <span className={`camera-card-badge ${camera.availableForPeriod ? "available" : "unavailable"}`}>
                                {camera.availableForPeriod ? "Available" : "Unavailable"}
                            </span>
                        </div>

                        <div className="camera-card-body">
                            <p className="manufacturer">{camera.manufacturer?.name || "Unknown manufacturer"}</p>
                            <h2>{camera.specification?.resolution ? `${camera.manufacturer?.name} · ${camera.specification.resolution}` : camera.manufacturer?.name}</h2>

                            {camera.specification?.description && <p className="camera-card-desc">{camera.specification.description}</p>}

                            <div className="camera-spec-list">
                                {camera.specification?.imageSensor && (
                                    <div>
                                        <span>Sensor</span>
                                        <strong>{camera.specification.imageSensor}</strong>
                                    </div>
                                )}
                                {camera.specification?.screen && (
                                    <div>
                                        <span>Screen</span>
                                        <strong>{camera.specification.screen}</strong>
                                    </div>
                                )}
                                {camera.specification?.imageSize && (
                                    <div>
                                        <span>Image resolution</span>
                                        <strong>{camera.specification.imageSize}</strong>
                                    </div>
                                )}
                                {camera.specification?.power && (
                                    <div>
                                        <span>Power</span>
                                        <strong>{camera.specification.power}</strong>
                                    </div>
                                )}
                            </div>

                            <div className="camera-card-footer">
                                {camera.specification?.wifi ? (
                                    <span className="wifi-chip">Wi-Fi</span>
                                ) : <span/>}
                                {camera.note && <span className="note-text">{camera.note}</span>}
                            </div>

                            {isEmployee && (
                                <div className="camera-card-manage">
                                    <button
                                        type="button"
                                        className="camera-card-edit-btn"
                                        onClick={(event) => {
                                            event.stopPropagation()
                                            setEditingCamera(camera)
                                        }}
                                    >
                                        Edit
                                    </button>
                                    <button
                                        type="button"
                                        className="camera-card-delete-btn"
                                        onClick={(event) => handleDelete(event, camera)}
                                    >
                                        Delete
                                    </button>
                                </div>
                            )}
                        </div>
                    </article>
                ))}
            </section>

            {selectedCamera && (
                <CameraModal
                    camera={selectedCamera}
                    loggedInUser={loggedInUser}
                    onClose={() => setSelectedCamera(null)}
                    onBookingComplete={handleBookingComplete}
                />
            )}

            {editingCamera && (
                <EditCameraModal
                    camera={editingCamera}
                    loggedInUser={loggedInUser}
                    onClose={() => setEditingCamera(null)}
                    onSaved={handleEditSaved}
                />
            )}
        </main>
    )
}

export default Home
