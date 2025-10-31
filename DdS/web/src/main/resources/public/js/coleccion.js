// coleccion.js - Funcionalidades simples para la página de colección

function editarColeccion(id) {
    if (id) {
        window.location.href = '/editar-coleccion/' + id;
    } else {
        alert('No se puede editar: ID no disponible');
    }
}

function verEstadisticas(id) {
    if (id) {
        window.location.href = '/estadisticas?uuid=' + id;
    } else {
        alert('No se pueden ver estadísticas: ID no disponible');
    }
}

function verHecho(id) {
    if (id) {
        window.location.href = '/hechos/' + id;
    } else {
        alert('No se puede ver el hecho: ID no disponible');
    }
}