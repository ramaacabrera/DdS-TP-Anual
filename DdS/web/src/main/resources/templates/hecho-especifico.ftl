<#assign pageTitle = (hecho.titulo)!"Hecho Detallado">
<#assign content>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossorigin=""/>
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" crossorigin=""></script>

    <link rel="icon" href="data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>🗺️</text></svg>">

    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="/" class="header-link back-link">&larr; Volver a la Lista</a>
    </div>

    <#import "fragments/components.ftl" as cmp>

    <@cmp.hero hecho=hecho />

    <div class="title-actions-container" style="margin-top:20px;">
        <div class="title-content">
            <h1 class="main-title">${hecho.titulo?default("Hecho sin Título")?html}</h1>
            <span class="tag tag-title">${hecho.categoria?default("Sin Categoría")?html}</span>
        </div>
         <@cmp.actionButtons hecho=hecho
                 <a href="/hechos/${hecho.id}/editar" class="btn">✏Editar</a>
                 />
    </div>

    <hr class="separator" style="border: 1px solid #eee; margin-bottom: 20px;">

    <div class="grid-metadata">
        <div class="card">
            <p class="card-subtitle">📅 FECHA DE ACONTECIMIENTO</p>
            <p>
                <#if hecho.fechaDeAcontecimiento?has_content>
                    ${hecho.fechaDeAcontecimiento?string("dd/MM/yyyy HH:mm")}
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
                <#else>
                    No especificada
                </#if>
            </p>
        </div>

        <div class="card">
            <p class="card-subtitle">👤 CONTRIBUYENTE</p>
            <p>
                <#if hecho.contribuyente??>
                    ${hecho.contribuyente.nombre?default("Anónimo")?html}
                <#else>
                    Fuente Externa
                </#if>
            </p>
        </div>

        <div class="card">
            <p class="card-subtitle">🔗 FUENTE</p>
            <p>
                <#if hecho.fuente??>
                    ${hecho.fuente.tipoFuente?default("Metamapa Local")?html}
                <#else>
                    Fuente Desconocida
                </#if>
            </p>
        </div>
    </div>

    <div class="card" style="grid-column: 1 / -1; margin-top: 30px;">
        <p class="card-subtitle">🗺 MAPA DE UBICACIÓN</p>
        <div id="map" style="height: 350px; border-radius: 4px; margin-top: 10px;"></div>
    </div>

    <hr class="separator" style="border: 1px solid #eee; margin-bottom: 20px;">

    <div class="tabs">
        <h2>Contenido</h2>
        <div class="tabs-nav">
            <button class="tab-btn active" onclick="showTab('description-content', this)" data-tab="description">Descripción</button>
            <button class="tab-btn" onclick="showTab('multimedia-content', this)" data-tab="multimedia">Multimedia</button>
        </div>

        <div id="description-content" class="tab-content active">
            <p class="description-text">${hecho.descripcion?default("No hay descripción detallada.")?html}</p>
            <hr class="separator" style="border: 1px dashed #ccc; margin: 20px 0;">
            <p class="card-subtitle" style="color: #666; margin-bottom: 10px;">🏷️ ETIQUETAS</p>
            <@cmp.tagsList etiquetas=hecho.etiquetas />
        </div>

        <div id="multimedia-content" class="tab-content" style="display:none;">
            <@cmp.mediaGrid hecho=hecho />
        </div>
    </div>

    <script>
        window.addEventListener('load', function() {
            <#if hecho.ubicacion?? && hecho.ubicacion.latitud?? && hecho.ubicacion.longitud??>

            const lat = ${hecho.ubicacion.latitud?c};
            const lon = ${hecho.ubicacion.longitud?c};

            if (typeof L !== 'undefined') {
                try {
                    const map = L.map('map').setView([lat, lon], 13);

                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '&copy; OpenStreetMap contributors'
                    }).addTo(map);

                    L.marker([lat, lon])
                        .addTo(map)
                        .bindPopup('${hecho.titulo?js_string}')
                        .openPopup();
                } catch (error) {
                    console.error('Error mapa:', error);
                }
            } else {
                document.getElementById('map').innerHTML = '<p style="text-align:center; padding-top:100px;">Librería de mapas no cargada.</p>';
            }
            <#else>
            document.getElementById('map').innerHTML = '<p style="text-align:center; color:#999; padding-top:100px;">Ubicación no disponible.</p>';
            </#if>
        });
    </script>

</#assign>

<#include "layout.ftl">