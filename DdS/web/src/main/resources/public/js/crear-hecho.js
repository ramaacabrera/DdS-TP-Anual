document.addEventListener('DOMContentLoaded', function() {
    const defaultLat = -34.6037;
    const defaultLng = -58.3816;

    const map = L.map('mapa-selector').setView([defaultLat, defaultLng], 13);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    const marker = L.marker([defaultLat, defaultLng], { draggable: true }).addTo(map);

    function updateInputs(lat, lng) {
        // Verifica que los elementos existan antes de asignar
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
    // 2. LÓGICA DEL FORMULARIO
    // ==========================================
    const form = document.getElementById('form-crear-hecho');
    const selectCategoria = document.getElementById('categoria');
    const grupoOtraCategoria = document.getElementById('otra-categoria-group');
    const inputOtraCategoria = document.getElementById('otraCategoria');
    // El checkbox puede no existir si el usuario no está logueado
    const checkboxAnonimo = document.getElementById('anonimo');
    const inputMultimedia = document.getElementById('multimedia');
    const previewContainer = document.getElementById('preview-container');
    const previewGrid = document.getElementById('preview-grid');
    const btnCancelar = document.getElementById('btn-cancelar');

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

    // Preview Multimedia
    if (inputMultimedia) {
        inputMultimedia.addEventListener('change', function() {
            if (previewGrid) {
                previewGrid.innerHTML = '';
                if (this.files.length > 0) {
                    previewContainer.style.display = 'block';
                    Array.from(this.files).forEach(file => {
                        const div = document.createElement('div');
                        div.className = 'preview-item';
                        if (file.type.startsWith('image/')) {
                            const img = document.createElement('img');
                            img.src = URL.createObjectURL(file);
                            div.appendChild(img);
                        } else {
                            div.innerHTML = '📎 ' + file.name;
                        }
                        previewGrid.appendChild(div);
                    });
                } else {
                    previewContainer.style.display = 'none';
                }
            }
        });
    }

    // Botón Cancelar
    if (btnCancelar) {
        btnCancelar.addEventListener('click', function() {
            if (confirm('¿Cancelar reporte? Se perderán los datos.')) {
                window.location.href = '/';
            }
        });
    }

    // SUBMIT DEL FORMULARIO
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
                const inputMultimedia = document.getElementById('multimedia');
                const urlsMultimedia = []; // Aquí guardaremos los resultados

                if (inputMultimedia && inputMultimedia.files.length > 0) {

                    // Creamos una promesa por cada archivo para subirlos en paralelo
                    const uploadPromises = Array.from(inputMultimedia.files).map(file => {
                        const formData = new FormData();
                        formData.append('file', file);
                        formData.append('upload_preset', CLOUDINARY_PRESET);

                        // Usamos 'auto' en la URL para que detecte si es video o imagen
                        return fetch(CLOUDINARY_URL, {
                            method: 'POST',
                            body: formData
                        })
                            .then(response => response.json())
                            .then(data => {
                                if (data.secure_url) {
                                    return {
                                        url: data.secure_url,
                                        tipo: data.resource_type // 'image' o 'video'
                                    };
                                } else {
                                    throw new Error('Error subiendo a Cloudinary');
                                }
                            });
                    });

                    // Esperamos a que TODOS se suban
                    const resultados = await Promise.all(uploadPromises);
                    urlsMultimedia.push(...resultados);
                }
                const categoriaVal = selectCategoria.value;
                const otraCatVal = inputOtraCategoria ? inputOtraCategoria.value.trim() : '';

                // LÓGICA DE CONTRIBUYENTE
                let contribuyenteData = null;

                // 1. Si hay un usuario logueado (CURRENT_USER viene del FTL)
                if (CURRENT_USER && CURRENT_USER !== "") {
                    // 2. Verificamos si marcó "Anónimo" (si el checkbox existe y está marcado)
                    const esAnonimo = checkboxAnonimo && checkboxAnonimo.checked;

                    if (!esAnonimo) {
                        // Enviamos el objeto con el nombre de usuario
                        contribuyenteData = { username: CURRENT_USER };
                    }
                    // Si esAnonimo es true, contribuyenteData se queda como null
                }
                // Si no hay CURRENT_USER (usuario no logueado), contribuyenteData queda null

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
                        tipoContenido: obtenerTipoEnum(item.tipo) // Guardamos si es video o imagen
                    })),

                    // Asignamos el valor calculado arriba
                    contribuyente: contribuyenteData,

                    etiquetas: document.getElementById('etiquetas').value ?
                        document.getElementById('etiquetas').value.split(',').map(tag => ({ nombre: tag.trim() })) : [],
                };

                // Debug para ver qué se envía (puedes quitarlo luego)
                console.log("Enviando JSON:", hechoData);

                const response = await fetch(URL_PUBLICA + '/hechos', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(hechoData)
                });

                if (response.ok) {
                    if (mensajeExito) mensajeExito.style.display = 'block';
                    setTimeout(() => { window.location.href = '/'; }, 2000);
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

// Lógica para convertir respuesta de Cloudinary a tu Enum Java
function obtenerTipoEnum(data) {
    // 1. Caso IMAGEN
    if (data.resource_type === 'image') {
        return 'IMAGEN';
    }

    // 2. Caso VIDEO o AUDIO (Cloudinary agrupa ambos como 'video')
    if (data.resource_type === 'video') {
        // Verificamos si es audio mirando la propiedad is_audio o la extensión
        const esAudio = data.is_audio === true ||
            ['mp3', 'wav', 'ogg', 'aac', 'm4a', 'flac'].includes(data.format);

        if (esAudio) {
            return 'AUDIO';
        } else {
            return 'VIDEO';
        }
    }

    // Default por si llega 'raw' (archivos zip, doc, etc), lo tratamos como IMAGEN o null según tu lógica
    return 'IMAGEN';
}