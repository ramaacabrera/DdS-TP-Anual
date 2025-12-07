<#assign pageTitle = "Solicitar Eliminación">
<#assign content>
    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="javascript:history.back()" class="header-link back-link">&larr; Volver Atrás</a>
    </div>

    <h1 class="main-title">Solicitar Eliminación de Hecho</h1>

    <p style="color: var(--muted-color); margin-bottom: 25px;">
        Está solicitando la eliminación del hecho ID: <strong>${hechoId?html}</strong>
    </p>

    <script>
        const URL_PUBLICA = '${urlPublica!"http://localhost:8087/api"}';
        // Variable global para saber quién está logueado en el JS
        const CURRENT_USER = "${username!}";
    </script>

    <form id="form-solicitud-eliminacion" class="form-container" method="POST">

        <input type="hidden" id="ID_hechoAsociado" name="ID_hechoAsociado" value="${hechoId?html}">

        <div class="form-group">
            <label for="justificacion" class="form-label">Justificación *</label>
            <textarea id="justificacion" name="justificacion" class="form-textarea" rows="6" required
                      placeholder="Explique detalladamente por qué solicita la eliminación de este hecho (máximo 500 caracteres)..."
                      maxlength="500"></textarea>
            <small class="form-help">Máximo 500 caracteres. Sea específico y claro en su justificación.</small>
            <div id="contador-caracteres" class="contador-caracteres" style="margin-top: 8px; font-size: 0.85rem; color: var(--muted-color);">
                0 / 500 caracteres
            </div>
        </div>

        <div class="form-section">
            <h3 class="form-section-title">👤 Información del solicitante</h3>

            <#if accessToken??>
                <div style="background-color: #e8f5e9; padding: 15px; border-radius: 8px; border: 1px solid #c8e6c9; margin-bottom: 15px;">
                    <p style="margin: 0; color: #2e7d32; font-weight: 500;">
                        <span style="font-size: 1.2em;">👋</span> Solicitando como <strong>${username}</strong>
                    </p>
                    <p style="margin: 5px 0 0 0; font-size: 0.9em; color: #1b5e20;">
                        Esta solicitud quedará asociada a tu cuenta.
                    </p>
                </div>

                <div class="form-checkbox-group">
                    <input type="checkbox" id="anonimo" name="anonimo" class="form-checkbox">
                    <label for="anonimo" class="form-checkbox-label">Enviar solicitud de forma <strong>anónima</strong></label>
                </div>

            <#else>
                <div style="background-color: #fff3e0; padding: 15px; border-radius: 8px; border: 1px solid #ffe0b2; margin-bottom: 15px;">
                    <p style="margin: 0; color: #e65100; font-weight: 500;">
                        <span style="font-size: 1.2em;">🕵️</span> Solicitando de forma <strong>anónima</strong>
                    </p>
                    <p style="margin: 5px 0 0 0; font-size: 0.9em; color: #ef6c00;">
                        Si deseas hacer un seguimiento de tu solicitud, te recomendamos
                        <a href="/login" style="color: #e65100; text-decoration: underline; font-weight: bold;">iniciar sesión</a>.
                    </p>
                </div>
            </#if>
        </div>

        <div class="form-actions">
            <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
            <button type="submit" id="btn-enviar" class="btn btn-primary">Enviar Solicitud</button>
        </div>

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