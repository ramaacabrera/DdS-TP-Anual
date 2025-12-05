document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('form-solicitud-eliminacion');
    const btnCancelar = document.getElementById('btn-cancelar');
    const textareaJustificacion = document.getElementById('justificacion');
    const contadorCaracteres = document.getElementById('contador-caracteres');

    // El checkbox puede no existir si el usuario no está logueado
    const checkboxAnonimo = document.getElementById('anonimo');

    // Contador de caracteres para la justificación
    if(textareaJustificacion && contadorCaracteres) {
        textareaJustificacion.addEventListener('input', function() {
            const caracteres = this.value.length;
            contadorCaracteres.textContent = `${caracteres} / 500 caracteres`;

            if (caracteres > 500) {
                contadorCaracteres.style.color = '#dc3545';
            } else {
                contadorCaracteres.style.color = 'var(--muted-color)';
            }
        });
    }

    // Cancelar
    if(btnCancelar) {
        btnCancelar.addEventListener('click', function() {
            if (confirm('¿Está seguro de que desea cancelar? Se perderán los datos ingresados.')) {
                window.history.back();
            }
        });
    }

    // Validación y Envío del formulario
    if(form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();

            const justificacion = textareaJustificacion.value.trim();

            if (justificacion.length > 500) {
                alert('La justificación no puede tener más de 500 caracteres.');
                textareaJustificacion.focus();
                return;
            }

            if (justificacion.length === 0) {
                alert('La justificación es obligatoria.');
                textareaJustificacion.focus();
                return;
            }

            const btnEnviar = document.getElementById('btn-enviar');
            const mensajeExito = document.getElementById('mensaje-exito');
            const mensajeError = document.getElementById('mensaje-error');

            mensajeExito.style.display = 'none';
            mensajeError.style.display = 'none';
            btnEnviar.disabled = true;
            btnEnviar.textContent = 'Enviando...';

            try {
                // --- LÓGICA DE USUARIO / ANÓNIMO ---
                let usuarioData = null;

                // 1. Si hay un usuario logueado (CURRENT_USER viene del FTL)
                if (CURRENT_USER && CURRENT_USER !== "") {
                    // 2. Verificamos si marcó "Anónimo" (si el checkbox existe y está marcado)
                    const esAnonimo = checkboxAnonimo && checkboxAnonimo.checked;

                    if (!esAnonimo) {
                        // Enviamos el objeto con el nombre de usuario
                        usuarioData = { username: CURRENT_USER };
                    }
                    // Si esAnonimo es true, usuarioData se queda como null
                }
                // Si no hay CURRENT_USER (usuario no logueado), usuarioData queda null
                // -----------------------------------

                const solicitudData = {
                    id_hechoAsociado: document.getElementById('ID_hechoAsociado').value,
                    justificacion: justificacion,
                    usuario: usuarioData
                };

                console.log('Enviando solicitud:', JSON.stringify(solicitudData));

                const response = await fetch(URL_PUBLICA + '/solicitudEliminacion', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(solicitudData)
                });

                if (response.ok) {
                    mensajeExito.style.display = 'block';
                    setTimeout(() => {
                        const hechoId = document.getElementById('ID_hechoAsociado').value;
                        window.location.href = '/hechos/' + encodeURIComponent(hechoId);
                    }, 2000);
                } else {
                    const text = await response.text();
                    throw new Error(`Error ${response.status}: ${text}`);
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
    }
});