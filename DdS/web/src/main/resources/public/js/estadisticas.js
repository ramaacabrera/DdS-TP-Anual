// Utilidades
function showLoading() {
    document.getElementById("loading-overlay").classList.add("active")
}

function hideLoading() {
    document.getElementById("loading-overlay").classList.remove("active")
}

function showError(containerId, message) {
    const container = document.getElementById(containerId)
    container.innerHTML = `<p class="error-message">Error: ${message}</p>`
}

// Cargar estadísticas generales al inicio
async function cargarEstadisticasGenerales() {
    const API_BASE_URL = 'http://localhost:8088/api/estadisticas';
    try {
        // Cargar spam
        const spamResponse = await fetch(`${API_BASE_URL}/solicitudesSpam`)
        if (spamResponse.ok) {
            const spamData = await spamResponse.json()
            document.getElementById("spam-count").textContent = spamData.spam || spamData.estadisticas_spam || 0
        }

        // Cargar categoría máxima
        const categoriaResponse = await fetch(`${API_BASE_URL}/categoriaMax`)
        if (categoriaResponse.ok) {
            const categoriaData = await categoriaResponse.json()
            document.getElementById("categoria-max").textContent =
                categoriaData.categoria || categoriaData.estadisticas_categoria_max_hechos || "N/A"
        }
    } catch (error) {
        console.error("Error cargando estadísticas generales:", error)
    }
}

// Buscar estadísticas por categoría
async function buscarCategoria() {
    const categoria = document.getElementById("categoria-select").value.trim()

    if (!categoria) {
        showError("categoria-results", "Por favor ingresa una categoría")
        return
    }

    showLoading()
    const resultsContainer = document.getElementById("categoria-results")
    const API_BASE_URL = 'http://localhost:8088/api/estadisticas';

    try {
        // Obtener provincia máxima
        const provinciaResponse = await fetch(`${API_BASE_URL}/provinciaMax/categorias/${encodeURIComponent(categoria)}`)
        const provinciaData = await provinciaResponse.json()

        // Obtener hora máxima
        const horaResponse = await fetch(`${API_BASE_URL}/horaMax/categorias/${encodeURIComponent(categoria)}`)
        const horaData = await horaResponse.json()

        if (provinciaResponse.ok && horaResponse.ok) {
            resultsContainer.innerHTML = `
                <div class="result-item">
                    <span class="result-label">Provincia con más hechos:</span>
                    <span class="result-value">${provinciaData.provincia || provinciaData.estadisticasCategoria_provincia || "N/A"}</span>
                </div>
                <div class="result-item">
                    <span class="result-label">Hora con más hechos:</span>
                    <span class="result-value">${horaData.hora || horaData.estadisticasCategoria_hora || "N/A"}:00 hs</span>
                </div>
            `
        } else {
            showError("categoria-results", "No se encontraron datos para esta categoría")
        }
    } catch (error) {
        showError("categoria-results", "Categoria Invalida: " + error.message)
    } finally {
        hideLoading()
    }
}

// Buscar estadísticas por colección
async function buscarColeccion() {
    const coleccion = document.getElementById("coleccion-select").value.trim()

    if (!coleccion) {
        showError("coleccion-results", "Por favor ingresa un ID de colección")
        return
    }

    showLoading()
    const API_BASE_URL = 'http://localhost:8088/api/estadisticas';
    const resultsContainer = document.getElementById("coleccion-results")

    try {
        const response = await fetch(`${API_BASE_URL}/provinciaMax/colecciones/${encodeURIComponent(coleccion)}`)
        const data = await response.json()

        if (response.ok) {
            resultsContainer.innerHTML = `
                <div class="result-item">
                    <span class="result-label">Provincia con más hechos:</span>
                    <span class="result-value">${data.provincia || data.estadisticasColeccion_provincia || "N/A"}</span>
                </div>
            `
        } else {
            showError("coleccion-results", "No se encontraron datos para esta colección")
        }
    } catch (error) {
        showError("coleccion-results", "Error al buscar la colección: " + error.message)
    } finally {
        hideLoading()
    }
}

// Cargar todas las categorías (ejemplo con categorías comunes)
async function cargarTodasCategorias() {
    const categoriasComunes = ["Incendio", "Choque", "Asesinato", "Terremoto"]
    const container = document.getElementById("todas-categorias")

    showLoading()

    const API_BASE_URL = 'http://localhost:8088/api/estadisticas';

    try {
        const resultados = await Promise.all(
            categoriasComunes.map(async (categoria) => {
                try {
                    const [provinciaRes, horaRes] = await Promise.all([
                        fetch(`${API_BASE_URL}/provinciaMax/categorias/${encodeURIComponent(categoria)}`),
                        fetch(`${API_BASE_URL}/horaMax/categorias/${encodeURIComponent(categoria)}`),
                    ])

                    const provinciaData = await provinciaRes.json()
                    const horaData = await horaRes.json()

                    return {
                        categoria,
                        provincia: provinciaData.provincia || provinciaData.estadisticasCategoria_provincia || "N/A",
                        hora: horaData.hora || horaData.estadisticasCategoria_hora || "N/A",
                    }
                } catch {
                    return null
                }
            }),
        )

        const categoriasValidas = resultados.filter((r) => r !== null)

        if (categoriasValidas.length > 0) {
            container.innerHTML = categoriasValidas
                .map(
                    (cat) => `
                <div class="categoria-card">
                    <h3>${cat.categoria}</h3>
                    <div class="categoria-info">
                        <div class="info-row">
                            <span class="result-label">Provincia:</span>
                            <span class="result-value">${cat.provincia}</span>
                        </div>
                        <div class="info-row">
                            <span class="result-label">Hora pico:</span>
                            <span class="result-value">${cat.hora}:00 hs</span>
                        </div>
                    </div>
                </div>
            `,
                )
                .join("")
        } else {
            container.innerHTML = '<p class="placeholder-text">No se encontraron categorías disponibles</p>'
        }
    } catch (error) {
        container.innerHTML = '<p class="error-message">Error al cargar las categorías</p>'
    } finally {
        hideLoading()
    }
}

// Permitir buscar con Enter
document.addEventListener("DOMContentLoaded", () => {
    cargarEstadisticasGenerales()
    cargarTodasCategorias()

    document.getElementById("categoria-select").addEventListener("keypress", (e) => {
        if (e.key === "Enter") buscarCategoria()
    })

    document.getElementById("coleccion-select").addEventListener("keypress", (e) => {
        if (e.key === "Enter") buscarColeccion()
    })
})
