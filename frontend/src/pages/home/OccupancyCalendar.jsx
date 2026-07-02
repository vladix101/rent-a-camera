import {useMemo, useState} from "react"

const MONTH_NAMES = [
    "Januar", "Februar", "Mart", "April", "Maj", "Jun",
    "Jul", "Avgust", "Septembar", "Oktobar", "Novembar", "Decembar"
]
const DAY_LABELS = ["Pon", "Uto", "Sre", "Čet", "Pet", "Sub", "Ned"]

const toIso = (date) => {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, "0")
    const day = String(date.getDate()).padStart(2, "0")
    return `${year}-${month}-${day}`
}

const startOfDay = (date) => {
    const copy = new Date(date)
    copy.setHours(0, 0, 0, 0)
    return copy
}

const buildOccupiedSet = (zauzetost) => {
    const occupied = new Set()
    zauzetost.forEach(({datumOd, datumDo}) => {
        if (!datumOd || !datumDo) {
            return
        }
        let cursor = startOfDay(new Date(datumOd))
        const end = startOfDay(new Date(datumDo))
        while (cursor <= end) {
            occupied.add(toIso(cursor))
            cursor.setDate(cursor.getDate() + 1)
        }
    })
    return occupied
}

const rangeHasOccupiedDay = (occupiedSet, startIso, endIso) => {
    let cursor = startOfDay(new Date(startIso))
    const end = startOfDay(new Date(endIso))
    while (cursor <= end) {
        if (occupiedSet.has(toIso(cursor))) {
            return true
        }
        cursor.setDate(cursor.getDate() + 1)
    }
    return false
}

const OccupancyCalendar = ({zauzetost, readOnly, selectedStart, selectedEnd, onSelect}) => {
    const [viewDate, setViewDate] = useState(() => startOfDay(new Date()))
    const today = useMemo(() => startOfDay(new Date()), [])
    const occupiedSet = useMemo(() => buildOccupiedSet(zauzetost), [zauzetost])

    const year = viewDate.getFullYear()
    const month = viewDate.getMonth()
    const firstOfMonth = new Date(year, month, 1)
    const startWeekday = (firstOfMonth.getDay() + 6) % 7
    const daysInMonth = new Date(year, month + 1, 0).getDate()

    const cells = []
    for (let i = 0; i < startWeekday; i++) {
        cells.push(null)
    }
    for (let day = 1; day <= daysInMonth; day++) {
        cells.push(new Date(year, month, day))
    }

    const handlePrevMonth = () => setViewDate(new Date(year, month - 1, 1))
    const handleNextMonth = () => setViewDate(new Date(year, month + 1, 1))

    const handleDayClick = (date) => {
        if (readOnly || !date) {
            return
        }
        const iso = toIso(date)
        if (date < today || occupiedSet.has(iso)) {
            return
        }

        if (!selectedStart || (selectedStart && selectedEnd)) {
            onSelect(iso, null)
            return
        }

        if (date < startOfDay(new Date(selectedStart))) {
            onSelect(iso, null)
            return
        }

        if (rangeHasOccupiedDay(occupiedSet, selectedStart, iso)) {
            onSelect(iso, null)
            return
        }

        onSelect(selectedStart, iso)
    }

    const dayState = (date) => {
        if (!date) {
            return ""
        }
        const iso = toIso(date)
        const classes = []

        if (date < today) {
            classes.push("cal-day-past")
        } else if (occupiedSet.has(iso)) {
            classes.push("cal-day-occupied")
        } else {
            classes.push("cal-day-free")
        }

        if (toIso(today) === iso) {
            classes.push("cal-day-today")
        }

        if (selectedStart && iso === selectedStart) {
            classes.push("cal-day-selected-start")
        }
        if (selectedEnd && iso === selectedEnd) {
            classes.push("cal-day-selected-end")
        }
        if (selectedStart && selectedEnd && iso > selectedStart && iso < selectedEnd) {
            classes.push("cal-day-in-range")
        }

        return classes.join(" ")
    }

    return (
        <div className="occupancy-calendar">
            <div className="occupancy-calendar-header">
                <button type="button" onClick={handlePrevMonth} aria-label="Prethodni mesec">‹</button>
                <span>{MONTH_NAMES[month]} {year}</span>
                <button type="button" onClick={handleNextMonth} aria-label="Sledeći mesec">›</button>
            </div>

            <div className="occupancy-calendar-weekdays">
                {DAY_LABELS.map((label) => <span key={label}>{label}</span>)}
            </div>

            <div className="occupancy-calendar-grid">
                {cells.map((date, index) => (
                    <button
                        type="button"
                        key={date ? toIso(date) : `empty-${index}`}
                        className={`cal-day ${dayState(date)} ${readOnly ? "cal-readonly" : ""}`}
                        disabled={!date || readOnly}
                        onClick={() => handleDayClick(date)}
                    >
                        {date ? date.getDate() : ""}
                    </button>
                ))}
            </div>

            <div className="occupancy-calendar-legend">
                <span><i className="legend-dot legend-free"/> Slobodno</span>
                <span><i className="legend-dot legend-occupied"/> Zauzeto</span>
                {!readOnly && <span><i className="legend-dot legend-selected"/> Izabrano</span>}
            </div>
        </div>
    )
}

export default OccupancyCalendar
