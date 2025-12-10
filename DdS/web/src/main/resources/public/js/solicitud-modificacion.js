// solicitud-modificacion.js

var mapaModificacion;
var marcadorPropuesto = null;
var coordenadasOriginales = {}; // Para referencia

function initMapaModificacion(config) {
    coordenadasOriginales = { lat: config.originalLat, lon: config.originalLon };

    // 1. Inicializar Mapa (centrado en la ubicación original)
    mapaModificacion = L.map(config.mapContainerId).setView([config.originalLat, config.originalLon], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(mapaModificacion);

    // 2. Marcar Ubicación Original (Solo para referencia visual)
    L.marker([config.originalLat, config.originalLon], {
        icon: L.divIcon({className: 'map-original-marker', html: '<span style="color: blue; font-size: 20px;">★</span>'})
    }).addTo(mapaModificacion).bindPopup("Ubicación Original");

    // 3. Listener de Clic para seleccionar nueva ubicación
    mapaModificacion.on('click', function(e) {
        const lat = e.latlng.lat.toFixed(6);
        const lon = e.latlng.lng.toFixed(6);

        // Remover marcador anterior
        if (marcadorPropuesto) {
            mapaModificacion.removeLayer(marcadorPropuesto);
        }

        // Crear nuevo marcador (icono de lápiz o diferente color)
        marcadorPropuesto = L.marker([lat, lon]).addTo(mapaModificacion);
        marcadorPropuesto.bindPopup("Ubicación Propuesta").openPopup();

        // Actualizar campos ocultos del formulario
        document.getElementById('latitud').value = lat;
        document.getElementById('longitud').value = lon;

        document.getElementById('coordenadas-seleccionadas').textContent = `Coordenadas propuestas: Lat ${lat}, Lon ${lon}`;
    });
}


document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('form-solicitud-modificacion');
    const mensajeExito = document.getElementById('mensaje-exito');
    const mensajeError = document.getElementById('mensaje-error');
    const btnEnviar = document.getElementById('btn-enviar');


    const POST_URL = '/api/solicitar-modificacion';

    form.addEventListener('submit', async (e) => {
        e.preventDefault(); // Detener el envío por defecto del formulario (evita el 404)

        mensajeExito.style.display = 'none';
        mensajeError.style.display = 'none';
        btnEnviar.disabled = true;

        const formData = new FormData(form);
        const justificacion = formData.get('justificacion');

        if (!justificacion || justificacion.trim() === '') {
            mensajeError.textContent = 'La justificación es obligatoria.';
            mensajeError.style.display = 'block';
            btnEnviar.disabled = false;
            return;
        }

        const hechoId = formData.get('hechoId');
        const usuarioId = formData.get('usuarioId');

        // 1. Recolectar solo los campos que tienen un valor NUEVO
        const cambios = {};
        let camposModificadosCount = 0;

        // Asumiendo que tus campos editables tienen el atributo 'name' (ej: name="titulo", name="descripcion")
        const camposEditables = ['titulo', 'descripcion' /*, ... otros campos ... */];

        camposEditables.forEach(campo => {
            const valor = formData.get(campo);
            if (valor && valor.trim() !== '') {
                cambios[campo] = valor.trim();
                camposModificadosCount++;
            }
        });

        if (camposModificadosCount === 0) {
            mensajeError.textContent = 'Debe proponer una modificación en al menos un campo.';
            mensajeError.style.display = 'block';
            btnEnviar.disabled = false;
            return;
        }

        // 2. Construir el cuerpo JSON
        const body = {
            "hechoId": "uuid...",
            "justificacion": "...",
            "hechoModificado": {
                "titulo": "nuevo titulo",
                "ubicacion": {
                    "latitud": valor_lat,
                    "longitud": valor_lon
                }
            }
        };

        // 3. Enviar la solicitud POST
        try {
            const response = await fetch(POST_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (response.ok && response.status === 201) {
                mensajeExito.style.display = 'block';
                // Redirigir o limpiar el formulario después de un breve retraso
                setTimeout(() => {
                    window.location.href = `/hechos/${hechoId}`;
                }, 2000);
            } else {
                const errorData = await response.json();
                mensajeError.textContent = `Error (${response.status}): ${errorData.error || response.statusText}`;
                mensajeError.style.display = 'block';
            }

        } catch (error) {
            mensajeError.textContent = 'Error de conexión con el servidor.';
            mensajeError.style.display = 'block';
            console.error('Fetch error:', error);
        } finally {
            btnEnviar.disabled = false;
        }
    });
});