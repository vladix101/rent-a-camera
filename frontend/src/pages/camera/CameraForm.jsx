import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"

const emptyForm = {
    manufacturerId: "",
    categoryId: "",
    specificationId: "",
    purchaseDate: "",
    note: "",
    available: true
}

const toFormValues = (camera) => {
    if (!camera) {
        return emptyForm
    }
    return {
        manufacturerId: camera.manufacturer?.id ?? "",
        categoryId: camera.category?.id ?? "",
        specificationId: camera.specification?.id ?? "",
        purchaseDate: camera.purchaseDate ?? "",
        note: camera.note ?? "",
        available: camera.available ?? true
    }
}

const specificationLabel = (specification) => {
    const parts = [specification.resolution, specification.imageSensor].filter(Boolean).join(" · ")
    const description = specification.description ? specification.description.slice(0, 40) : ""
    return [parts, description].filter(Boolean).join(" — ") || `Specification #${specification.id}`
}

const CameraForm = ({camera, submitLabel, isSubmitting, fieldErrors, onSubmit, onCancel}) => {
    const [formData, setFormData] = useState(() => toFormValues(camera))
    const [categories, setCategories] = useState([])
    const [manufacturers, setManufacturers] = useState([])
    const [specifications, setSpecifications] = useState([])

    useEffect(() => {
        const fetchOptions = async () => {
            try {
                const [categoriesRes, manufacturersRes, specificationsRes] = await Promise.all([
                    fetch(apiUrl("/api/categories")),
                    fetch(apiUrl("/api/manufacturers")),
                    fetch(apiUrl("/api/specifications"))
                ])
                if (categoriesRes.ok) setCategories(await categoriesRes.json())
                if (manufacturersRes.ok) setManufacturers(await manufacturersRes.json())
                if (specificationsRes.ok) setSpecifications(await specificationsRes.json())
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
            manufacturerId: formData.manufacturerId === "" ? null : Number(formData.manufacturerId),
            categoryId: formData.categoryId === "" ? null : Number(formData.categoryId),
            specificationId: formData.specificationId === "" ? null : Number(formData.specificationId),
            purchaseDate: formData.purchaseDate === "" ? null : formData.purchaseDate
        })
    }

    return (
        <form className="camera-form" onSubmit={handleSubmit}>
            <div className="camera-form-grid">
                <div className="auth-field">
                    <label htmlFor="manufacturerId">Manufacturer</label>
                    <select id="manufacturerId" name="manufacturerId" value={formData.manufacturerId} onChange={handleChange}>
                        <option value="">Select a manufacturer</option>
                        {manufacturers.map((manufacturer) => (
                            <option key={manufacturer.id} value={manufacturer.id}>{manufacturer.name}</option>
                        ))}
                    </select>
                    {fieldErrors?.manufacturerId && <p className="field-error">{fieldErrors.manufacturerId}</p>}
                </div>

                <div className="auth-field">
                    <label htmlFor="categoryId">Category</label>
                    <select id="categoryId" name="categoryId" value={formData.categoryId} onChange={handleChange}>
                        <option value="">Select a category</option>
                        {categories.map((category) => (
                            <option key={category.id} value={category.id}>{category.name}</option>
                        ))}
                    </select>
                    {fieldErrors?.categoryId && <p className="field-error">{fieldErrors.categoryId}</p>}
                </div>

                <div className="auth-field" style={{gridColumn: "1 / -1"}}>
                    <label htmlFor="specificationId">Specification</label>
                    <select id="specificationId" name="specificationId" value={formData.specificationId} onChange={handleChange}>
                        <option value="">Select a specification</option>
                        {specifications.map((specification) => (
                            <option key={specification.id} value={specification.id}>{specificationLabel(specification)}</option>
                        ))}
                    </select>
                    {fieldErrors?.specificationId && <p className="field-error">{fieldErrors.specificationId}</p>}
                </div>

                <div className="auth-field">
                    <label htmlFor="purchaseDate">Purchase date</label>
                    <input id="purchaseDate" name="purchaseDate" type="date" value={formData.purchaseDate} onChange={handleChange}/>
                </div>
            </div>

            <div className="auth-field">
                <label htmlFor="note">Note</label>
                <input id="note" name="note" type="text" placeholder="Internal note about the camera's condition" value={formData.note} onChange={handleChange}/>
            </div>

            <div className="camera-form-toggles">
                <label className="camera-form-checkbox">
                    <input type="checkbox" name="available" checked={formData.available} onChange={handleChange}/>
                    Available for rent
                </label>
            </div>

            {fieldErrors?.form && <p className="form-error">{fieldErrors.form}</p>}

            <div className="verification-actions">
                <button type="button" className="btn-secondary" onClick={onCancel} disabled={isSubmitting}>
                    Cancel
                </button>
                <button type="submit" className="auth-submit" disabled={isSubmitting} style={{width: "auto", padding: "0 20px"}}>
                    {isSubmitting ? "Saving..." : submitLabel}
                </button>
            </div>
        </form>
    )
}

export default CameraForm
