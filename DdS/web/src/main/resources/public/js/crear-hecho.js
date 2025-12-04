document.addEventListener('DOMContentLoaded', function() {

    // --- 1. LÓGICA DEL MAPA (Sin cambios) ---
    const defaultLat = -34.6037;
    const defaultLng = -58.3816;
    const map = L.map('mapa-selector').setView([defaultLat, defaultLng], 13);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    const marker = L.marker([defaultLat, defaultLng], { draggable: true }).addTo(map);

    function updateInputs(lat, lng) {
        const elLat = document.getElementById('latitud');
        const elLng = document.getElementById('longitud');
        if(elLat) elLat.value = lat;
        if(elLng) elLng.value = lng;
    }
    updateInputs(defaultLat, defaultLng);

    marker.on('dragend', function(e) {
        const position = marker.getLatLng();
        updateInputs(position.lat, position.lng);
    });
    map.on('click', function(e) {
        marker.setLatLng(e.latlng);
        updateInputs(e.latlng.lat, e.latlng.lng);
    });
    if ("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition(function(position) {
            const lat = position.coords.latitude;
            const lng = position.coords.longitude;
            map.setView([lat, lng], 15);
            marker.setLatLng([lat, lng]);
            updateInputs(lat, lng);
        });
    }

    // ==========================================
    // 2. LÓGICA DEL FORMULARIO Y MULTIMEDIA
    // ==========================================
    const form = document.getElementById('form-crear-hecho');
    const selectCategoria = document.getElementById('categoria');
    const grupoOtraCategoria = document.getElementById('otra-categoria-group');
    const inputOtraCategoria = document.getElementById('otraCategoria');
    const checkboxAnonimo = document.getElementById('anonimo');
    const inputMultimedia = document.getElementById('multimedia');
    const previewContainer = document.getElementById('preview-container');
    const previewGrid = document.getElementById('preview-grid');
    const btnCancelar = document.getElementById('btn-cancelar');

    // === NUEVO: Array para mantener los archivos en memoria ===
    let archivosSeleccionados = [];

    // Toggle "Otra categoría"
    if (selectCategoria && grupoOtraCategoria) {
        selectCategoria.addEventListener('change', function() {
            if (this.value === 'Otro') {
                grupoOtraCategoria.style.display = 'block';
                inputOtraCategoria.required = true;
            } else {
                grupoOtraCategoria.style.display = 'none';
                inputOtraCategoria.required = false;
                inputOtraCategoria.value = '';
            }
        });
    }

    // === NUEVO: Lógica de selección y borrado de archivos ===
    if (inputMultimedia) {
        inputMultimedia.addEventListener('change', function(e) {
            const nuevosArchivos = Array.from(e.target.files);

            // Agregamos los nuevos archivos al array existente
            archivosSeleccionados = [...archivosSeleccionados, ...nuevosArchivos];

            // Actualizamos la vista
            actualizarVistaPrevia();

            // Importante: Limpiamos el input para permitir seleccionar el mismo archivo nuevamente si se desea
            // o para que el evento 'change' se dispare de nuevo al agregar más.
            inputMultimedia.value = '';
        });
    }

    function actualizarVistaPrevia() {
        if (!previewGrid) return;
        previewGrid.innerHTML = ''; // Limpiar HTML actual

        if (archivosSeleccionados.length > 0) {
            previewContainer.style.display = 'block';

            archivosSeleccionados.forEach((file, index) => {
                const div = document.createElement('div');
                div.className = 'preview-item';

                // Botón de eliminar
                const btnRemove = document.createElement('button');
                btnRemove.className = 'btn-remove-file';
                btnRemove.innerHTML = '×';
                btnRemove.onclick = function() { eliminarArchivo(index); };
                div.appendChild(btnRemove);

                // Contenido visual
                if (file.type.startsWith('image/')) {
                    const img = document.createElement('img');
                    img.src = URL.createObjectURL(file);
                    div.appendChild(img);
                } else if (file.type.startsWith('video/')) {
                    const video = document.createElement('video');
                    video.src = URL.createObjectURL(file);
                    video.style.width = '100%';
                    video.style.height = '80px';
                    div.appendChild(video);
                } else {
                    const span = document.createElement('div');
                    span.innerHTML = '📎 <br>' + (file.name.length > 10 ? file.name.substring(0,10)+'...' : file.name);
                    span.style.paddingTop = '20px';
                    div.appendChild(span);
                }

                previewGrid.appendChild(div);
            });
        } else {
            previewContainer.style.display = 'none';
        }
    }

    // Función global o dentro del scope para borrar
    window.eliminarArchivo = function(index) {
        // Elimina el archivo del array por índice
        archivosSeleccionados.splice(index, 1);
        actualizarVistaPrevia();
    };


    // Botón Cancelar
    if (btnCancelar) {
        btnCancelar.addEventListener('click', function() {
            if (confirm('¿Cancelar reporte? Se perderán los datos.')) {
                window.location.href = '/';
            }
        });
    }

    // === SUBMIT DEL FORMULARIO ===
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();

            const btnEnviar = document.getElementById('btn-enviar');
            const mensajeExito = document.getElementById('mensaje-exito');
            const mensajeError = document.getElementById('mensaje-error');

            if (mensajeExito) mensajeExito.style.display = 'none';
            if (mensajeError) mensajeError.style.display = 'none';
            if (btnEnviar) {
                btnEnviar.disabled = true;
                btnEnviar.textContent = 'Enviando...';
            }

            try {
                const urlsMultimedia = [];

                // === CAMBIO: Usamos 'archivosSeleccionados' en lugar del input directo ===
                if (archivosSeleccionados.length > 0) {

                    const uploadPromises = archivosSeleccionados.map(file => {
                        const formData = new FormData();
                        formData.append('file', file);
                        formData.append('upload_preset', CLOUDINARY_PRESET);

                        return fetch(CLOUDINARY_URL, {
                            method: 'POST',
                            body: formData
                        })
                            .then(response => response.json())
                            .then(data => {
                                if (data.secure_url) {
                                    return {
                                        url: data.secure_url,
                                        tipo: obtenerTipoEnum(data)
                                    };
                                } else {
                                    throw new Error('Error subiendo a Cloudinary: ' + (data.error ? data.error.message : 'Desconocido'));
                                }
                            });
                    });

                    const resultados = await Promise.all(uploadPromises);
                    urlsMultimedia.push(...resultados);
                }

                const categoriaVal = selectCategoria.value;
                const otraCatVal = inputOtraCategoria ? inputOtraCategoria.value.trim() : '';

                let contribuyenteData = null;
                if (CURRENT_USER && CURRENT_USER !== "") {
                    const esAnonimo = checkboxAnonimo && checkboxAnonimo.checked;
                    if (!esAnonimo) {
                        contribuyenteData = { username: CURRENT_USER };
                    }
                }

                const hechoData = {
                    titulo: document.getElementById('titulo').value,
                    descripcion: document.getElementById('descripcion').value,
                    categoria: (categoriaVal === 'Otro' && otraCatVal) ? otraCatVal : categoriaVal,
                    ubicacion: {
                        latitud: parseFloat(document.getElementById('latitud').value),
                        longitud: parseFloat(document.getElementById('longitud').value),
                        descripcion: null
                    },
                    fechaDeAcontecimiento: new Date(document.getElementById('fechaAcontecimiento').value).toISOString(),
                    contenidoMultimedia: urlsMultimedia.map(item => ({
                        contenido: item.url,
                        tipoContenido: item.tipo
                    })),
                    contribuyente: contribuyenteData,
                    etiquetas: document.getElementById('etiquetas').value ?
                        document.getElementById('etiquetas').value.split(',').map(tag => ({ nombre: tag.trim() })) : [],
                };

                const response = await fetch(URL_PUBLICA + '/hechos', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(hechoData)
                });

                if (response.ok) {
                    if (mensajeExito) mensajeExito.style.display = 'block';
                    // setTimeout(() => { window.location.href = '/'; }, 2000);
                } else {
                    const text = await response.text();
                    throw new Error(`Error ${response.status}: ${text}`);
                }

            } catch (error) {
                console.error(error);
                if (mensajeError) {
                    mensajeError.textContent = `❌ Error: ${error.message}`;
                    mensajeError.style.display = 'block';
                }
            } finally {
                if (btnEnviar) {
                    btnEnviar.disabled = false;
                    btnEnviar.textContent = 'Enviar Reporte';
                }
            }
        });
    }
});

// Función auxiliar (asegúrate de que esté disponible)
function obtenerTipoEnum(data) {
    if (data.resource_type === 'image') return 'IMAGEN';
    if (data.resource_type === 'video') {
        const esAudio = data.is_audio === true || ['mp3', 'wav', 'ogg', 'aac', 'm4a', 'flac'].includes(data.format);
        return esAudio ? 'AUDIO' : 'VIDEO';
    }
    return 'IMAGEN';
}