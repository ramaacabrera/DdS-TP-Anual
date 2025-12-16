const solicitudId = window.solicitudData?.id;
const tipoSolicitud = window.solicitudData?.tipo;

async function procesarSolicitud(accion) {
    try {
        if (!solicitudId) {
            Swal.fire({
                title: 'Error',
                text: 'ID de solicitud no disponible',
                icon: 'error',
                background: '#ffecec',
                confirmButtonColor: '#ff4f81'
            });
            return;
        }

        // --- Confirmación SweetAlert ---
        const confirmMessage = accion === 'ACEPTADA'
            ? '¿Estás seguro de aceptar esta solicitud? El hecho será afectado según su tipo.'
            : '¿Estás seguro de rechazar esta solicitud?';

        const confirmResult = await Swal.fire({
            title: 'Confirmación',
            text: confirmMessage,
            icon: 'warning',
            background: '#fff7f7',
            color: '#333',
            showCancelButton: true,
            confirmButtonText: 'Sí, continuar',
            cancelButtonText: 'Cancelar',
            confirmButtonColor: '#ff4f81',
            cancelButtonColor: '#777',
            showClass: {
                popup: 'animate__animated animate__jello'
            }
        });

        if (!confirmResult.isConfirmed) return;

        // --- Endpoint ---
        const endpoint = `/admin/solicitudes/${tipoSolicitud}/${solicitudId}`;

        console.log('Enviando PATCH a:', endpoint, 'con acción:', accion);

        // --- Envío del PATCH ---
        const response = await fetch(endpoint, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ accion: accion })
        });

        if (response.ok) {
            await Swal.fire({
                title: '¡Listo!',
                text: `Solicitud ${accion.toLowerCase()} correctamente`,
                icon: 'success',
                background: '#fffbec',
                color: '#333',
                confirmButtonColor: '#ff4f81',
                confirmButtonText: 'Aceptar',
                showClass: {
                    popup: 'animate__animated animate__bounceIn'
                },
                hideClass: {
                    popup: 'animate__animated animate__bounceOut'
                }
            });

            window.location.href = '/admin/solicitudes/eliminacion';
        } else {
            const errorText = await response.text();
            Swal.fire({
                title: 'Error',
                text: `Error ${response.status}: ${errorText}`,
                icon: 'error',
                background: '#ffecec',
                confirmButtonColor: '#ff4f81'
            });
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.fire({
            title: 'Ups...',
            text: 'Error al procesar la solicitud. Intente nuevamente.',
            icon: 'error',
            background: '#ffecec',
            confirmButtonColor: '#ff4f81'
        });
    }
}

// --- Botones ---
function aceptarSolicitud() {
    procesarSolicitud('ACEPTADA');
}

function rechazarSolicitud() {
    procesarSolicitud('RECHAZADA');
}
