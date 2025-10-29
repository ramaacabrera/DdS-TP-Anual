<#assign pageTitle = "Editar Colección">
<#assign content>
<div class="container">
    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="/colecciones" class="header-link back-link">&larr; Volver a colecciones</a>
    </div>

    <h1 class="main-title">Editar Colección</h1>

    <!-- Script con variables del backend -->
    <script>
        const URL_ADMIN = '${urlAdmin!"http://localhost:8081"}';
        const COLECCION_ID = '${coleccionId}';
    </script>

    <!-- Formulario de edición -->
    <form id="form-editar-coleccion" class="form-container" method="POST" action="/api/colecciones/${coleccionId}">
        <!-- Título -->
        <div class="form-group">
            <label for="titulo" class="form-label">Título *</label>
            <input type="text" id="titulo" name="titulo" class="form-input" required>
        </div>

        <!-- Descripción -->
        <div class="form-group">
            <label for="descripcion" class="form-label">Descripción *</label>
            <textarea id="descripcion" name="descripcion" class="form-textarea" rows="4" required></textarea>
        </div>

        <!-- Algoritmo -->
        <div class="form-group">
            <label for="algoritmo" class="form-label">Algoritmo de consenso *</label>
            <select id="algoritmo" name="algoritmo" class="form-select" required>
                <#list algoritmos as alg>
                    <option value="${alg}">${alg?capitalize}</option>
                </#list>
            </select>
        </div>

        <!-- Fuentes -->
        <div class="form-section">
            <h3 class="form-section-title"> Fuentes asociadas</h3>
            <select id="fuentes" name="fuentes" multiple class="form-select" size="4">
                <#list fuentes as fuente>
                    <option value="${fuente}">${fuente?capitalize}</option>
                </#list>
            </select>
        </div>

        <!-- Criterios -->
        <div class="form-section">
            <h3 class="form-section-title"> Criterios de pertenencia</h3>
            <div class="form-group">
                <label for="criteriosDePertenencia" class="form-label">Criterios (opcional)</label>
                <textarea id="criteriosDePertenencia" name="criteriosDePertenencia" class="form-textarea"
                          placeholder="Ej: CriterioDeTexto: palabra, CriterioCategoria: ambiental"></textarea>
                <small class="form-help">Separá varios criterios con comas</small>
            </div>
        </div>

        <!-- Botones -->
        <div class="form-actions">
            <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
            <button type="submit" class="btn btn-primary">Guardar cambios</button>
        </div>

        <!-- Mensajes -->
        <div id="mensaje-exito" class="mensaje mensaje-exito" style="display:none;">
             Colección actualizada correctamente.
        </div>
        <div id="mensaje-error" class="mensaje mensaje-error" style="display:none;">
             Error al actualizar la colección. Intente nuevamente.
        </div>
    </form>
</div>

<!-- Script JS para precargar y actualizar -->
<script>
document.addEventListener("DOMContentLoaded", async () => {
    try {
        const res = await fetch(`${URL_ADMIN}/colecciones/${COLECCION_ID}`);
        if (!res.ok) throw new Error("Error al obtener los datos de la colección");
        const coleccion = await res.json();

        // Precarga de campos
        document.getElementById("titulo").value = coleccion.titulo || "";
        document.getElementById("descripcion").value = coleccion.descripcion || "";
        document.getElementById("algoritmo").value = coleccion.algoritmoDeConsenso || "";

        // Fuentes
        const fuentesSelect = document.getElementById("fuentes");
        if (coleccion.fuentes && coleccion.fuentes.length > 0) {
            Array.from(fuentesSelect.options).forEach(opt => {
                if (coleccion.fuentes.map(f => f.tipoDeFuente).includes(opt.value)) {
                    opt.selected = true;
                }
            });
        }

        // Criterios
        const criterios = (coleccion.criteriosDePertenencia || []).map(c => c.tipo).join(", ");
        document.getElementById("criteriosDePertenencia").value = criterios;

    } catch (err) {
        console.error("Error cargando colección:", err);
        document.getElementById("mensaje-error").style.display = "block";
        document.getElementById("mensaje-error").innerText = "No se pudo cargar la colección para editar.";
    }
});

document.getElementById("form-editar-coleccion").addEventListener("submit", async (e) => {
    e.preventDefault();

    const data = {
        titulo: document.getElementById("titulo").value,
        descripcion: document.getElementById("descripcion").value,
        algoritmoDeConsenso: document.getElementById("algoritmo").value,
        fuentes: Array.from(document.getElementById("fuentes").selectedOptions).map(o => o.value),
        criteriosDePertenencia: document.getElementById("criteriosDePertenencia").value.split(",").map(c => c.trim())
    };

    try {
        const res = await fetch(`${URL_ADMIN}/colecciones/${COLECCION_ID}`, {
            method: "PUT",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data)
        });

        if (!res.ok) throw new Error("Error al guardar los cambios");
        document.getElementById("mensaje-exito").style.display = "block";
        setTimeout(() => window.location.href = "/colecciones", 1500);
    } catch (err) {
        document.getElementById("mensaje-error").style.display = "block";
        console.error(err);
    }
});
</script>
</#assign>
<#include "layout.ftl">


