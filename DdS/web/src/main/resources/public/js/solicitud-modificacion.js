var mapaModificacion;
var marcadorPropuesto = null;
var coordenadasOriginales = {};

function initMapaModificacion(config) {
    coordenadasOriginales = { lat: config.originalLat, lon: config.originalLon };

    mapaModificacion = L.map(config.mapContainerId).setView([config.originalLat, config.originalLon], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(mapaModificacion);

    L.marker([config.originalLat, config.originalLon], {
        icon: L.divIcon({className: 'map-original-marker', html: '<span style="color: blue; font-size: 20px;">★</span>'})
    }).addTo(mapaModificacion).bindPopup("Ubicación Original");

    mapaModificacion.on('click', function(e) {
        const lat = e.latlng.lat.toFixed(6);
        const lon = e.latlng.lng.toFixed(6);

        if (marcadorPropuesto) {
            mapaModificacion.removeLayer(marcadorPropuesto);
        }

        marcadorPropuesto = L.marker([lat, lon]).addTo(mapaModificacion);
        marcadorPropuesto.bindPopup("Ubicación Propuesta").openPopup();

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

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        mensajeExito.style.display = 'none';
        mensajeError.style.display = 'none';
        btnEnviar.disabled = true;

        const formData = new FormData(form);


        const hechoModificado = {};
        let hayCambios = false;

        const camposSimples = ['titulo', 'descripcion', 'categoria', 'fechaDeAcontecimiento'];

        camposSimples.forEach(campo => {
            const valor = formData.get(campo);
            if (valor && valor.trim() !== '') {
                hechoModificado[campo] = valor.trim();
                hayCambios = true;
            }
        });

        const lat = formData.get('ubicacion.latitud');
        const lon = formData.get('ubicacion.longitud');

        if (lat && lon && lat.trim() !== '' && lon.trim() !== '') {
            hechoModificado['ubicacion'] = {
                'latitud': parseFloat(lat),
                'longitud': parseFloat(lon)
            };
            hayCambios = true;
        }

        if (!hayCambios) {
            mensajeError.textContent = 'Debe proponer una modificación en al menos un campo (o cambiar la ubicación).';
            mensajeError.style.display = 'block';
            btnEnviar.disabled = false;
            return;
        }

        const body = {
            "username": formData.get('username'),
            "hechoModificado": hechoModificado,
            "accessToken": ACCESS_TOKEN
        };

        console.log("Enviando JSON:", JSON.stringify(body));
        const hechoId = formData.get('hechoId');
        // --- 3. ENVÍO ---
        try {
            const response = await fetch(URL_PUBLICA + "/hecho/"+hechoId, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (response.ok) {
                mensajeExito.style.display = 'block';
                const hechoId = formData.get('hechoId');
                setTimeout(() => {
                    window.location.href = `/hechos/${hechoId}`;
                }, 2000);
            } else {
                const errorData = await response.json();
                mensajeError.textContent = `Error: ${errorData.error || 'No se pudo procesar la solicitud'}`;
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