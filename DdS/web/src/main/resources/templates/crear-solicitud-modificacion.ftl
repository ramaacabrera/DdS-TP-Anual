<#assign pageTitle = "Solicitar Modificación">
<#assign content>
    <#assign timestampRaw = hecho.fechaDeAcontecimiento!"0">

<#-- PASO 1: Limpieza de la cadena (Eliminar todos los puntos) -->
    <#assign timestampLimpio = timestampRaw?replace(".", "", "all")>

<#-- PASO 2: Verificar si la cadena limpia es un número válido y es mayor que cero -->
    <#if timestampLimpio?is_number && timestampLimpio?number gt 0>

    <#-- El valor debe ser numérico (Long) para ?datetime. -->
    <#-- ?number: Convierte la cadena limpia a un número (Long). -->
        <#assign timestampMilisegundos = timestampLimpio?number>

    <#-- PASO 3: Convertir el número (milisegundos) a objeto de fecha de Freemarker -->
        <#assign fechaObjeto = timestampMilisegundos?datetime>

    <#-- PASO 4: Formatear para el input HTML y para la visualización -->
        <#assign fechaISO = fechaObjeto?string["yyyy-MM-dd'T'HH:mm"]>
        <#assign fechaLegible = fechaObjeto?string["dd/MM/yyyy HH:mm"]>

    <#else>
    <#-- Fallback si no hay datos válidos -->
        <#assign fechaISO = "">
        <#assign fechaLegible = "N/A">
    </#if>

    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin=""/>
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>
    <script src="/js/solicitud-modificacion.js" ></script>
    <script>

        const originalLatStr = '${hecho.ubicacion.latitud!"-34.6037"}';
        const originalLonStr = '${hecho.ubicacion.longitud!"-58.3816"}';

        // 2. Formatear: Reemplazar la coma por el punto decimal para compatibilidad con Leaflet/JS
        const originalLat = originalLatStr.replace(',', '.');
        const originalLon = originalLonStr.replace(',', '.');

        document.addEventListener('DOMContentLoaded', () => {
            // La comprobación 'typeof initMapaModificacion' es correcta y segura.
            if (typeof initMapaModificacion === 'function') {
                initMapaModificacion({
                    mapContainerId: 'mapa-selector',
                    // Pasamos las variables JavaScript formateadas
                    originalLat: originalLat,
                    originalLon: originalLon
                });
            }
        });
    </script>



    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="javascript:history.back()" class="header-link back-link">&larr; Volver Atrás</a>
    </div>

    <h1 class="main-title">✍️ Solicitar Modificación de Hecho</h1>

    <p style="color: black; margin-bottom: 25px;">
        Está solicitando la modificación del hecho ID: <strong>${hechoId?html}</strong>. Por favor, ingrese solo los nuevos valores que desea cambiar.
    </p>

<#-- Variables JS necesarias -->
    <script>
        const CURRENT_USER_ID = "${usuarioId!}";
        const HECHO_ID = "${hechoId?html}";
    </script>


    <form id="form-solicitud-modificacion" class="form-container" method="POST">

        <input type="hidden" id="ID_hechoAsociado" name="hechoId" value="${hechoId?html}">
        <input type="hidden" id="ID_usuario" name="usuarioId" value="${usuarioId!}">

        <h3 class="form-section-title">Valores a Modificar</h3>

        <div class="campos-modificacion-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 30px;">

            <div class="campo-original">
                <h4>Original</h4>
            </div>
            <div class="campo-propuesto">
                <h4>Valor Propuesto (Editar)</h4>
            </div>

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
                <select id="categoria" name="categoria" class="form-select">
                    <option value="">Seleccione o deje vacío</option>
                    <option value="Incendio">Incendio</option>
                    <option value="Desaparición">Desaparición</option>
                    <option value="Contaminación">Contaminación</option>
                    <option value="Otro">Otro</option>
                </select>
            </div>

            <#-- 4. Fecha de Acontecimiento -->
            <div class="form-group campo-original">
                <label class="form-label">Fecha Original</label>
                <input type="text" value="${fechaLegible}" class="form-input" disabled>
            </div>
            <div class="form-group campo-propuesto">
                <label for="fechaDeAcontecimiento" class="form-label">Nueva Fecha/Hora</label>
                <input type="datetime-local"
                       id="fechaDeAcontecimiento"
                       name="fechaDeAcontecimiento"
                       class="form-input"
                       value=""> </div>

        </div>

        <h3 class="form-section-title">📍 Ubicación</h3>
    <p class="form-help">Haga clic en el mapa para proponer una nueva ubicación. La ubicación original está marcada.</p>

        <div id="mapa-selector" style="height: 300px; width: 100%; border-radius: 8px; border: 1px solid var(--border-color); margin-bottom: 15px; z-index: 1;"></div>

        <input type="hidden" id="latitud" name="ubicacion.latitud">
        <input type="hidden" id="longitud" name="ubicacion.longitud">

        <p id="coordenadas-seleccionadas" style="color: black; font-size: 0.9em;">
            Coordenadas propuestas: Ninguna
        </p>


        <h3 class="form-section-title" style="margin-top: 20px;">Justificación de los Cambios *</h3>

        <div class="form-group">
            <label for="justificacion" class="form-label">Justificación</label>
            <textarea id="justificacion" name="justificacion" class="form-textarea" rows="6" required
                      placeholder="Explique detalladamente por qué solicita la modificación y el propósito de los cambios..."
                      maxlength="500"></textarea>
            <small class="form-help">La justificación es obligatoria.</small>
        </div>

        <#-- Sección de anonimato -->

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
            <button type="submit" id="btn-enviar" class="btn btn-primary">Enviar Solicitud de Modificación</button>
        </div>

        <div id="mensaje-exito" class="mensaje mensaje-exito" style="display: none;">
            ✅ Solicitud de modificación enviada exitosamente.
        </div>

        <div id="mensaje-error" class="mensaje mensaje-error" style="display: none;">
            ❌ Error al enviar la solicitud. Por favor, intente nuevamente.
        </div>
    </form>
</#assign>

<#include "layout.ftl">
<script src="/js/solicitud-modificacion.js" defer></script>
