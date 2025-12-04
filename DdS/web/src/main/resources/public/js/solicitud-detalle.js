const solicitudId = window.solicitudData?.id;
const tipoSolicitud = window.solicitudData?.tipo;

const getEndpoint = () => {
    if (tipoSolicitud === 'eliminacion') {
        return `/admin/solicitudes/eliminacion/${solicitudId}`;
    } else if (tipoSolicitud === 'modificacion') {
        return `/admin/solicitudes/modificacion/${solicitudId}`;
    }
    throw new Error('Tipo de solicitud no válido');
};
async function procesarSolicitud(accion) {
    try {
        if (!solicitudId) {
            alert('ID de solicitud no disponible');
            return;
        }

        // Confirmación antes de proceder
        const confirmMessage = accion === 'ACEPTADA'
            ? '¿Estás seguro de aceptar esta solicitud? Esta acción cambiará el estado del hecho a "OCULTO" si es una eliminación.'
            : '¿Estás seguro de rechazar esta solicitud?';

        if (!confirm(confirmMessage)) {
            return;
        }

        // Usar la ruta que tienes configurada en application.js
        const endpoint = `/admin/solicitudes/${tipoSolicitud}/${solicitudId}`;

        console.log('Enviando PATCH a:', endpoint, 'con acción:', accion);

        const response = await fetch(endpoint, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ accion: accion }) // Enviar como objeto JSON
        });

        if (response.ok) {
            alert(`Solicitud ${accion.toLowerCase()} correctamente`);
            window.location.href = '/admin/solicitudes';
        } else {
            const errorText = await response.text();
            alert(`Error ${response.status}: ${errorText}`);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al procesar la solicitud. Intente nuevamente.');
    }
}

// Funciones específicas para cada acción
    function aceptarSolicitud() {
      procesarSolicitud('ACEPTADA');
    }

    function rechazarSolicitud() {
      procesarSolicitud('RECHAZADA');
    }