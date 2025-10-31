<#assign pageTitle = "Crear Colección">
<#assign content>
    <div class="container">
        <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
            <a href="/" class="header-link back-link">&larr; Volver al inicio</a>
        </div>

        <h1 class="main-title">Crear nueva colección</h1>

        <#if resultado??>
            <h2 style="text-align: center">${resultado}</h2>
        </#if>

        <script>
            const URL_ADMIN = '${urlAdmin!"http://localhost:8081"}';
        </script>

        <form id="form-crear-coleccion" class="form-container" method="POST" action="/colecciones">
            <!-- Título -->
            <div class="form-group">
                <label for="titulo" class="form-label">Título *</label>
                <input type="text" id="titulo" name="titulo" class="form-input" required placeholder="Ej: Colección de reportes ambientales">
            </div>

            <!-- Descripción -->
            <div class="form-group">
                <label for="descripcion" class="form-label">Descripción *</label>
                <textarea id="descripcion" name="descripcion" class="form-textarea" rows="4" required placeholder="Describa brevemente la colección..."></textarea>
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

            <!-- Criterios -->
            <div class="form-section">
                <h3 class="form-section-title">Criterios de pertenencia</h3>
                <div class="form-group">
                    <label for="tipoCriterio" class="form-label">Tipo de criterio</label>
                    <select id="tipoCriterio" name="criteriosDePertenencia" class="form-select" onchange="mostrarCampoCriterio()">
                        <option value="CriterioDeTexto">Texto</option>
                        <option value="CriterioCategoria">Categoría</option>
                        <option value="CriterioTipoMultimedia">Tipo Multimedia</option>
                        <option value="CriterioEtiquetas">Etiquetas</option>
                        <option value="CriterioFecha">Fecha</option>
                        <option value="CriterioUbicacion">Ubicación</option>
                        <option value="CriterioContribuyente">Contribuyente</option>
                        <option value="CriterioTipoFuente">Tipo Fuente</option>
                    </select>
                    <div id="campoCriterio"></div>
                </div>
            </div>

            <!-- Fuentes -->
            <div class="form-section">
                <h3 class="form-section-title">Fuentes</h3>
                <label for="fuentes" class="form-label">Seleccioná una o más fuentes:</label><br>
                <select id="fuentes" name="fuentes" multiple class="form-select" size="4">
                    <#list fuentes as fuente>
                        <option value="${fuente}">${fuente?capitalize}</option>
                    </#list>
                </select>
            </div>

            <!-- Listas iniciales -->
            <div class="form-section">
                <h3 class="form-section-title">Hechos incluidos en la colección</h3>
                <div id="lista-hechos" class="lista-vacia">
                    <p class="texto-placeholder">Aún no hay hechos asociados.</p>
                    <!-- Acá se cargan los hechos cuando se agreguen -->
                </div>
            </div>

            <div class="form-section">
                <h3 class="form-section-title">Hechos consensuados</h3>
                <div id="lista-consensuados" class="lista-vacia">
                    <p class="texto-placeholder">Aún no hay hechos consensuados.</p>
                    <!-- Acá se miuestran los hechos filtrados por el algoritmo de consenso -->
                </div>
            </div>

            <!-- Botones -->
            <div class="form-actions">
                <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
                <button type="submit" class="btn btn-primary">Crear Colección</button>
            </div>

            <!-- Mensajes -->
            <div id="mensaje-exito" class="mensaje mensaje-exito" style="display:none;">
                ✅ Colección creada exitosamente.
            </div>
            <div id="mensaje-error" class="mensaje mensaje-error" style="display:none;">
                ❌ Error al crear la colección. Intente nuevamente.
            </div>
        </form>
    </div>
</#assign>
<#include "layout.ftl">

