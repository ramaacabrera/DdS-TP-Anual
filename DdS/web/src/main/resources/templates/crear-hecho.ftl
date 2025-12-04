<script src="/js/crear-hecho.js" defer></script>
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin=""/>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>

<#assign pageTitle = "Reportar un Hecho">
<#assign content>
    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="/" class="header-link back-link">&larr; Volver al Inicio</a>
    </div>

    <h1 class="main-title">Reportar un Hecho</h1>

    <script>
        const URL_PUBLICA = '${urlPublica}';
        const CURRENT_USER = "${username!}";
        const CLOUDINARY_URL = '${cloudinaryUrl}';
        const CLOUDINARY_PRESET = '${cloudinaryPreset}';
    </script>

    <form id="form-crear-hecho" class="form-container" method="POST" action="/api/hechos" enctype="multipart/form-data">

        <!-- Información del contribuyente -->
        <div class="form-section">
            <h3 class="form-section-title">👤 Información del contribuyente</h3>

            <#if access_token??>
                <div style="background-color: #e8f5e9; padding: 15px; border-radius: 8px; border: 1px solid #c8e6c9; margin-bottom: 15px;">
                    <p style="margin: 0; color: #2e7d32; font-weight: 500;">
                        <span style="font-size: 1.2em;">👋</span> Estás reportando como <strong>${username}</strong>
                    </p>
                    <p style="margin: 5px 0 0 0; font-size: 0.9em; color: #1b5e20;">
                        Este hecho quedará registrado a tu nombre.
                    </p>
                </div>

                <div class="form-checkbox-group">
                    <input type="checkbox" id="anonimo" name="anonimo" class="form-checkbox">
                    <label for="anonimo" class="form-checkbox-label">Prefiero reportar de forma <strong>anónima</strong></label>
                </div>

            <#else>
                <div style="background-color: #fff3e0; padding: 15px; border-radius: 8px; border: 1px solid #ffe0b2; margin-bottom: 15px;">
                    <p style="margin: 0; color: #e65100; font-weight: 500;">
                        <span style="font-size: 1.2em;">🕵️</span> Estás reportando de forma <strong>anónima</strong>
                    </p>
                    <p style="margin: 5px 0 0 0; font-size: 0.9em; color: #ef6c00;">
                        Si deseas que este reporte se asocie a tu perfil debes
                        <a href="/login" style="color: #e65100; text-decoration: underline; font-weight: bold;">iniciar sesión aquí</a>.
                    </p>
                </div>
            </#if>
        </div>

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

            <p class="form-help" style="margin-bottom: 10px;">
                Haga clic en el mapa para marcar la ubicación exacta.
            </p>

            <div id="mapa-selector" style="height: 300px; width: 100%; border-radius: 8px; border: 1px solid var(--border-color); margin-bottom: 15px; z-index: 1;"></div>

            <!--<div class="form-group">
                <label for="descripcionUbicacion" class="form-label">Descripción de la ubicación *</label>
                <input type="text" id="descripcionUbicacion" name="ubicacion.descripcion" class="form-input" required
                       placeholder="Ej:Avenida Sarmiento 1233, Barracas. Frente a la estación de tren, casa de rejas blancas...">
            </div>-->

            <input type="hidden" id="latitud" name="ubicacion.latitud" required>
            <input type="hidden" id="longitud" name="ubicacion.longitud" required>
        </div>

        <!-- Fecha del acontecimiento -->
        <div class="form-group">
            <label for="fechaAcontecimiento" class="form-label">Fecha del acontecimiento *</label>
            <input type="datetime-local" id="fechaAcontecimiento" name="fechaDeAcontecimiento" class="form-input" required>
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