console.log("hola");

// Variables globales para almacenar el estado
let accionActual = '';
let solicitudId = 0;
let tipoSolicitud = '';

// Inicializar cuando el DOM esté cargado
document.addEventListener('DOMContentLoaded', function() {
    // Obtener datos desde las variables globales definidas en el FTL
    solicitudId = window.solicitudData?.id || 0;
    tipoSolicitud = window.solicitudData?.tipo || '';

    configurarModal();
});

function procesarSolicitud(accion) {
    accionActual = accion;

    const mensajes = {
        'ACEPTADA': '¿Está seguro de que desea ACEPTAR esta solicitud?',
        'RECHAZADA': '¿Está seguro de que desea RECHAZAR esta solicitud?'
    };

    document.getElementById('modal-mensaje').textContent = mensajes[accion] || '¿Está seguro de que desea realizar esta acción?';
    document.getElementById('modal-titulo').textContent = `Confirmar ${accion.toLowerCase()}`;

    const modal = document.getElementById('modal-confirmacion');
    modal.style.display = 'flex';
}

function configurarModal() {
    const modal = document.getElementById('modal-confirmacion');
    const btnCancelar = document.getElementById('btn-cancelar-modal');
    const btnConfirmar = document.getElementById('btn-confirmar-modal');

    if (!modal || !btnCancelar || !btnConfirmar) {
        console.error('No se encontraron los elementos del modal');
        return;
    }

    // Cerrar modal al hacer clic en cancelar
    btnCancelar.addEventListener('click', function() {
        cerrarModal();
    });

    // Cerrar modal al hacer clic fuera del contenido
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            cerrarModal();
        }
    });

    // Confirmar acción
    btnConfirmar.addEventListener('click', function() {
        if (accionActual && solicitudId) {
            enviarSolicitud(accionActual);
        }
        cerrarModal();
    });
}

function cerrarModal() {
    const modal = document.getElementById('modal-confirmacion');
    modal.style.display = 'none';
    accionActual = '';
}

function enviarSolicitud(accion) {
    // Determinar la URL base según el tipo de solicitud
    const baseUrl = tipoSolicitud === 'eliminacion'
        ? '/admin/solicitudes-eliminacion'
        : '/admin/solicitudes-modificacion';

    const url = `${baseUrl}/${solicitudId}/procesar`;

    // Mostrar indicador de carga
    const btnConfirmar = document.getElementById('btn-confirmar-modal');
    const originalText = btnConfirmar.textContent;
    btnConfirmar.textContent = 'Procesando...';
    btnConfirmar.disabled = true;

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            accion: accion
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor');
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                // Mostrar mensaje de éxito y recargar la página
                mostrarMensajeExito(`Solicitud ${accion.toLowerCase()} correctamente`);
                setTimeout(() => {
                    window.location.reload();
                }, 1500);
            } else {
                throw new Error(data.message || 'Error al procesar la solicitud');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarMensajeError('Error al procesar la solicitud: ' + error.message);

            // Restaurar botón
            btnConfirmar.textContent = originalText;
            btnConfirmar.disabled = false;
        });
}

function mostrarMensajeExito(mensaje) {
    // Crear y mostrar mensaje de éxito
    const alerta = document.createElement('div');
    alerta.className = 'alert alert-success';
    alerta.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 10000;
        padding: 15px 20px;
        background-color: #d4edda;
        color: #155724;
        border: 1px solid #c3e6cb;
        border-radius: 5px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    `;
    alerta.textContent = mensaje;

    document.body.appendChild(alerta);

    // Remover después de 3 segundos
    setTimeout(() => {
        if (alerta.parentNode) {
            alerta.parentNode.removeChild(alerta);
        }
    }, 3000);
}

function mostrarMensajeError(mensaje) {
    // Crear y mostrar mensaje de error
    const alerta = document.createElement('div');
    alerta.className = 'alert alert-error';
    alerta.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 10000;
        padding: 15px 20px;
        background-color: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
        border-radius: 5px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    `;
    alerta.textContent = mensaje;

    document.body.appendChild(alerta);

    // Remover después de 5 segundos
    setTimeout(() => {
        if (alerta.parentNode) {
            alerta.parentNode.removeChild(alerta);
        }
    }, 5000);
}

// También exportar funciones para uso global (si es necesario)
window.procesarSolicitud = procesarSolicitud;