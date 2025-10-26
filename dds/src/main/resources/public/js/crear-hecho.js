document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('form-crear-hecho');
    const btnCancelar = document.getElementById('btn-cancelar');
    const checkboxAnonimo = document.getElementById('anonimo');
    const inputNombre = document.getElementById('nombreContribuyente');
    const inputMultimedia = document.getElementById('multimedia');
    const previewContainer = document.getElementById('preview-container');
    const previewGrid = document.getElementById('preview-grid');

    // Manejar checkbox anónimo
    checkboxAnonimo.addEventListener('change', function() {
        inputNombre.disabled = this.checked;
        if (this.checked) {
            inputNombre.value = '';
        }
    });

    // Vista previa de archivos multimedia
    inputMultimedia.addEventListener('change', function(e) {
        previewGrid.innerHTML = '';

        if (this.files.length > 0) {
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
        } else {
            previewContainer.style.display = 'none';
        }
    });

    // Cancelar
    btnCancelar.addEventListener('click', function() {
        if (confirm('¿Está seguro de que desea cancelar? Se perderán los datos ingresados.')) {
            window.location.href = '/';
        }
    });

    // Envío del formulario
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const btnEnviar = document.getElementById('btn-enviar');
        const mensajeExito = document.getElementById('mensaje-exito');
        const mensajeError = document.getElementById('mensaje-error');

        // Ocultar mensajes anteriores
        mensajeExito.style.display = 'none';
        mensajeError.style.display = 'none';

        // Deshabilitar botón
        btnEnviar.disabled = true;
        btnEnviar.textContent = 'Enviando...';

        try {
            const formData = new FormData(this);

            // Si es anónimo, asegurarse de que no se envíe nombre
            if (checkboxAnonimo.checked) {
                formData.delete('contribuyente.nombre');
            }

            const response = await fetch('/api/hechos', {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                mensajeExito.style.display = 'block';
                setTimeout(() => {
                    window.location.href = '/';
                }, 2000);
            } else {
                throw new Error('Error en la respuesta del servidor');
            }

        } catch (error) {
            console.error('Error:', error);
            mensajeError.style.display = 'block';
        } finally {
            btnEnviar.disabled = false;
            btnEnviar.textContent = 'Enviar Reporte';
        }
    });
});