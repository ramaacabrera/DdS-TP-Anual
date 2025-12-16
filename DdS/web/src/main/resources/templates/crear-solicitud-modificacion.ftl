<#assign pageTitle = "Solicitar Modificación">
<#assign content>
<#-- Lógica de fechas (la mantengo igual) -->
    <#assign timestampRaw = hecho.fechaDeAcontecimiento!"0">
    <#assign timestampLimpio = timestampRaw?replace(".", "", "all")>
    <#if timestampLimpio?is_number && timestampLimpio?number gt 0>
        <#assign timestampMilisegundos = timestampLimpio?number>
        <#assign fechaObjeto = timestampMilisegundos?datetime>
    <#else>
        <#assign fechaISO = "">
    </#if>

    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin=""/>
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>

    <style>
        /* Estilos (los mantengo igual) */
        .preview-grid { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 10px; }
        .preview-item { position: relative; width: 100px; height: 100px; border: 1px solid #ddd; border-radius: 4px; overflow: hidden; display: flex; align-items: center; justify-content: center; background: #f9f9f9; }
        .preview-item img, .preview-item video { width: 100%; height: 100%; object-fit: cover; }
        .btn-remove-file { position: absolute; top: 0; right: 0; background: rgba(255,0,0,0.8); color: white; border: none; cursor: pointer; width: 24px; height: 24px; font-weight: bold; z-index: 10; display: flex; align-items: center; justify-content: center; }
        .btn-remove-file:hover { background: red; }
        .badge-nuevo { position: absolute; bottom: 0; left: 0; background: #4CAF50; color: white; font-size: 10px; padding: 2px 4px; width: 100%; text-align: center; }
    </style>

    <script>
        // Variables globales para el JS
        const CURRENT_USERNAME = "${username!}";
        const CURRENT_USER_ID = "${usuarioId!}";
        const HECHO_ID = "${hechoId?html}";
        const URL_PUBLICA = '${urlPublica}';
        const ACCESS_TOKEN = '${accessToken}';
        const CLOUDINARY_URL = '${cloudinaryUrl!"https://api.cloudinary.com/v1_1/demo/auto/upload"}';
        const CLOUDINARY_PRESET = '${cloudinaryPreset!"preset_demo"}';

        const MULTIMEDIA_EXISTENTE = [
            <#if hecho.contenidoMultimedia??>
            <#list hecho.contenidoMultimedia as media>
            {
                contenido: "${media.contenido}",
                tipoContenido: "${media.tipoContenido}"
            }<#if media?has_next>,</#if>
            </#list>
            </#if>
        ];
    </script>

    <script>
        // Lógica segura para coordenadas
        const latStr = '${(hecho.ubicacion.latitud)!-34.6037}';
        const lonStr = '${(hecho.ubicacion.longitud)!-58.3816}';

        // Convertimos comas a puntos y aseguramos que sean números para JS
        const originalLat = parseFloat(String(latStr).replace(',', '.'));
        const originalLon = parseFloat(String(lonStr).replace(',', '.'));

        document.addEventListener('DOMContentLoaded', () => {
            if (typeof initMapaModificacion === 'function') {
                initMapaModificacion({
                    mapContainerId: 'mapa-selector',
                    originalLat: originalLat,
                    originalLon: originalLon
                });
            }
        });
    </script>

    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="javascript:history.back()" class="header-link back-link">&larr; Volver Atrás</a>
    </div>

    <h1 class="main-title"> Modificar Hecho</h1>
    <p style="color: black; margin-bottom: 25px;">
        Está solicitando la modificación del hecho ID: <strong>${hechoId?html}</strong>.
    </p>

    <form id="form-solicitud-modificacion" class="form-container" method="POST">

        <input type="hidden" id="ID_hechoAsociado" name="hechoId" value="${hechoId?html}">
        <#-- Usamos el UUID si existe, sino el username (el backend lo arreglará si hace falta) -->
        <input type="hidden" id="ID_usuario" name="usuarioId" value="${usuarioId!username!}">
        <input type="hidden" name="username" value="${username!}">

        <h3 class="form-section-title">Valores a Modificar</h3>

        <div class="campos-modificacion-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 30px;">

            <div class="campo-original"><h4>Original</h4></div>
            <div class="campo-propuesto"><h4>Valor Propuesto (Editar)</h4></div>
            <hr style="grid-column: 1 / 3;">

            <#-- 1. Título -->
            <div class="form-group campo-original">
                <label class="form-label">Título Original</label>
                <input type="text" value="${hecho.titulo!""}" class="form-input" disabled>
            </div>
            <div class="form-group campo-propuesto">
                <label for="titulo" class="form-label">Nuevo Título</label>
                <input type="text" id="titulo" name="titulo" class="form-input" placeholder="Dejar vacío si no se modifica">
            </div>

            <#-- 2. Descripción -->
            <div class="form-group campo-original">
                <label class="form-label">Descripción Original</label>
                <textarea class="form-textarea" rows="4" disabled>${hecho.descripcion!""}</textarea>
            </div>
            <div class="form-group campo-propuesto">
                <label for="descripcion" class="form-label">Nueva Descripción</label>
                <textarea id="descripcion" name="descripcion" class="form-textarea" rows="4" placeholder="Dejar vacío si no se modifica"></textarea>
            </div>

            <#-- 3. Categoría -->
            <div class="form-group campo-original">
                <label class="form-label">Categoría Original</label>
                <input type="text" value="${hecho.categoria!""}" class="form-input" disabled>
            </div>
            <div class="form-group campo-propuesto">
                <label for="categoria" class="form-label">Nueva Categoría</label>
                <select id="categoria" name="categoria" class="form-select" required>
                    <option value="">Seleccione una categoría</option>
                    <#list categorias as cat>
                        <option value='${cat}'>${cat}</option>
                    </#list>
                </select>
            </div>

            <#-- 4. Fecha -->
            <div class="form-group campo-original">
                <label class="form-label">Fecha Original</label>
                <input type="text" value="${(hecho.fechaDeAcontecimiento?number_to_datetime?string('dd/MM/yyyy HH:mm'))!'Sin fecha'}" class="form-input" disabled>
            </div>
            <div class="form-group campo-propuesto">
                <label for="fechaDeAcontecimiento" class="form-label">Nueva Fecha/Hora</label>
                <input type="datetime-local" id="fechaDeAcontecimiento" name="fechaDeAcontecimiento" class="form-input">
            </div>
        </div>

        <#-- 5. MULTIMEDIA -->
        <h3 class="form-section-title">📷 Contenido multimedia</h3>
        <div class="form-group">
            <label for="multimedia" class="form-label">Gestión de Archivos</label>
            <p class="form-help">Aquí puede ver los archivos actuales, eliminar los que desee y agregar nuevos.</p>

            <input type="file" id="multimedia" class="form-input" multiple accept="image/*,video/*,audio/*">

            <div id="preview-container" class="preview-container" style="margin-top: 15px;">
                <h4 class="preview-title" style="font-size: 0.9em; margin-bottom: 10px;">Archivos en el hecho:</h4>
                <div id="preview-grid" class="preview-grid">
                </div>
            </div>
        </div>

        <h3 class="form-section-title" style="margin-top: 30px;">📍 Ubicación</h3>
        <p class="form-help">Haga clic en el mapa para proponer una nueva ubicación.</p>

        <div id="mapa-selector" style="height: 300px; width: 100%; border-radius: 8px; border: 1px solid var(--border-color); margin-bottom: 15px; z-index: 1;"></div>

        <input type="hidden" id="latitud" name="ubicacion.latitud">
        <input type="hidden" id="longitud" name="ubicacion.longitud">
        <p id="coordenadas-seleccionadas" style="color: black; font-size: 0.9em;">Coordenadas propuestas: Ninguna</p>

        <h3 class="form-section-title" style="margin-top: 30px;">📝 Justificación</h3>
        <div class="form-group">
            <label for="justificacion" class="form-label">Motivo de la modificación *</label>
            <textarea id="justificacion" name="justificacion" class="form-textarea" rows="3" required placeholder="Explique brevemente por qué realiza estos cambios..."></textarea>
        </div>

        <div class="form-actions">
            <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
            <button type="submit" id="btn-enviar" class="btn btn-primary">Confirmar Modificación</button>
        </div>

        <div id="mensaje-exito" class="mensaje mensaje-exito" style="display: none;">✅ Modificación enviada exitosamente.</div>
        <div id="mensaje-error" class="mensaje mensaje-error" style="display: none;">❌ Error al enviar la modificación.</div>
    </form>
</#assign>

<#include "layout.ftl">
<script src="/js/solicitud-modificacion.js" defer></script>