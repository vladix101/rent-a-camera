import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"

const emptyForm = {
    proizvodjacNaziv: "",
    kategorijaId: "",
    datumKupovine: "",
    napomena: "",
    dostupan: true,
    rezolucija: "",
    senzorSlike: "",
    wifi: false,
    ekran: "",
    napajanje: "",
    velicinaSlike: "",
    opis: ""
}

const toFormValues = (fotoaparat) => {
    if (!fotoaparat) {
        return emptyForm
    }
    return {
        proizvodjacNaziv: fotoaparat.proizvodjacNaziv ?? "",
        kategorijaId: fotoaparat.kategorijaId ?? "",
        datumKupovine: fotoaparat.datumKupovine ?? "",
        napomena: fotoaparat.napomena ?? "",
        dostupan: fotoaparat.dostupan ?? true,
        rezolucija: fotoaparat.rezolucija ?? "",
        senzorSlike: fotoaparat.senzorSlike ?? "",
        wifi: fotoaparat.wifi ?? false,
        ekran: fotoaparat.ekran ?? "",
        napajanje: fotoaparat.napajanje ?? "",
        velicinaSlike: fotoaparat.velicinaSlike ?? "",
        opis: fotoaparat.opis ?? ""
    }
}

const CameraForm = ({fotoaparat, submitLabel, isSubmitting, fieldErrors, onSubmit, onCancel}) => {
    const [formData, setFormData] = useState(() => toFormValues(fotoaparat))
    const [kategorije, setKategorije] = useState([])

    useEffect(() => {
        const fetchKategorije = async () => {
            try {
                const response = await fetch(apiUrl("/api/kategorije"))
                if (!response.ok) {
                    return
                }
                setKategorije(await response.json())
            } catch (error) {
                console.error("Error fetching kategorije:", error.message)
            }
        }

        void fetchKategorije()
    }, [])

    const handleChange = (event) => {
        const {name, value, type, checked} = event.target
        setFormData({...formData, [name]: type === "checkbox" ? checked : value})
    }

    const handleSubmit = (event) => {
        event.preventDefault()
        onSubmit({
            ...formData,
            kategorijaId: formData.kategorijaId === "" ? null : Number(formData.kategorijaId),
            datumKupovine: formData.datumKupovine === "" ? null : formData.datumKupovine
        })
    }

    return (
        <form className="camera-form" onSubmit={handleSubmit}>
            <div className="camera-form-grid">
                <div className="auth-field">
                    <label htmlFor="proizvodjacNaziv">Proizvođač</label>
                    <input
                        id="proizvodjacNaziv"
                        name="proizvodjacNaziv"
                        type="text"
                        placeholder="npr. Canon"
                        value={formData.proizvodjacNaziv}
                        onChange={handleChange}
                    />
                    {fieldErrors?.proizvodjacNaziv && <p className="field-error">{fieldErrors.proizvodjacNaziv}</p>}
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

                <div className="auth-field">
                    <label htmlFor="rezolucija">Rezolucija</label>
                    <input id="rezolucija" name="rezolucija" type="text" placeholder="npr. 24MP" value={formData.rezolucija} onChange={handleChange}/>
                </div>

                <div className="auth-field">
                    <label htmlFor="senzorSlike">Senzor slike</label>
                    <input id="senzorSlike" name="senzorSlike" type="text" placeholder="npr. APS-C CMOS" value={formData.senzorSlike} onChange={handleChange}/>
                </div>

                <div className="auth-field">
                    <label htmlFor="ekran">Ekran</label>
                    <input id="ekran" name="ekran" type="text" placeholder='npr. 3.0" LCD' value={formData.ekran} onChange={handleChange}/>
                </div>

                <div className="auth-field">
                    <label htmlFor="napajanje">Napajanje</label>
                    <input id="napajanje" name="napajanje" type="text" placeholder="npr. Li-ion baterija" value={formData.napajanje} onChange={handleChange}/>
                </div>

                <div className="auth-field">
                    <label htmlFor="velicinaSlike">Rezolucija slike</label>
                    <input id="velicinaSlike" name="velicinaSlike" type="text" placeholder="npr. 6000x4000" value={formData.velicinaSlike} onChange={handleChange}/>
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

            <div className="auth-field">
                <label htmlFor="opis">Specifikacije / opis</label>
                <textarea id="opis" name="opis" rows="4" placeholder="Detaljan opis fotoaparata i specifikacija" value={formData.opis} onChange={handleChange}/>
            </div>

            <div className="camera-form-toggles">
                <label className="camera-form-checkbox">
                    <input type="checkbox" name="wifi" checked={formData.wifi} onChange={handleChange}/>
                    Wi-Fi
                </label>
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
