<#assign pageTitle = "Colecciones">
<#assign content>
<div class="container">
    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="/" class="header-link back-link">&larr; Volver al inicio</a>
    </div>

    <h1 class="main-title">Listado de Colecciones</h1>

    <div class="form-actions" style="margin-bottom: 20px;">
        <a href="/crear-coleccion" class="btn btn-primary">➕ Nueva Colección</a>
    </div>

    <#if colecciones?? && (colecciones?size > 0)>
        <div class="list column gap-16">
            <#list colecciones as c>
                <div class="card">
                    <h3>${c.titulo!''}</h3>
                    <p>${c.descripcion!''}</p>
                    <small><b>Algoritmo:</b> ${c.algoritmoDeConsenso!''}</small>

                    <div style="margin-top:10px; display:flex; gap:8px; flex-wrap:wrap;">
                        <a href="/colecciones/${c.handle!''}" class="btn btn-secondary btn-sm">👁️ Ver Detalle</a>

                        <#if rolUsuario = "ADMINISTRADOR">
                            <a href="/editar-coleccion/${c.handle!''}" class="btn btn-sm btn-outline">✏️ Editar</a>

                            <!-- Botón de Eliminar - Versión más simple -->
                            <button
                                onclick="confirmarEliminacion('${c.handle!''}', '${(c.titulo!"")?js_string}')"
                                class="btn btn-sm btn-danger">
                                🗑️ Eliminar
                            </button>
                        </#if>
                    </div>
                </div>
            </#list>
        </div>
    <#else>
        <div class="empty-state">
            <div class="empty-state-icon">📭</div>
            <h3>No hay colecciones creadas aún</h3>
            <p>Podés crear una nueva colección desde el botón de arriba.</p>
        </div>
    </#if>
</div>

<!-- Modal de confirmación -->
<div id="modalConfirmacion" class="modal" style="display:none;">
    <div class="modal-content">
        <h3>Confirmar Eliminación</h3>
        <p id="mensajeConfirmacion">¿Estás seguro de que querés eliminar esta colección?</p>
        <div style="display:flex; justify-content:flex-end; gap:10px; margin-top:20px;">
            <button onclick="cerrarModal()" class="btn btn-outline">Cancelar</button>
            <button onclick="eliminarColeccion()" class="btn btn-danger">Eliminar</button>
        </div>
    </div>
</div>

<script>
    let coleccionAEliminar = null;
    let tituloColeccion = '';

    function confirmarEliminacion(handle, titulo) {
coleccionAEliminar = handle;
tituloColeccion = titulo || 'Sin título';

// Usar concatenación de strings en lugar de template literal
var mensaje = '¿Estás seguro de que querés eliminar la colección "<strong>' +
                     (titulo || 'Sin título') +
                     '</strong>"?<br><small>Esta acción no se puede deshacer.</small>';

document.getElementById('mensajeConfirmacion').innerHTML = mensaje;
document.getElementById('modalConfirmacion').style.display = 'flex';
}

    function cerrarModal() {
document.getElementById('modalConfirmacion').style.display = 'none';
coleccionAEliminar = null;
tituloColeccion = '';
}

    function eliminarColeccion() {
if (!coleccionAEliminar) return;

// Mostrar loading
var botonEliminar = event.target;
botonEliminar.disabled = true;
botonEliminar.innerHTML = 'Eliminando...';

// Realizar petición DELETE
fetch('/api/colecciones/' + coleccionAEliminar, {
method: 'DELETE',
headers: {
'Content-Type': 'application/json'
}
        })
.then(function(response) {
if (response.ok) {
// Recargar la página después de eliminar
window.location.reload();
} else {
return response.json().then(function(data) {
throw new Error(data.message || 'Error al eliminar');
});
}
})
.catch(function(error) {
alert('Error: ' + error.message);
botonEliminar.disabled = false;
            botonEliminar.innerHTML = 'Eliminar';
        })
.finally(function() {
cerrarModal();
});
}

// Cerrar modal al hacer clic fuera
document.getElementById('modalConfirmacion').addEventListener('click', function(e) {
if (e.target === this) {
cerrarModal();
}
    });

    // Cerrar con Escape
    document.addEventListener('keydown', function(e) {
if (e.key === 'Escape') {
cerrarModal();
}
    });
</script>

<style>
    /* Estilos para el modal */
.modal {
position: fixed;
top: 0;
left: 0;
width: 100%;
height: 100%;
background: rgba(0, 0, 0, 0.5);
display: flex;
align-items: center;
justify-content: center;
z-index: 1000;
}

.modal-content {
background: white;
padding: 30px;
border-radius: 8px;
max-width: 500px;
width: 90%;
box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.btn-danger {
background-color: #dc3545;
color: white;
border: 1px solid #dc3545;
}

.btn-danger:hover {
background-color: #c82333;
border-color: #bd2130;
}

.btn-outline {
background-color: transparent;
color: #6c757d;
border: 1px solid #6c757d;
}

.btn-outline:hover {
background-color: #6c757d;
color: white;
}

.btn-sm {
padding: 6px 12px;
font-size: 14px;
}
</style>
</#assign>

<#include "layout.ftl">