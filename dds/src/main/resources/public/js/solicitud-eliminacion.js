document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('form-solicitud-eliminacion');
    const btnCancelar = document.getElementById('btn-cancelar');
    const checkboxAnonimo = document.getElementById('anonimo');
    const inputNombre = document.getElementById('nombreUsuario');
    const textareaJustificacion = document.getElementById('justificacion');
    const contadorCaracteres = document.getElementById('contador-caracteres');

    // Contador de caracteres para la justificación
    textareaJustificacion.addEventListener('input', function() {
        const caracteres = this.value.length;
        contadorCaracteres.textContent = `${caracteres} caracteres`;

        if (caracteres < 500) {
            contadorCaracteres.style.color = '#dc3545';
        } else {
            contadorCaracteres.style.color = 'var(--muted-color)';
        }
    });

    // Manejar checkbox anónimo
    checkboxAnonimo.addEventListener('change', function() {
        inputNombre.disabled = this.checked;
        if (this.checked) {
            inputNombre.value = '';
        }
    });

    // Cancelar
    btnCancelar.addEventListener('click', function() {
        if (confirm('¿Está seguro de que desea cancelar? Se perderán los datos ingresados.')) {
            window.history.back();
        }
    });

    // Validación del formulario
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const justificacion = textareaJustificacion.value.trim();

        // Validar longitud mínima
        if (justificacion.length < 500) {
            alert('La justificación debe tener al menos 500 caracteres. Actualmente tiene ' + justificacion.length + ' caracteres.');
            textareaJustificacion.focus();
            return;
        }

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
            // Construir objeto JSON como espera el backend
            const solicitudData = {
                ID_hechoAsociado: document.getElementById('ID_hechoAsociado').value,
                justificacion: justificacion,
                usuario: checkboxAnonimo.checked ? null : {
                    nombre: inputNombre.value.trim() || ""
                }
            };

            console.log('Enviando solicitud de eliminación:', solicitudData);

            const response = await fetch('/api/solicitudEliminacion', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(solicitudData)
            });

            console.log('Respuesta del servidor:', response.status, response.statusText);

            if (response.ok) {
                const result = await response.text();
                console.log('Éxito:', result);
                mensajeExito.style.display = 'block';
                setTimeout(() => {
                    // Redirigir a la página del hecho o al inicio
                    const hechoId = document.getElementById('ID_hechoAsociado').value;
                    window.location.href = '/hecho/' + encodeURIComponent(hechoId);
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
            mensajeError.textContent = `❌ ${error.message}`;
            mensajeError.style.display = 'block';
        } finally {
            btnEnviar.disabled = false;
            btnEnviar.textContent = 'Enviar Solicitud';
        }
    });
});