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
                window.location.href = '${baseHref!"/hechos"}';
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
    console.log(hechoId);
    console.log(nombreEtiqueta);
    if(!nombreEtiqueta.trim()) return;

    try {
        let url = URL_ADMIN + 'api/hecho/' + hechoId + '/etiquetas'
        console.log(url)
        const response = await fetch(url, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'accessToken': ACCESS_TOKEN
            },
            body: JSON.stringify({ nombre: nombreEtiqueta })
            // Si tu backend espera Form Data:
            // body: 'nombreEtiqueta=' + encodeURIComponent(nombreEtiqueta)
        });

        if (response.ok) {
            // Recargar la página para ver los cambios
            window.location.reload();
        } else {
            alert('Error al guardar la etiqueta. Verifica si ya existe.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Hubo un error de conexión');
    }
}