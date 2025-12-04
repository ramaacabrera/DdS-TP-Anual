// Utilidades
function showLoading() {
    const overlay = document.getElementById("loading-overlay");
    if (overlay) overlay.classList.add("active");
}

function hideLoading() {
    const overlay = document.getElementById("loading-overlay");
    if (overlay) overlay.classList.remove("active");
}

function showError(containerId, message) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `<p class="error-message" style="color: #d32f2f;">Error: ${message}</p>`;
    }
}

async function cargarEstadisticasGenerales() {
    const API_BASE_URL = '/api/estadisticas';
    try {
        const spamResponse = await fetch(`${API_BASE_URL}/solicitudesSpam`);
        if (spamResponse.ok) {
            const spamData = await spamResponse.json();
            const spamElement = document.getElementById("spam-count");
            if (spamElement) {
                spamElement.textContent = spamData.spam || spamData.estadisticas_spam || 0;
            }
        }

        const categoriaResponse = await fetch(`${API_BASE_URL}/categoriaMax`);
        if (categoriaResponse.ok) {
            const categoriaData = await categoriaResponse.json();
            const catElement = document.getElementById("categoria-max");
            if (catElement) {
                catElement.textContent = categoriaData.categoria || categoriaData.estadisticas_categoria_max_hechos || "N/A";
            }
        }
    } catch (error) {
        console.error("Error cargando estadísticas generales:", error);
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

    resultsContainer.innerHTML = '<div class="spinner" style="border-width: 2px; width: 30px; height: 30px; margin: 10px auto; display:block;"></div>';

    try {
        const encodedCat = encodeURIComponent(categoria);

        const [resProvincia, resHora] = await Promise.all([
            fetch(`/api/estadisticas/provinciaMax/categorias/${encodedCat}`),
            fetch(`/api/estadisticas/horaMax/categorias/${encodedCat}`)
        ]);

        if (resProvincia.status === 404 && resHora.status === 404) {
            mostrarMensajeSinResultados(resultsContainer, categoria);
            return;
        }

        const dataProvincia = resProvincia.ok ? await resProvincia.json() : {};
        const dataHora = resHora.ok ? await resHora.json() : {};

        const provinciaVal = dataProvincia.provincia || null;
        const horaVal = (dataHora.hora !== undefined && dataHora.hora !== null) ? dataHora.hora : null;

        if (!provinciaVal && horaVal === null) {
            mostrarMensajeSinResultados(resultsContainer, categoria);
            return;
        }

        resultsContainer.innerHTML = `
            <div class="result-success-container">
                <div class="result-header">
                    <h3>Resultados para: <strong>${categoria}</strong></h3>
                </div>
                <div class="result-grid">
                    <div class="result-stat">
                        <span class="result-label">Provincia más activa</span>
                        <span class="result-value" style="color: var(--brand-dark);">${provinciaVal || "N/A"}</span>
                    </div>
                    <div class="result-stat">
                        <span class="result-label">Hora pico</span>
                        <span class="result-value" style="color: var(--brand-primary);">${horaVal !== null ? horaVal + ":00 hs" : "N/A"}</span>
                    </div>
                </div>
            </div>
        `;

    } catch (error) {
        console.error("Error en búsqueda:", error);
        resultsContainer.innerHTML = `
            <div style="color: #e57373; text-align: center; margin-top: 10px;">
                <p>Ocurrió un error de conexión. Intente nuevamente.</p>
            </div>
        `;
    }
}

function mostrarMensajeSinResultados(container, categoria) {
    container.innerHTML = `
        <div style="text-align: center; padding: 1rem; color: #d32f2f; background-color: #ffebee; border-radius: 8px; margin-top: 10px;">
            <p style="margin: 0;">No se han encontrado resultados para la categoria <strong>${categoria}</strong></p>
        </div>
    `;
}

const catInput = document.getElementById('categoria-select');
if (catInput) {
    catInput.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            buscarCategoria();
        }
    });
}

async function buscarColeccion() {
    const input = document.getElementById("coleccion-select");
    const coleccion = input.value.trim();

    if (!coleccion) {
        showError("coleccion-results", "Por favor ingresa un ID de colección");
        return;
    }

    showLoading();
    const API_BASE_URL = '/api/estadisticas';
    const resultsContainer = document.getElementById("coleccion-results");

    try {
        const response = await fetch(`${API_BASE_URL}/provinciaMax/colecciones/${encodeURIComponent(coleccion)}`);

        if (response.ok) {
            const data = await response.json();
            if(data.nombre || data.provincia) {
                resultsContainer.innerHTML = `
                    <div class="result-success-container" style="margin-top: 10px;">
                        <div class="result-grid">
                            <div class="result-stat">
                                <span class="result-label">Colección</span>
                                <span class="result-value" style="font-size: 1rem;">${data.nombre || "Desconocida"}</span>
                            </div>
                            <div class="result-stat">
                                <span class="result-label">Provincia frecuente</span>
                                <span class="result-value" style="color: var(--brand-dark);">${data.provincia || "N/A"}</span>
                            </div>
                        </div>
                    </div>
                `;
            } else {
                throw new Error("Empty data");
            }
        } else {
            throw new Error("Not found");
        }
    } catch (error) {
        resultsContainer.innerHTML = `
            <div style="text-align: center; padding: 1rem; color: #d32f2f; background-color: #ffebee; border-radius: 8px; margin-top: 10px;">
                <p style="margin: 0;">No se encontraron datos para la colección "<strong>${coleccion}</strong>"</p>
            </div>
        `;
    } finally {
        hideLoading();
    }
}

document.addEventListener("DOMContentLoaded", () => {
    cargarEstadisticasGenerales();

    const colInput = document.getElementById("coleccion-select");
    if (colInput) {
        colInput.addEventListener("keypress", (e) => {
            if (e.key === "Enter") buscarColeccion();
        });
    }
});