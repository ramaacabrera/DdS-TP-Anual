document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('form-crear-hecho');
    const btnCancelar = document.getElementById('btn-cancelar');
    const checkboxAnonimo = document.getElementById('anonimo');
    const inputNombre = document.getElementById('nombreContribuyente');
    const inputMultimedia = document.getElementById('multimedia');
    const previewContainer = document.getElementById('preview-container');
    const previewGrid = document.getElementById('preview-grid');

    // Manejar checkbox anónimo
    if (checkboxAnonimo && inputNombre) {
        checkboxAnonimo.addEventListener('change', function() {
            inputNombre.disabled = this.checked;
            if (this.checked) {
                inputNombre.value = '';
            }
        });
    }

    // Vista previa de archivos multimedia (si existe el elemento)
    if (inputMultimedia) {
        inputMultimedia.addEventListener('change', function(e) {
            if (previewGrid) {
                previewGrid.innerHTML = '';

                if (this.files.length > 0 && previewContainer) {
                    previewContainer.style.display = 'block';

                    Array.from(this.files).forEach(file => {
                        const previewItem = document.createElement('div');
                        previewItem.className = 'preview-item';

                        if (file.type.startsWith('image/')) {
                            const img = document.createElement('img');
                            img.src = URL.createObjectURL(file);
                            previewItem.appendChild(img);
                        } else if (file.type.startsWith('video/')) {
                            previewItem.innerHTML = '🎥 ' + file.name;
                        } else if (file.type.startsWith('audio/')) {
                            previewItem.innerHTML = '🎵 ' + file.name;
                        } else {
                            previewItem.innerHTML = '📎 ' + file.name;
                        }

                        previewGrid.appendChild(previewItem);
                    });
                } else if (previewContainer) {
                    previewContainer.style.display = 'none';
                }
            }
        });
    }

    // Cancelar
    if (btnCancelar) {
        btnCancelar.addEventListener('click', function() {
            if (confirm('¿Está seguro de que desea cancelar? Se perderán los datos ingresados.')) {
                window.location.href = '/';
            }
        });
    }

    // Envío del formulario
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();

            const btnEnviar = document.getElementById('btn-enviar');
            const mensajeExito = document.getElementById('mensaje-exito');
            const mensajeError = document.getElementById('mensaje-error');

            // Ocultar mensajes anteriores
            if (mensajeExito) mensajeExito.style.display = 'none';
            if (mensajeError) mensajeError.style.display = 'none';

            // Deshabilitar botón
            if (btnEnviar) {
                btnEnviar.disabled = true;
                btnEnviar.textContent = 'Enviando...';
            }

            try {
                // Construir objeto JSON como espera el backend
                const hechoData = {
                    titulo: document.getElementById('titulo').value,
                    descripcion: document.getElementById('descripcion').value,
                    categoria: document.getElementById('categoria').value,
                    ubicacion: {
                        latitud: parseFloat(document.getElementById('latitud').value),
                        longitud: parseFloat(document.getElementById('longitud').value),
                        descripcion: document.getElementById('descripcionUbicacion').value || ""
                    },
                    fechaDeAcontecimiento: new Date(document.getElementById('fechaAcontecimiento').value).toISOString(),
                    contribuyente: document.getElementById('anonimo').checked ? null : {
                        nombre: document.getElementById('nombreContribuyente').value || ""
                    },
                    etiquetas: document.getElementById('etiquetas').value ?
                        document.getElementById('etiquetas').value.split(',').map(etiqueta => ({
                            nombre: etiqueta.trim()
                        })) : [],
                    contenidoMultimedia: [] // Por ahora vacío
                };

                console.log('Enviando datos:', hechoData);
                console.log('JSON stringified:', JSON.stringify(hechoData));

                const response = await fetch('/api/hechos', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(hechoData)
                });

                console.log('Respuesta del servidor:', response.status, response.statusText);

                if (response.ok) {
                    const result = await response.text();
                    console.log('Éxito:', result);
                    if (mensajeExito) {
                        mensajeExito.style.display = 'block';
                    }
                    setTimeout(() => {
                        window.location.href = '/';
                    }, 2000);
                } else {
                    let errorMessage = `Error ${response.status}: ${response.statusText}`;
                    try {
                        const errorBody = await response.text();
                        console.error('Cuerpo del error:', errorBody);
                        errorMessage += ` - ${errorBody}`;
                    } catch (e) {
                        console.error('No se pudo leer el cuerpo del error');
                    }
                    throw new Error(errorMessage);
                }

            } catch (error) {
                console.error('Error completo:', error);

                // Mensaje más específico
                let mensajeErrorTexto = 'Error al enviar el reporte. ';
                if (error.message.includes('500')) {
                    mensajeErrorTexto += 'Error interno del servidor. Por favor, contacte al administrador.';
                } else if (error.message.includes('Network Error')) {
                    mensajeErrorTexto += 'Error de conexión. Verifique su internet.';
                } else {
                    mensajeErrorTexto += error.message;
                }

                if (mensajeError) {
                    mensajeError.textContent = `❌ ${mensajeErrorTexto}`;
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