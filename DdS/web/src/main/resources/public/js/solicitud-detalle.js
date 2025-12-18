const solicitudId = window.solicitudData?.id;
const tipoSolicitud = window.solicitudData?.tipo;

// Variables para rastrear cambios (solo aplica para modificación)
let valoresOriginales = {};
let hayCambios = false;

// Inicializar al cargar la página - SOLO para modificación
document.addEventListener('DOMContentLoaded', function() {
    // Solo inicializar para solicitudes de modificación
    if (tipoSolicitud === 'modificacion') {
        // Almacenar valores originales usando un identificador único
        document.querySelectorAll('.input-modificado').forEach((input, index) => {
            const campo = input.closest('.cambio-row')?.getAttribute('data-campo');
            const key = campo ? `${campo}_${index}` : `input_${index}`;

            // Asignar un atributo data-key para identificarlo
            input.setAttribute('data-key', key);

            // Guardar valor original
            valoresOriginales[key] = input.value;

            // Para selects, guardar también la opción seleccionada
            if (input.tagName === 'SELECT') {
                // Marcar la opción actual como seleccionada
                Array.from(input.options).forEach(option => {
                    if (option.value === input.value) {
                        option.setAttribute('data-original-selected', 'true');
                    }
                });
            }

            // Agregar listeners para detectar cambios
            input.addEventListener('input', detectarCambios);
            input.addEventListener('change', detectarCambios);

            // Para selects, también escuchar el evento de apertura/cierre
            input.addEventListener('click', detectarCambios);
        });

        // Verificar cambios iniciales después de un pequeño delay
        setTimeout(detectarCambios, 100);
    }
});

function detectarCambios() {
    // Si no es modificación, no hay cambios posibles
    if (tipoSolicitud !== 'modificacion') {
        hayCambios = false;
        return false;
    }

    hayCambios = false;
    const aceptarBtn = document.querySelector('.btn-success');

    // Verificar si algún input ha cambiado
    document.querySelectorAll('.input-modificado').forEach(input => {
        const key = input.getAttribute('data-key');
        const valorActual = input.value;
        const valorOriginal = valoresOriginales[key];

        // Para selects, verificar si la opción seleccionada es diferente
        if (input.tagName === 'SELECT') {
            const opcionOriginal = Array.from(input.options).find(opt =>
                opt.hasAttribute('data-original-selected')
            );
            const opcionActual = Array.from(input.options).find(opt =>
                opt.value === valorActual && opt.selected
            );

            if (opcionOriginal && opcionActual && opcionOriginal.value !== opcionActual.value) {
                hayCambios = true;
            }
        }
        // Para textareas e inputs
        else if (valorActual !== valorOriginal && valorActual !== 'SIN_CAMBIOS_MANUALES') {
            hayCambios = true;
        }
    });

    // Actualizar botón si hay cambios (solo para modificación)
    if (aceptarBtn && tipoSolicitud === 'modificacion') {
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

        // Solo aplicar lógica de cambios para solicitudes de modificación
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

        // --- Confirmación SweetAlert ---
        let confirmMessage, confirmButtonText, confirmIcon;
        let showDenyButton = false;
        let denyButtonText = '';

        if (accion === 'ACEPTADA') {
            // Lógica diferente para eliminación vs modificación
            if (tipoSolicitud === 'modificacion') {
                if (hayCambios) {
                    confirmMessage = 'Has realizado cambios a los valores propuestos. ¿Deseas aceptar con estas sugerencias?';
                    confirmButtonText = 'Aceptar con sugerencias';
                    confirmIcon = 'question';
                    showDenyButton = true;
                    denyButtonText = 'Aceptar sin sugerencias';
                } else {
                    confirmMessage = '¿Estás seguro de aceptar esta solicitud? El hecho será modificado según los cambios propuestos.';
                    confirmButtonText = 'Aceptar Solicitud';
                    confirmIcon = 'warning';
                }
            } else {
                // Para eliminación - mensaje específico
                confirmMessage = '¿Estás seguro de aceptar esta solicitud de ELIMINACIÓN? El hecho será eliminado permanentemente.';
                confirmButtonText = 'Aceptar Eliminación';
                confirmIcon = 'warning';
                // Para eliminación, NO mostrar botón "Aceptar sin sugerencias"
                showDenyButton = false;
            }
        } else {
            // Para rechazo (ambos tipos)
            confirmMessage = '¿Estás seguro de rechazar esta solicitud?';
            confirmButtonText = 'Sí, rechazar';
            confirmIcon = 'warning';
        }

        const swalOptions = {
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
            }
        };

        // Solo agregar denyButton si es una modificación con cambios
        if (showDenyButton) {
            swalOptions.showDenyButton = true;
            swalOptions.denyButtonText = denyButtonText;
            swalOptions.denyButtonColor = '#3085d6';
        }

        const confirmResult = await Swal.fire(swalOptions);

        if (confirmResult.isDenied && tipoSolicitud === 'modificacion') {
            // Si eligió "Aceptar sin sugerencias", restaurar valores originales
            document.querySelectorAll('.input-modificado').forEach(input => {
                const key = input.getAttribute('data-key');
                if (valoresOriginales[key] !== undefined) {
                    input.value = valoresOriginales[key];
                }
            });
            hayCambios = false;
        } else if (!confirmResult.isConfirmed && !confirmResult.isDenied) {
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
            let successMessage = `Solicitud ${accion.toLowerCase()} correctamente`;

            if (tipoSolicitud === 'modificacion' && hayCambios) {
                successMessage += ' con sugerencias';
            }

            await Swal.fire({
                title: '¡Listo!',
                text: successMessage,
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
    // Si es eliminación, procesar directamente sin verificar cambios
    if (tipoSolicitud === 'eliminacion') {
        procesarSolicitud('ACEPTADA');
        return;
    }

    // Solo para modificación: verificar cambios
    const tieneCambios = detectarCambios();
    procesarSolicitud('ACEPTADA');
}

function rechazarSolicitud() {
    procesarSolicitud('RECHAZADA');
}