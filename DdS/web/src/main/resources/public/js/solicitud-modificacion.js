var mapaModificacion;
var marcadorPropuesto = null;
var coordenadasOriginales = {};

let archivosSeleccionados = [];
let listaExistentes = [];

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

function obtenerTipoEnum(data) {
    if (data.resource_type === 'image') return 'IMAGEN';
    if (data.resource_type === 'video') {
        const esAudio = data.is_audio === true || ['mp3', 'wav', 'ogg', 'aac', 'm4a', 'flac'].includes(data.format);
        return esAudio ? 'AUDIO' : 'VIDEO';
    }
    return 'IMAGEN';
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('form-solicitud-modificacion');
    const mensajeExito = document.getElementById('mensaje-exito');
    const mensajeError = document.getElementById('mensaje-error');
    const btnEnviar = document.getElementById('btn-enviar');
    const inputMultimedia = document.getElementById('multimedia');

    if (typeof MULTIMEDIA_EXISTENTE !== 'undefined') {
        listaExistentes = [...MULTIMEDIA_EXISTENTE];
        // Renderizar inmediatamente lo que hay
        actualizarVistaUnificada();
    }

    if (inputMultimedia) {
        inputMultimedia.addEventListener('change', function(e) {
            const nuevosArchivos = Array.from(e.target.files);
            archivosSeleccionados = [...archivosSeleccionados, ...nuevosArchivos];

            // Actualizar la vista unificada
            actualizarVistaUnificada();

            inputMultimedia.value = '';
        });
    }

    // --- FUNCIÓN DE RENDERIZADO UNIFICADA ---
    function actualizarVistaUnificada() {
        const grid = document.getElementById('preview-grid');
        const container = document.getElementById('preview-container');
        if (!grid) return;

        grid.innerHTML = '';

        const totalItems = listaExistentes.length + archivosSeleccionados.length;

        if (totalItems > 0) {
            container.style.display = 'block';

            // 1. Renderizar EXISTENTES
            listaExistentes.forEach((item, index) => {
                const div = crearPreviewItem(item.contenido, item.tipoContenido);

                const btnRemove = document.createElement('button');
                btnRemove.className = 'btn-remove-file';
                btnRemove.innerHTML = '×';
                btnRemove.onclick = function(e) { e.preventDefault(); eliminarExistente(index); };

                div.appendChild(btnRemove);
                grid.appendChild(div);
            });

            // 2. Renderizar NUEVOS
            archivosSeleccionados.forEach((file, index) => {
                const url = URL.createObjectURL(file);
                let tipo = 'IMAGEN';
                if (file.type.startsWith('video/')) tipo = 'VIDEO';
                if (file.type.startsWith('audio/')) tipo = 'AUDIO';

                const div = crearPreviewItem(url, tipo);

                const badge = document.createElement('div');
                badge.className = 'badge-nuevo';
                badge.innerText = 'NUEVO';
                div.appendChild(badge);

                const btnRemove = document.createElement('button');
                btnRemove.className = 'btn-remove-file';
                btnRemove.innerHTML = '×';
                btnRemove.onclick = function(e) { e.preventDefault(); eliminarNuevo(index); };

                div.appendChild(btnRemove);
                grid.appendChild(div);
            });

        } else {
            grid.innerHTML = '<p style="color:#666; width:100%; text-align:center;">No hay contenido multimedia.</p>';
        }
    }

    function crearPreviewItem(url, tipo) {
        const div = document.createElement('div');
        div.className = 'preview-item';

        if (tipo === 'IMAGEN') {
            const img = document.createElement('img');
            img.src = url;
            div.appendChild(img);
        } else if (tipo === 'VIDEO') {
            const video = document.createElement('video');
            video.src = url;
            div.appendChild(video);
        } else {
            const span = document.createElement('div');
            span.innerHTML = '📎';
            span.style.fontSize = "24px";
            div.appendChild(span);
        }
        return div;
    }

    // --- FUNCIONES DE ELIMINACIÓN ---
    window.eliminarExistente = function(index) {
        if(confirm("¿Eliminar este archivo original del hecho?")) {
            listaExistentes.splice(index, 1);
            actualizarVistaUnificada();
        }
    };

    window.eliminarNuevo = function(index) {
        archivosSeleccionados.splice(index, 1);
        actualizarVistaUnificada();
    };

    // --- ENVÍO DEL FORMULARIO ---
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        mensajeExito.style.display = 'none';
        mensajeError.style.display = 'none';
        btnEnviar.disabled = true;
        btnEnviar.textContent = 'Procesando...';

        try {
            const formData = new FormData(form);
            const hechoModificado = {};
            let hayCambios = false;

            // 1. Campos simples
            const camposSimples = ['titulo', 'descripcion', 'categoria', 'fechaDeAcontecimiento'];
            camposSimples.forEach(campo => {
                const valor = formData.get(campo);
                if (valor && valor.trim() !== '') {
                    hechoModificado[campo] = valor.trim();
                    hayCambios = true;
                }
            });

            // 2. Ubicación
            const lat = formData.get('ubicacion.latitud');
            const lon = formData.get('ubicacion.longitud');
            if (lat && lon && lat.trim() !== '' && lon.trim() !== '') {
                hechoModificado['ubicacion'] = {
                    'latitud': parseFloat(lat),
                    'longitud': parseFloat(lon)
                };
                hayCambios = true;
            }

            // 3. MULTIMEDIA
            const cantidadOriginal = (typeof MULTIMEDIA_EXISTENTE !== 'undefined') ? MULTIMEDIA_EXISTENTE.length : 0;
            const huboBorrado = listaExistentes.length !== cantidadOriginal;
            const huboAgregado = archivosSeleccionados.length > 0;

            if (huboBorrado || huboAgregado) {
                btnEnviar.textContent = 'Subiendo archivos...';

                let multimediaFinal = [...listaExistentes];

                if (archivosSeleccionados.length > 0) {
                    const uploadPromises = archivosSeleccionados.map(file => {
                        const fd = new FormData();
                        fd.append('file', file);
                        fd.append('upload_preset', CLOUDINARY_PRESET);

                        return fetch(CLOUDINARY_URL, {
                            method: 'POST',
                            body: fd
                        })
                            .then(res => res.json())
                            .then(data => {
                                if (data.secure_url) {
                                    return {
                                        contenido: data.secure_url,
                                        tipoContenido: obtenerTipoEnum(data)
                                    };
                                } else {
                                    throw new Error('Error Cloudinary: ' + (data.error?.message || 'Desconocido'));
                                }
                            });
                    });

                    const resultadosNuevos = await Promise.all(uploadPromises);
                    // Fusionamos: Viejos + Nuevos
                    multimediaFinal = [...multimediaFinal, ...resultadosNuevos];
                }

                hechoModificado['contenidoMultimedia'] = multimediaFinal;
                hayCambios = true;
            }

            if (!hayCambios) {
                throw new Error('Debe proponer una modificación en al menos un campo o cambiar la multimedia.');
            }

            const body = {
                "username": formData.get('username'),
                "hechoModificado": hechoModificado,
                "accessToken": ACCESS_TOKEN
            };

            console.log("Enviando JSON:", JSON.stringify(body));
            const hechoId = formData.get('hechoId');

            const response = await fetch(URL_PUBLICA + "/hecho/"+hechoId, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });

            if (response.ok) {
                mensajeExito.style.display = 'block';
                setTimeout(() => {
                    window.location.href = `/hechos/${hechoId}`;
                }, 2000);
            } else {
                const errorData = await response.json();
                throw new Error(errorData.error || 'No se pudo procesar la solicitud');
            }

        } catch (error) {
            mensajeError.textContent = error.message || 'Error desconocido';
            mensajeError.style.display = 'block';
            console.error('Error:', error);
        } finally {
            btnEnviar.disabled = false;
            btnEnviar.textContent = 'Confirmar Modificación';
        }
    });
});