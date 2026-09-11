import {useEffect, useMemo, useState} from "react"
import {Cell, Pie, PieChart, ResponsiveContainer, Tooltip} from "recharts"
import {apiUrl} from "../../api/apiConfig.js"
import "./StatisticsPage.css"

const SLICE_COLORS = ["#7c3aed", "#06b6d4", "#f59e0b", "#10b981", "#ec4899", "#ef4444"]
const OTHER_COLOR = "#94a3b8"
const MAX_SLICES = 6

const todayIso = () => new Date().toISOString().slice(0, 10)
const tomorrowIso = () => {
    const date = new Date()
    date.setDate(date.getDate() + 1)
    return date.toISOString().slice(0, 10)
}

const buildChartData = (statsPerCamera) => {
    if (!statsPerCamera || statsPerCamera.length === 0) {
        return []
    }
    const top = statsPerCamera.slice(0, MAX_SLICES)
    const rest = statsPerCamera.slice(MAX_SLICES)
    const chartData = top.map((item) => ({
        name: item.cameraName,
        count: item.rentalCount,
        percentage: item.percentage
    }))
    if (rest.length > 0) {
        chartData.push({
            name: "Other",
            count: rest.reduce((sum, item) => sum + item.rentalCount, 0),
            percentage: Math.round(rest.reduce((sum, item) => sum + (item.percentage ?? 0), 0) * 100) / 100
        })
    }
    return chartData
}

const renderSliceLabel = ({x, y, textAnchor, percent}) => (
    <text
        className="pie-slice-label"
        x={x}
        y={y}
        textAnchor={textAnchor}
        dominantBaseline="middle"
        fill="#1e1b4b"
        fontSize={12}
        fontWeight={700}
    >
        {`${Math.round(percent * 1000) / 10}%`}
    </text>
)

const StatisticsPage = ({loggedInUser}) => {
    const [dateFrom, setDateFrom] = useState(todayIso)
    const [dateTo, setDateTo] = useState(tomorrowIso)
    const [stats, setStats] = useState(null)
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(true)

    const rangeInvalid = dateFrom && dateTo && dateTo <= dateFrom

    useEffect(() => {
        if (rangeInvalid) {
            return
        }

        let cancelled = false

        const fetchStats = async () => {
            setLoading(true)
            setError("")
            try {
                const response = await fetch(
                    apiUrl(`/api/rentals/stats?dateFrom=${dateFrom}&dateTo=${dateTo}`),
                    {headers: {"Authorization": `Bearer ${loggedInUser.token}`}}
                )
                if (!response.ok) {
                    if (!cancelled) setError("Statistics could not be loaded")
                    return
                }
                const data = await response.json()
                if (!cancelled) setStats(data)
            } catch (error) {
                console.error("Error fetching stats:", error.message)
                if (!cancelled) setError("Statistics could not be loaded")
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void fetchStats()
        return () => {
            cancelled = true
        }
    }, [dateFrom, dateTo, rangeInvalid, loggedInUser.token])

    const chartData = useMemo(() => buildChartData(stats?.statsPerCamera), [stats])

    return (
        <main className="main-content">
            <h1 className="page-title">Statistics</h1>
            <p className="page-subtitle">Business overview and how often each camera is rented.</p>

            <section className="filter-bar" aria-label="Availability period">
                <div className="filter-field">
                    <label htmlFor="statDateFrom">Period from</label>
                    <input id="statDateFrom" type="date" value={dateFrom} onChange={(event) => setDateFrom(event.target.value)}/>
                </div>
                <div className="filter-field">
                    <label htmlFor="statDateTo">Period to</label>
                    <input id="statDateTo" type="date" min={dateFrom} value={dateTo} onChange={(event) => setDateTo(event.target.value)}/>
                </div>
                {rangeInvalid && <p className="filter-error">End date must be after the start date.</p>}
            </section>

            {error && <p className="error-banner">{error}</p>}

            {!error && !loading && stats && (
                <>
                    <section className="stats-grid" aria-label="Key figures">
                        <article className="stat-tile">
                            <span className="stat-tile-label">Total rentals</span>
                            <strong className="stat-tile-value">{stats.totalRentals}</strong>
                        </article>
                        <article className="stat-tile">
                            <span className="stat-tile-label">Total clients</span>
                            <strong className="stat-tile-value">{stats.totalClients}</strong>
                        </article>
                        <article className="stat-tile">
                            <span className="stat-tile-label">Total cameras</span>
                            <strong className="stat-tile-value">{stats.totalCameras}</strong>
                        </article>
                        <article className="stat-tile stat-tile-accent">
                            <span className="stat-tile-label">Most rented camera</span>
                            <strong className="stat-tile-value stat-tile-value-text">{stats.mostPopularCamera ?? "No data"}</strong>
                        </article>
                        <article className="stat-tile stat-tile-good">
                            <span className="stat-tile-label">Available in selected period</span>
                            <strong className="stat-tile-value">{stats.availableCameras}</strong>
                        </article>
                        <article className="stat-tile stat-tile-bad">
                            <span className="stat-tile-label">Unavailable</span>
                            <strong className="stat-tile-value">{stats.unavailableCameras}</strong>
                        </article>
                    </section>

                    <section className="chart-card" aria-label="Rental share per camera">
                        <h2>Rental share per camera</h2>

                        {chartData.length === 0 ? (
                            <p className="empty-state">There are no rentals to build statistics from yet.</p>
                        ) : (
                            <div className="chart-card-body">
                                <div className="chart-wrap">
                                    <ResponsiveContainer width="100%" height={340}>
                                        <PieChart>
                                            <Pie
                                                data={chartData}
                                                dataKey="count"
                                                nameKey="name"
                                                cx="50%"
                                                cy="50%"
                                                outerRadius="72%"
                                                label={renderSliceLabel}
                                                labelLine={false}
                                            >
                                                {chartData.map((entry, index) => (
                                                    <Cell
                                                        key={entry.name}
                                                        fill={entry.name === "Other" ? OTHER_COLOR : SLICE_COLORS[index % SLICE_COLORS.length]}
                                                        stroke="#ffffff"
                                                        strokeWidth={2}
                                                    />
                                                ))}
                                            </Pie>
                                            <Tooltip
                                                formatter={(value, name) => [`${value} rentals`, name]}
                                                contentStyle={{
                                                    borderRadius: 12,
                                                    border: "1px solid rgba(30, 27, 75, 0.10)",
                                                    boxShadow: "0 10px 24px rgba(76, 29, 149, 0.16)"
                                                }}
                                            />
                                        </PieChart>
                                    </ResponsiveContainer>
                                </div>

                                <ul className="chart-legend-list">
                                    {stats.statsPerCamera.map((item, index) => (
                                        <li key={item.cameraName + index}>
                                            <span
                                                className="chart-legend-dot"
                                                style={{background: index < MAX_SLICES ? SLICE_COLORS[index % SLICE_COLORS.length] : OTHER_COLOR}}
                                            />
                                            <span className="chart-legend-name">{item.cameraName}</span>
                                            <span className="chart-legend-count">{item.rentalCount}x · {item.percentage}%</span>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        )}
                    </section>
                </>
            )}
        </main>
    )
}

export default StatisticsPage
