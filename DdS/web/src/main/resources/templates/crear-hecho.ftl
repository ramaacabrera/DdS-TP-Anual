<script src="/js/crear-hecho.js" defer></script>

<#assign pageTitle = "Reportar un Hecho">
<#assign content>
    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="/" class="header-link back-link">&larr; Volver al Inicio</a>
    </div>

    <h1 class="main-title">Reportar un Hecho</h1>

    <script>
        const URL_PUBLICA = '${urlPublica!"http://localhost:8087"}';
    </script>

    <form id="form-crear-hecho" class="form-container" method="POST" action="/api/hechos" enctype="multipart/form-data">

        <!-- Título -->
        <div class="form-group">
            <label for="titulo" class="form-label">Título *</label>
            <input type="text" id="titulo" name="titulo" class="form-input" required
                   placeholder="Ej: Incendio forestal en Parque Nacional">
        </div>

        <!-- Descripción -->
        <div class="form-group">
            <label for="descripcion" class="form-label">Descripción *</label>
            <textarea id="descripcion" name="descripcion" class="form-textarea" rows="4" required
                      placeholder="Describa el hecho con detalle..."></textarea>
        </div>

        <!-- Categoría -->
        <div class="form-group">
            <label for="categoria" class="form-label">Categoría *</label>
            <select id="categoria" name="categoria" class="form-select" required>
                <option value="">Seleccione una categoría</option>
                <option value="Incendio">Incendio</option>
                <option value="Desaparición">Desaparición</option>
                <option value="Contaminación">Contaminación</option>
                <option value="Accidente vial">Accidente vial</option>
                <option value="Crimen de odio">Crimen de odio</option>
                <option value="Desastre natural">Desastre natural</option>
                <option value="Otro">Otro</option>
            </select>
        </div>

        <!-- Campo para "Otra categoría" (se muestra solo cuando se selecciona "Otro") -->
        <div id="otra-categoria-group" class="form-group" style="display: none;">
            <label for="otraCategoria" class="form-label">Especifique la categoría *</label>
            <input type="text" id="otraCategoria" name="otraCategoria" class="form-input"
                   placeholder="Ej: Inundación, Derrumbe, etc.">
        </div>

        <!-- Ubicación -->
        <div class="form-section">
            <h3 class="form-section-title">📍 Ubicación</h3>

            <div class="form-row">
                <div class="form-group">
                    <label for="latitud" class="form-label">Latitud *</label>
                    <input type="number" id="latitud" name="ubicacion.latitud" class="form-input" step="any" required
                           placeholder="Ej: -34,6037">
                </div>

                <div class="form-group">
                    <label for="longitud" class="form-label">Longitud *</label>
                    <input type="number" id="longitud" name="ubicacion.longitud" class="form-input" step="any" required
                           placeholder="Ej: -58,3816">
                </div>
            </div>
        </div>

        <!-- Fecha del acontecimiento -->
        <div class="form-group">
            <label for="fechaAcontecimiento" class="form-label">Fecha del acontecimiento *</label>
            <input type="datetime-local" id="fechaAcontecimiento" name="fechaDeAcontecimiento" class="form-input" required>
        </div>

        <!-- Información del contribuyente -->
        <div class="form-section">
            <h3 class="form-section-title">👤 Información del contribuyente (opcional)</h3>

            <div class="form-group">
                <label for="nombreContribuyente" class="form-label">Nombre</label>
                <input type="text" id="nombreContribuyente" name="contribuyente.nombre" class="form-input"
                       placeholder="Su nombre (opcional)">
            </div>

            <div class="form-checkbox-group">
                <input type="checkbox" id="anonimo" name="anonimo" class="form-checkbox">
                <label for="anonimo" class="form-checkbox-label">Enviar de forma anónima</label>
            </div>
        </div>

        <!-- Etiquetas -->
        <div class="form-section">
            <h3 class="form-section-title">🏷️ Etiquetas (opcional)</h3>
            <div class="form-group">
                <label for="etiquetas" class="form-label">Etiquetas (separadas por comas)</label>
                <input type="text" id="etiquetas" name="etiquetasInput" class="form-input"
                       placeholder="Ej: emergencia, rescate, denuncia">
                <small class="form-help">Separe las etiquetas con comas</small>
            </div>
        </div>

        <!-- Contenido multimedia -->
        <div class="form-section">
            <h3 class="form-section-title">📷 Contenido multimedia (opcional)</h3>

            <div class="form-group">
                <label for="multimedia" class="form-label">Archivos multimedia</label>
                <input type="file" id="multimedia" name="archivosMultimedia" class="form-input" multiple
                       accept="image/*,video/*,audio/*">
                <small class="form-help">Formatos aceptados: imágenes, videos, audio</small>
            </div>

            <div id="preview-container" class="preview-container" style="display: none;">
                <h4 class="preview-title">Vista previa:</h4>
                <div id="preview-grid" class="preview-grid"></div>
            </div>
        </div>

        <!-- Botones -->
        <div class="form-actions">
            <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
            <button type="submit" id="btn-enviar" class="btn btn-primary">Enviar Reporte</button>
        </div>

        <!-- Mensajes de estado -->
        <div id="mensaje-exito" class="mensaje mensaje-exito" style="display: none;">
            ✅ Hecho reportado exitosamente. Redirigiendo...
        </div>

        <div id="mensaje-error" class="mensaje mensaje-error" style="display: none;">
            ❌ Error al enviar el reporte. Por favor, intente nuevamente.
        </div>
    </form>
</#assign>

<#include "layout.ftl">