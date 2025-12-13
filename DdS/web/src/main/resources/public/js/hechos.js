function toggleFilters() {
    const filtersContent = document.getElementById('filtersContent');
    const toggleBtn = document.querySelector('.filters-header button');

    filtersContent.classList.toggle('collapsed');
    toggleBtn.classList.toggle('rotated');
}

// Eliminar la aplicación automática de filtros
document.addEventListener('DOMContentLoaded', function() {
    // Ya no hay event listeners para cambios automáticos en filtros
    // Los filtros solo se aplican al hacer clic en "Aplicar" o "Buscar"

    // Opcional: puedes agregar funcionalidad para limpiar filtros si lo deseas
    const clearButtons = document.querySelectorAll('.btn-ghost');
    clearButtons.forEach(button => {
        if (button.textContent.includes('Limpiar')) {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                // Redirigir a la página sin parámetros
                window.location.href = window.BASE_HREF || '/hechos';
            });
        }
    });
});


function abrirModalEtiqueta(hechoId) {
    document.getElementById('modalHechoId').value = hechoId;
    document.getElementById('inputEtiqueta').value = ''; // Limpiar input
    document.getElementById('modalEtiqueta').classList.remove('hidden');
    document.getElementById('inputEtiqueta').focus();
}

function cerrarModalEtiqueta() {
    document.getElementById('modalEtiqueta').classList.add('hidden');
}

// Cerrar al hacer clic fuera del modal
document.getElementById('modalEtiqueta').addEventListener('click', function(e) {
    if (e.target === this) {
        cerrarModalEtiqueta();
    }
});

async function enviarEtiqueta(event) {
    event.preventDefault();

    const hechoId = document.getElementById('modalHechoId').value;
    const nombreEtiqueta = document.getElementById('inputEtiqueta').value;

    if(!nombreEtiqueta.trim()) return;

    // 1. Crear el FormData y agregar el campo
    const formData = new FormData();
    formData.append("nombre", nombreEtiqueta);

    try {
        let url = URL_ADMIN + 'api/hecho/' + hechoId + '/etiquetas';

        const response = await fetch(url, {
            method: 'PATCH',
            headers: {
                'accessToken': ACCESS_TOKEN
            },
            // 3. Pasar el objeto formData directo
            body: formData
        });

        if (response.ok) {
            window.location.reload();
        } else {
            alert('Error al guardar la etiqueta.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Hubo un error de conexión');
    }
}