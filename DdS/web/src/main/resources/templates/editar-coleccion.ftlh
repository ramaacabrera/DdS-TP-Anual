<#assign pageTitle = "Editar Colección">
<#assign content>
    <div class="container">
        <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
            <a href="/colecciones" class="header-link back-link">&larr; Volver a colecciones</a>
        </div>

        <h1 class="main-title">Editar Colección</h1>

        <script>
            const URL_ADMIN = '${urlAdmin!"http://localhost:8081"}';
            const COLECCION_ID = '${coleccionId}';
        </script>

        <!-- Formulario -->
        <form id="form-editar-coleccion" class="form-container" method="POST" action="/api/colecciones/${coleccionId}">
            <!-- Título -->
            <div class="form-group">
                <label for="titulo" class="form-label">Título *</label>
                <input type="text" id="titulo" name="titulo" class="form-input" value="${coleccion.titulo!''}" required>
            </div>

            <!-- Descripción -->
            <div class="form-group">
                <label for="descripcion" class="form-label">Descripción *</label>
                <textarea id="descripcion" name="descripcion" class="form-textarea" rows="4" required>${coleccion.descripcion!''}</textarea>
            </div>

            <!-- Algoritmo -->
            <div class="form-group">
                <label for="algoritmo" class="form-label">Algoritmo de consenso *</label>
                <select id="algoritmo" name="algoritmo" class="form-select" required>
                    <#list algoritmos as alg>
                        <option value="${alg}" <#if coleccion.algoritmoDeConsenso?string == alg>selected</#if>>
                            ${alg?capitalize}
                        </option>
                    </#list>
                </select>
            </div>

            <!-- Fuentes -->
            <div class="form-section">
                <h3 class="form-section-title"> Fuentes asociadas </h3>
                <select id="fuentes" name="fuentes" multiple class="form-select" size="4">
                    <#list fuentes as fuente>
                        <option value="${fuente}"
                                <#if coleccion.fuentes?has_content && coleccion.fuentes?map(f -> f.tipoDeFuente)?seq_contains(fuente)>selected</#if>>
                            ${fuente?capitalize}
                        </option>
                    </#list>
                </select>
            </div>

            <!-- Criterios -->
            <div class="form-section">
                <h3 class="form-section-title"> Criterios de pertenencia </h3>
                <textarea id="criteriosDePertenencia" name="criteriosDePertenencia" class="form-textarea" rows="3">
<#if coleccion.criteriosDePertenencia??>
    ${coleccion.criteriosDePertenencia?map(c -> c.tipo)?join(", ")}
</#if>
            </textarea>
                <small class="form-help">Separá varios criterios con comas</small>
            </div>

            <!-- Hechos -->
            <div class="form-section">
                <h3 class="form-section-title">Hechos asociados</h3>
                <#if coleccion.hechos?? && coleccion.hechos?size > 0>
                    <ul class="list-group">
                        <#list coleccion.hechos as hecho>
                            <li class="list-item">${hecho.titulo}</li>
                        </#list>
                    </ul>
                <#else>
                    <p class="texto-placeholder">No hay hechos asociados aún.</p>
                </#if>
            </div>

            <!-- Hechos consensuados -->
            <div class="form-section">
                <h3 class="form-section-title">Hechos consensuados</h3>
                <#if coleccion.hechosConsensuados?? && coleccion.hechosConsensuados?size > 0>
                    <ul class="list-group">
                        <#list coleccion.hechosConsensuados as hecho>
                            <li class="list-item">${hecho.titulo}</li>
                        </#list>
                    </ul>
                <#else>
                    <p class="texto-placeholder">No hay hechos consensuados aún.</p>
                </#if>
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

    <!-- Script para envío -->
    <script>
        document.getElementById("form-editar-coleccion").addEventListener("submit", async (e) => {
            e.preventDefault();

            const data = {
                titulo: document.getElementById("titulo").value,
                descripcion: document.getElementById("descripcion").value,
                algoritmoDeConsenso: document.getElementById("algoritmo").value,
                fuentes: Array.from(document.getElementById("fuentes").selectedOptions).map(o => o.value),
                criteriosDePertenencia: document.getElementById("criteriosDePertenencia").value
                    .split(",")
                    .map(c => c.trim())
                    .filter(c => c.length > 0)
            };

            try {
                const res = await fetch(`${URL_ADMIN}/colecciones/${COLECCION_ID}`, {
                    method: "PUT",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(data)
                });

                if (!res.ok) throw new Error("Error al guardar los cambios");
                document.getElementById("mensaje-exito").style.display = "block";
                setTimeout(() => window.location.href = `/colecciones/${COLECCION_ID}`, 1500);
            } catch (err) {
                console.error(err);
                document.getElementById("mensaje-error").style.display = "block";
            }
        });
    </script>
</#assign>

<#include "layout.ftl">
