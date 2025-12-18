const solicitudId = window.solicitudData?.id;
const tipoSolicitud = window.solicitudData?.tipo;

// Variables para rastrear cambios
let valoresOriginales = {};
let hayCambios = false;

// Inicializar al cargar la página
document.addEventListener('DOMContentLoaded', function() {
    // Almacenar valores originales
    document.querySelectorAll('.input-modificado').forEach(input => {
        const key = input.name || input.id || input.className;
        if (input.tagName === 'TEXTAREA') {
            valoresOriginales[key] = input.value;
        } else if (input.tagName === 'SELECT') {
            valoresOriginales[key] = input.value;
        } else {
            valoresOriginales[key] = input.value;
        }

        // Agregar listeners para detectar cambios
        input.addEventListener('input', detectarCambios);
        input.addEventListener('change', detectarCambios);
    });

    // Cambiar texto inicial del botón si es necesario
    detectarCambios();
});

function detectarCambios() {
    hayCambios = false;
    const aceptarBtn = document.querySelector('.btn-success');

    // Verificar si algún input ha cambiado
    document.querySelectorAll('.input-modificado').forEach(input => {
        const key = input.name || input.id || input.className;
        const valorActual = input.value;
        const valorOriginal = valoresOriginales[key];

        if (valorActual !== valorOriginal && valorActual !== 'SIN_CAMBIOS_MANUALES') {
            hayCambios = true;
        }
    });

    // Actualizar botón si hay cambios
    if (aceptarBtn) {
        if (hayCambios) {
            aceptarBtn.innerHTML = 'Aceptar con sugerencias';
            aceptarBtn.setAttribute('data-con-sugerencias', 'true');
            aceptarBtn.classList.add('con-sugerencias');
        } else {
            aceptarBtn.innerHTML = 'Aceptar Solicitud';
            aceptarBtn.setAttribute('data-con-sugerencias', 'false');
            aceptarBtn.classList.remove('con-sugerencias');
        }
    }

    return hayCambios;
}


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

        let datosExtra = {};

        if (accion === 'ACEPTADA' && tipoSolicitud === 'modificacion') {
            const cambios = [];
            document.querySelectorAll('.cambio-row').forEach(row => {
                const campo = row.getAttribute('data-campo');
                const input = row.querySelector('.input-modificado');
                if (input && input.value !== 'SIN_CAMBIOS_MANUALES') {
                    cambios.push({
                        campo: campo,
                        valorFinal: input.value
                    });
                }
            });
            datosExtra.cambiosAprobados = cambios;

            // Agregar flag para indicar si se aceptó con sugerencias
            const aceptarBtn = document.querySelector('.btn-success');
            const conSugerencias = aceptarBtn?.getAttribute('data-con-sugerencias') === 'true';
            datosExtra.conSugerencias = conSugerencias;
        }

        // --- Confirmación SweetAlert (modificada para preguntar por sugerencias) ---
        let confirmMessage, confirmButtonText, confirmIcon;

        if (accion === 'ACEPTADA') {
            const conSugerencias = hayCambios;

            if (conSugerencias) {
                confirmMessage = 'Has realizado cambios a los valores propuestos. ¿Deseas aceptar con estas sugerencias?';
                confirmButtonText = 'Aceptar con sugerencias';
                confirmIcon = 'question';
            } else {
                confirmMessage = '¿Estás seguro de aceptar esta solicitud? El hecho será afectado según su tipo.';
                confirmButtonText = 'Aceptar sin sugerencias';
                confirmIcon = 'warning';
            }
        } else {
            confirmMessage = '¿Estás seguro de rechazar esta solicitud?';
            confirmButtonText = 'Sí, rechazar';
            confirmIcon = 'warning';
        }

        const confirmResult = await Swal.fire({
            title: 'Confirmación',
            text: confirmMessage,
            icon: confirmIcon,
            background: '#fff7f7',
            color: '#333',
            showCancelButton: true,
            confirmButtonText: confirmButtonText,
            cancelButtonText: 'Cancelar',
            confirmButtonColor: '#ff4f81',
            cancelButtonColor: '#777',
            showClass: {
                popup: 'animate__animated animate__jello'
            },
            // Para aceptaciones con cambios, mostrar botón adicional
            showDenyButton: accion === 'ACEPTADA' && hayCambios,
            denyButtonText: 'Aceptar sin sugerencias',
            denyButtonColor: '#3085d6'
        });

        if (confirmResult.isDenied) {
            // Si eligió "Aceptar sin sugerencias", restaurar valores originales
            document.querySelectorAll('.input-modificado').forEach(input => {
                const key = input.name || input.id || input.className;
                if (valoresOriginales[key] !== undefined) {
                    input.value = valoresOriginales[key];
                }
            });
            hayCambios = false;
        } else if (!confirmResult.isConfirmed) {
            return; // Cancelar
        }

        // --- Endpoint ---
        const endpoint = `/admin/solicitudes/${tipoSolicitud}/${solicitudId}`;

        console.log('Enviando PATCH a:', endpoint, 'con acción:', accion, 'conSugerencias:', datosExtra.conSugerencias);

        // --- Envío del PATCH ---
        const response = await fetch(endpoint, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                accion: accion,
                ...datosExtra
            })
        });

        if (response.ok) {
            await Swal.fire({
                title: '¡Listo!',
                text: `Solicitud ${accion.toLowerCase()} correctamente ${hayCambios ? 'con sugerencias' : ''}`,
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

            window.location.href = `/admin/solicitudes/${tipoSolicitud}`;
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
    // Primero detectar si hay cambios
    const tieneCambios = detectarCambios();

    if (tieneCambios) {
        procesarSolicitud('ACEPTADA');
    } else {
        // Si no hay cambios, proceder normalmente
        procesarSolicitud('ACEPTADA');
    }
}

function rechazarSolicitud() {
    procesarSolicitud('RECHAZADA');
}