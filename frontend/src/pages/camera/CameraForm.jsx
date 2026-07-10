import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"

const emptyForm = {
    proizvodjacId: "",
    kategorijaId: "",
    specifikacijaId: "",
    datumKupovine: "",
    napomena: "",
    dostupan: true
}

const toFormValues = (fotoaparat) => {
    if (!fotoaparat) {
        return emptyForm
    }
    return {
        proizvodjacId: fotoaparat.proizvodjacId ?? "",
        kategorijaId: fotoaparat.kategorijaId ?? "",
        specifikacijaId: fotoaparat.specifikacijaId ?? "",
        datumKupovine: fotoaparat.datumKupovine ?? "",
        napomena: fotoaparat.napomena ?? "",
        dostupan: fotoaparat.dostupan ?? true
    }
}

const opisSpecifikacije = (specifikacija) => {
    const delovi = [specifikacija.rezolucija, specifikacija.senzorSlike].filter(Boolean).join(" · ")
    const opis = specifikacija.opis ? specifikacija.opis.slice(0, 40) : ""
    return [delovi, opis].filter(Boolean).join(" — ") || `Specifikacija #${specifikacija.id}`
}

const CameraForm = ({fotoaparat, submitLabel, isSubmitting, fieldErrors, onSubmit, onCancel}) => {
    const [formData, setFormData] = useState(() => toFormValues(fotoaparat))
    const [kategorije, setKategorije] = useState([])
    const [proizvodjaci, setProizvodjaci] = useState([])
    const [specifikacije, setSpecifikacije] = useState([])

    useEffect(() => {
        const fetchOptions = async () => {
            try {
                const [kategorijeRes, proizvodjaciRes, specifikacijeRes] = await Promise.all([
                    fetch(apiUrl("/api/kategorije")),
                    fetch(apiUrl("/api/proizvodjaci")),
                    fetch(apiUrl("/api/specifikacije"))
                ])
                if (kategorijeRes.ok) setKategorije(await kategorijeRes.json())
                if (proizvodjaciRes.ok) setProizvodjaci(await proizvodjaciRes.json())
                if (specifikacijeRes.ok) setSpecifikacije(await specifikacijeRes.json())
            } catch (error) {
                console.error("Error fetching form options:", error.message)
            }
        }

        void fetchOptions()
    }, [])

    const handleChange = (event) => {
        const {name, value, type, checked} = event.target
        setFormData({...formData, [name]: type === "checkbox" ? checked : value})
    }

    const handleSubmit = (event) => {
        event.preventDefault()
        onSubmit({
            ...formData,
            proizvodjacId: formData.proizvodjacId === "" ? null : Number(formData.proizvodjacId),
            kategorijaId: formData.kategorijaId === "" ? null : Number(formData.kategorijaId),
            specifikacijaId: formData.specifikacijaId === "" ? null : Number(formData.specifikacijaId),
            datumKupovine: formData.datumKupovine === "" ? null : formData.datumKupovine
        })
    }

    return (
        <form className="camera-form" onSubmit={handleSubmit}>
            <div className="camera-form-grid">
                <div className="auth-field">
                    <label htmlFor="proizvodjacId">Proizvođač</label>
                    <select id="proizvodjacId" name="proizvodjacId" value={formData.proizvodjacId} onChange={handleChange}>
                        <option value="">Izaberite proizvođača</option>
                        {proizvodjaci.map((proizvodjac) => (
                            <option key={proizvodjac.id} value={proizvodjac.id}>{proizvodjac.name}</option>
                        ))}
                    </select>
                    {fieldErrors?.proizvodjacId && <p className="field-error">{fieldErrors.proizvodjacId}</p>}
                </div>

                <div className="auth-field">
                    <label htmlFor="kategorijaId">Kategorija</label>
                    <select id="kategorijaId" name="kategorijaId" value={formData.kategorijaId} onChange={handleChange}>
                        <option value="">Izaberite kategoriju</option>
                        {kategorije.map((kategorija) => (
                            <option key={kategorija.id} value={kategorija.id}>{kategorija.naziv}</option>
                        ))}
                    </select>
                    {fieldErrors?.kategorijaId && <p className="field-error">{fieldErrors.kategorijaId}</p>}
                </div>

                <div className="auth-field" style={{gridColumn: "1 / -1"}}>
                    <label htmlFor="specifikacijaId">Specifikacija</label>
                    <select id="specifikacijaId" name="specifikacijaId" value={formData.specifikacijaId} onChange={handleChange}>
                        <option value="">Izaberite specifikaciju</option>
                        {specifikacije.map((specifikacija) => (
                            <option key={specifikacija.id} value={specifikacija.id}>{opisSpecifikacije(specifikacija)}</option>
                        ))}
                    </select>
                    {fieldErrors?.specifikacijaId && <p className="field-error">{fieldErrors.specifikacijaId}</p>}
                </div>

                <div className="auth-field">
                    <label htmlFor="datumKupovine">Datum nabavke</label>
                    <input id="datumKupovine" name="datumKupovine" type="date" value={formData.datumKupovine} onChange={handleChange}/>
                </div>
            </div>

            <div className="auth-field">
                <label htmlFor="napomena">Napomena</label>
                <input id="napomena" name="napomena" type="text" placeholder="Interna napomena o stanju aparata" value={formData.napomena} onChange={handleChange}/>
            </div>

            <div className="camera-form-toggles">
                <label className="camera-form-checkbox">
                    <input type="checkbox" name="dostupan" checked={formData.dostupan} onChange={handleChange}/>
                    Dostupan u ponudi
                </label>
            </div>

            {fieldErrors?.form && <p className="form-error">{fieldErrors.form}</p>}

            <div className="verification-actions">
                <button type="button" className="btn-secondary" onClick={onCancel} disabled={isSubmitting}>
                    Otkaži
                </button>
                <button type="submit" className="auth-submit" disabled={isSubmitting} style={{width: "auto", padding: "0 20px"}}>
                    {isSubmitting ? "Čuvanje..." : submitLabel}
                </button>
            </div>
        </form>
    )
}

export default CameraForm
