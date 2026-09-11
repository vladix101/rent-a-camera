import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import {getCameraImage} from "../../utils/cameraImages.js"
import "../home/Home.css"

const formatDate = (iso) => new Date(iso).toLocaleDateString("en-GB")

const MyRentals = ({loggedInUser}) => {
    const [rentals, setRentals] = useState([])
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const fetchMyRentals = async () => {
            try {
                const response = await fetch(apiUrl("/api/rentals/my"), {
                    headers: {"Authorization": `Bearer ${loggedInUser.token}`}
                })
                if (!response.ok) {
                    setError("Rentals could not be loaded")
                    return
                }
                setRentals(await response.json())
            } catch (error) {
                console.error("Error fetching my rentals:", error.message)
                setError("Rentals could not be loaded")
            } finally {
                setLoading(false)
            }
        }

        void fetchMyRentals()
    }, [loggedInUser.token])

    return (
        <main className="main-content">
            <h1 className="page-title">My rentals</h1>
            <p className="page-subtitle">All the cameras you have rented.</p>

            {error && <p className="error-banner">{error}</p>}

            {!error && !loading && rentals.length === 0 && (
                <p className="empty-state">You have no rentals yet.</p>
            )}

            <section className="rental-grid" aria-label="My rentals">
                {rentals.map((rental) => (
                    <article className="rental-card" key={rental.id}>
                        <div className="rental-card-art">
                            <img src={getCameraImage(rental.categoryName)} alt={rental.categoryName || "Camera"}/>
                        </div>
                        <div className="rental-card-body">
                            <p className="manufacturer">{rental.categoryName || "Camera"}</p>
                            <h2>
                                {rental.resolution
                                    ? `${rental.manufacturerName} · ${rental.resolution}`
                                    : rental.manufacturerName}
                            </h2>
                            {rental.description && <p className="camera-card-desc">{rental.description}</p>}
                            <span className="rental-period">
                                {formatDate(rental.startDate)} - {formatDate(rental.endDate)}
                            </span>
                        </div>
                    </article>
                ))}
            </section>
        </main>
    )
}

export default MyRentals
