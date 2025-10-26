<#assign pageTitle = "Solicitar Eliminación">
<#assign content>
    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="javascript:history.back()" class="header-link back-link">&larr; Volver Atrás</a>
    </div>

    <h1 class="main-title">Solicitar Eliminación de Hecho</h1>

    <p style="color: var(--muted-color); margin-bottom: 25px;">
        Está solicitando la eliminación del hecho ID: <strong>${hechoId?html}</strong>
    </p>

    <form id="form-solicitud-eliminacion" class="form-container" method="POST" action="/api/solicitudEliminacion">

        <!-- ID del Hecho (oculto) -->
        <input type="hidden" id="ID_hechoAsociado" name="ID_hechoAsociado" value="${hechoId?html}">

        <!-- Justificación -->
        <div class="form-group">
            <label for="justificacion" class="form-label">Justificación *</label>
            <textarea id="justificacion" name="justificacion" class="form-textarea" rows="6" required
                      placeholder="Explique detalladamente por qué solicita la eliminación de este hecho (mínimo 500 caracteres)..."
                      minlength="500"></textarea>
            <small class="form-help">Mínimo 500 caracteres. Sea específico y claro en su justificación.</small>
            <div id="contador-caracteres" class="contador-caracteres" style="margin-top: 8px; font-size: 0.85rem; color: var(--muted-color);">
                0 caracteres
            </div>
        </div>

        <!-- Información del solicitante -->
        <div class="form-section">
            <h3 class="form-section-title">👤 Información del solicitante (opcional)</h3>

            <div class="form-group">
                <label for="nombreUsuario" class="form-label">Nombre</label>
                <input type="text" id="nombreUsuario" name="usuario.nombre" class="form-input"
                       placeholder="Su nombre (opcional)">
            </div>

            <div class="form-checkbox-group">
                <input type="checkbox" id="anonimo" name="anonimo" class="form-checkbox">
                <label for="anonimo" class="form-checkbox-label">Enviar de forma anónima</label>
            </div>
        </div>

        <!-- Botones -->
        <div class="form-actions">
            <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
            <button type="submit" id="btn-enviar" class="btn btn-danger">Enviar Solicitud</button>
        </div>

        <!-- Mensajes de estado -->
        <div id="mensaje-exito" class="mensaje mensaje-exito" style="display: none;">
            ✅ Solicitud de eliminación enviada exitosamente. Redirigiendo...
        </div>

        <div id="mensaje-error" class="mensaje mensaje-error" style="display: none;">
            ❌ Error al enviar la solicitud. Por favor, intente nuevamente.
        </div>
    </form>
</#assign>

<#include "layout.ftl">

<script src="/js/solicitud-eliminacion.js" defer></script>