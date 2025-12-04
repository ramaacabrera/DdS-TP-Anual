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

async function buscarCategoria() {
    const input = document.getElementById('categoria-select');
    const resultsContainer = document.getElementById('categoria-results');
    const categoria = input.value.trim();

    if (!categoria) {
        alert("Por favor ingrese una categoría");
        return;
    }

    // Mostrar estado de carga
    resultsContainer.innerHTML = '<div class="spinner" style="border-width: 2px; width: 30px; height: 30px;"></div>';

    try {
        // Codificamos la categoría para URL
        const encodedCat = encodeURIComponent(categoria);

        // Hacemos las dos peticiones en paralelo
        const [resProvincia, resHora] = await Promise.all([
            fetch(`/api/estadisticas/provinciaMax/categorias/${encodedCat}`),
            fetch(`/api/estadisticas/horaMax/categorias/${encodedCat}`)
        ]);

        const dataProvincia = await resProvincia.json();
        const dataHora = await resHora.json();

        // Valores por defecto si hay error
        const provinciaVal = dataProvincia.provincia || "N/A";
        const horaVal = dataHora.hora ? `${dataHora.hora}:00 hs` : "N/A";

        // --- AQUÍ ESTÁ EL CAMBIO DE DISEÑO ---
        // Generamos un HTML ordenado con Grid y estilos limpios
        resultsContainer.innerHTML = `
            <div class="result-success-container">
                <div class="result-header">
                    <h3>Resultados para: <strong>${categoria}</strong></h3>
                </div>
                <div class="result-grid">
                    <div class="result-stat">
                        <span class="result-label">Provincia más activa</span>
                        <span class="result-value" style="color: var(--brand-dark);">${provinciaVal}</span>
                    </div>
                    <div class="result-stat">
                        <span class="result-label">Hora pico</span>
                        <span class="result-value" style="color: var(--brand-primary);">${horaVal}</span>
                    </div>
                </div>
            </div>
        `;

    } catch (error) {
        console.error(error);
        resultsContainer.innerHTML = `
            <div style="color: #e57373; text-align: center;">
                <p>No se encontraron datos para "${categoria}"</p>
            </div>
        `;
    }
}

document.getElementById('categoria-select').addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        buscarCategoria();
    }
});

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
// async function cargarTodasCategorias() {
//     const container = document.getElementById("todas-categorias")
//     showLoading()
//
//     try {
//         const response = await fetch(`${API_BASE_URL}/categorias`)
//         if (response.ok) {
//             const data = await response.json()
//
//             if (data.categorias && data.categorias.length > 0) {
//                 const resultados = await Promise.all(
//                     data.categorias.map(async (categoria) => {
//                         try {
//                             const [provinciaRes, horaRes] = await Promise.all([
//                                 fetch(`${API_BASE_URL}/provinciaMax/categorias/${encodeURIComponent(categoria)}`),
//                                 fetch(`${API_BASE_URL}/horaMax/categorias/${encodeURIComponent(categoria)}`),
//                             ])
//
//                             const provinciaData = await provinciaRes.json()
//                             const horaData = await horaRes.json()
//
//                             return {
//                                 categoria,
//                                 provincia: provinciaData.provincia || provinciaData.estadisticasCategoria_provincia || "N/A",
//                                 hora: horaData.hora || horaData.estadisticasCategoria_hora || "N/A",
//                             }
//                         } catch {
//                             return { categoria, provincia: "Error", hora: "Error" }
//                         }
//                     })
//                 )
//
//                 container.innerHTML = resultados
//                     .map((cat) => `
//                         <div class="categoria-card">
//                             <h3>${cat.categoria}</h3>
//                             <div class="categoria-info">
//                                 <div class="info-row">
//                                     <span class="result-label">Provincia:</span>
//                                     <span class="result-value">${cat.provincia}</span>
//                                 </div>
//                                 <div class="info-row">
//                                     <span class="result-label">Hora pico:</span>
//                                     <span class="result-value">${cat.hora}:00 hs</span>
//                                 </div>
//                             </div>
//                         </div>
//                     `).join("")
//             } else {
//                 container.innerHTML = '<p class="placeholder-text">No se encontraron categorías disponibles</p>'
//             }
//         }
//     } catch (error) {
//         container.innerHTML = '<p class="error-message">Error al cargar las categorías</p>'
//     } finally {
//         hideLoading()
//     }
// }

// Permitir buscar con Enter
document.addEventListener("DOMContentLoaded", () => {
    cargarEstadisticasGenerales()
    //cargarTodasCategorias()

    document.getElementById("categoria-select").addEventListener("keypress", (e) => {
        if (e.key === "Enter") buscarCategoria()
    })

    document.getElementById("coleccion-select").addEventListener("keypress", (e) => {
        if (e.key === "Enter") buscarColeccion()
    })
})
