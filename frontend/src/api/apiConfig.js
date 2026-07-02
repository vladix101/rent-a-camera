const configuredApiBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim()

const resolveApiBaseUrl = () => {
    if (configuredApiBaseUrl) {
        return configuredApiBaseUrl.replace(/\/$/, "")
    }

    const {protocol, hostname} = window.location
    const backendHost = hostname || "localhost"
    return `${protocol}//${backendHost}:8080`
}

export const API_BASE_URL = resolveApiBaseUrl()

export const apiUrl = (path) => `${API_BASE_URL}${path.startsWith("/") ? path : `/${path}`}`
