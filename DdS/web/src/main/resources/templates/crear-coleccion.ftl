<#assign pageTitle = "Crear Colección">
<#assign additionalCss = ["/css/styleCrearColeccion.css"]>
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

                <!-- Contenedor para los criterios agregados -->
                <div id="criterios-agregados" class="criterios-container">
                    <!-- Aquí se mostrarán los criterios agregados -->
                </div>

                <!-- Formulario para agregar nuevo criterio -->
                <div class="nuevo-criterio-card">
                    <h4 class="form-subtitle">Agregar nuevo criterio</h4>
                    <div class="form-group">
                        <label for="tipoCriterio" class="form-label">Tipo de criterio</label>
                        <select id="tipoCriterio" class="form-select">
                            <option value="">Seleccione un tipo</option>
                            <option value="CriterioDeTexto">Texto</option>
                            <option value="CriterioTipoMultimedia">Tipo Multimedia</option>
                            <option value="CriterioEtiquetas">Etiquetas</option>
                            <option value="CriterioFecha">Fecha</option>
                            <option value="CriterioUbicacion">Ubicación</option>
                            <option value="CriterioContribuyente">Contribuyente</option>
                            <option value="CriterioTipoFuente">Tipo de Fuente</option>
                        </select>
                        <div id="campoCriterio" class="campo-criterio-dinamico"></div>
                        <button type="button" id="btn-agregar-criterio" class="btn btn-secondary" style="margin-top: 10px;" disabled>
                            + Agregar criterio
                        </button>
                    </div>
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
        <div id="modal-exito" class="modal-overlay">
            <div class="modal-content">
                <div class="modal-icon">✅</div>
                <h2 class="modal-title">¡Colección creada con éxito!</h2>
                <p class="modal-message">La colección ha sido creada exitosamente. Serás redirigido en 3 segundos...</p>
                <button id="btn-redirigir" class="modal-button">Ir a Colecciones</button>
            </div>
        </div>
    </div>
</#assign>

<script src="/js/crear-coleccion.js"></script>

<#include "layout.ftl">
