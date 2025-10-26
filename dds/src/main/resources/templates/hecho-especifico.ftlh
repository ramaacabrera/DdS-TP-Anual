<!DOCTYPE html>

<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${hecho.titulo?default("Hecho Detallado")}</title>

    <link rel="stylesheet" href="/public/css/style.css">

    <script>
        function showTab(tabId, button) {
            const contents = document.querySelectorAll('.tab-content');
            const buttons = document.querySelectorAll('.tabs-nav button');

            contents.forEach(content => content.style.display = 'none');
            buttons.forEach(btn => btn.classList.remove('active'));

            document.getElementById(tabId).style.display = 'block';
            button.classList.add('active');
        }

        document.addEventListener('DOMContentLoaded', () => {
            // Inicia con la pestaña Descripción activa
            const descButton = document.querySelector('[data-tab="description"]');
            if(descButton) showTab('description-content', descButton);
        });
    </script>

</head>
<body>

<div class="container">
    <div class="header">
        <a href="/" class="header-link">&larr; Volver a la Lista</a>
    </div>

    <!-- Sección de Imagen Principal -->
    <div class="hero">
        <#-- Usamos el primer elemento multimedia como principal si existe -->
        <#if hecho.getContenidoMultimedia()?? && (hecho.getContenidoMultimedia()?size > 0)>
            <#assign primerMedia = hecho.getContenidoMultimedia()[0]>

        <#-- Si es IMAGEN y tiene contenido, se renderiza la imagen -->
            <#if (primerMedia.getTipoContenido()?upper_case == "IMAGEN") && primerMedia.contenido?? && primerMedia.contenido != "">
                <img src="${primerMedia.contenido}" alt="Imagen principal del hecho">
            <#else>
                <!-- Si no es imagen o el enlace está roto, usamos el fallback IMG -->
                <img src="/public/img/noimg-default.png" alt="Imagen por defecto">
            </#if>
        <#else>
            <!-- Si no hay contenido multimedia, usamos el fallback IMG -->
            <img src="/public/img/noimg-default.png" alt="Imagen por defecto">
        </#if>
    </div>

    <!-- Título y Acciones -->
    <div class="title-actions-container">
        <div class="title-content">
            <h1 class="main-title">${hecho.titulo?default("Hecho sin Título")}</h1>
            <!-- La clase tag-title en style.css define el fondo azul y el color blanco -->
            <span class="tag tag-title">${hecho.categoria?default("Sin Categoría")}</span>
        </div>
        <div class="action-buttons-group">
            <button class="action-button"  title="Compartir">🔗</button>
            <button class="action-button" title="Solicitar Modificación">✏️</button><!--//chau?-->
            <button class="action-button" title="Reportar/Eliminar">🚩</button>
        </div>
    </div>

    <hr class="separator" style="border: 1px solid #eee; margin-bottom: 20px;">

    <!-- Metadata Grid -->
    <div class="grid-metadata">
        <div class="card">
            <p class="card-subtitle">📅 FECHA DE ACONTECIMIENTO</p>
            <p>
                <#if hecho.fechaDeAcontecimiento?has_content>
                    ${hecho.fechaDeAcontecimiento?date?string("dd/MM/yyyy HH:mm")}
                <#else>
                    Desconocida
                </#if>
            </p>
        </div>

        <div class="card">
            <p class="card-subtitle">📍 UBICACIÓN</p>
            <p>
                <#if hecho.ubicacion??>
                    Lat: ${hecho.ubicacion.latitud?string("#.#####")}, Lon: ${hecho.ubicacion.longitud?string("#.#####")}
                    <br>
                    (${hecho.ubicacion.descripcion?default("Coordenadas")})
                <#else>
                    No especificada
                </#if>
            </p>
        </div>

        <div class="card">
            <p class="card-subtitle">👤 CONTRIBUYENTE</p>
            <p>
                <#if hecho.contribuyente??>
                    ${hecho.contribuyente.nombre?default("Anónimo")}
                <#else>
                    Fuente Externa
                </#if>
            </p>
        </div>

        <div class="card">
            <p class="card-subtitle">🔗 FUENTE</p>
            <p>
                <#if hecho.fuente??>
                    ${hecho.fuente.tipoDeFuente?default("Metamapa Local")}
                <#else>
                    Fuente Desconocida
                </#if>
            </p>
        </div>
    </div>

    <hr class="separator" style="border: 1px solid #eee; margin-bottom: 20px;">

    <!-- TABS -->
    <div class="tabs">
        <h2>Contenido</h2>

        <!-- Pestañas de Navegación (Simulación JS) -->
        <div class="tabs-nav">
            <button class="action-button active" onclick="showTab('description-content', this)" data-tab="description">Descripción</button>
            <button class="action-button" onclick="showTab('multimedia-content', this)" data-tab="multimedia">Multimedia</button>
        </div>

        <!-- Contenido de la Pestaña Descripción -->
        <div id="description-content" class="tab-content" style="display: block;">
            <p class="description-text">${hecho.descripcion?default("No hay descripción detallada.")}</p>

            <hr class="separator" style="border: 1px dashed #ccc; margin: 20px 0;">

            <p class="card-subtitle" style="color: #666; margin-bottom: 10px;">🏷️ ETIQUETAS</p>
            <div class="tag-list">
                <#list hecho.etiquetas as etiqueta>
                    <span class="tag">${etiqueta.nombre}</span>
                </#list>
                <#if hecho.etiquetas?size == 0><p class="card-subtitle" style="color:#999;">Sin etiquetas.</p></#if>
            </div>
        </div>

        <!-- Contenido de la Pestaña Multimedia -->
        <div id="multimedia-content" class="tab-content" style="display: none;">
            <div class="media-grid">
                <#list hecho.getContenidoMultimedia() as media>
                    <div class="media-item">
                        <#if media.getTipoContenido()?upper_case == "IMAGEN">
                            <img src="${media.contenido}" alt="Media">
                        <#else>
                            <p style="font-size: 12px; color: #666;">${media.getTipoContenido()}: ${media.contenido?substring(0, 20)}...</p>
                        </#if>
                    </div>
                </#list>
                <#if hecho.getContenidoMultimedia()?size == 0><p class="card-subtitle" style="color:#999; grid-column: 1 / -1;">No hay contenido multimedia adjunto.</p></#if>
            </div>
        </div>
    </div>

</div>
</body>
</html>