<#assign pageTitle = (hecho.titulo)!"Hecho Detallado">
<#assign additionalCss = ["/css/styleHechoEspecifico.css"]>
<#assign content>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossorigin=""/>
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" crossorigin=""></script>
    <link rel="icon" href="data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>🗺️</text></svg>">

    <#import "fragments/components.ftl" as cmp>

    <style>
        .tabs { font-family: 'Segoe UI', system-ui, sans-serif; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); overflow: hidden; margin-top: 20px; }
        .tabs-nav { display: flex; border-bottom: 1px solid #e5e7eb; background: #f9fafb; }
        .tab-btn { padding: 1rem 1.5rem; border: none; background: none; cursor: pointer; font-weight: 600; color: #6b7280; border-bottom: 2px solid transparent; transition: all 0.3s ease; }
        .tab-btn:hover { color: #2563eb; background: #eff6ff; }
        .tab-btn.active { color: #2563eb; border-bottom-color: #2563eb; background: #fff; }
        .tab-content { display: none; padding: 30px; animation: fadeIn 0.3s ease; }
        .tab-content.active { display: block; }

        .media-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
        .media-card { background: white; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden; transition: transform 0.2s, box-shadow 0.2s; display: flex; flex-direction: column; }
        .media-card:hover { transform: translateY(-4px); box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1); }
        .media-img-wrapper { height: 200px; overflow: hidden; }
        .media-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }
        .media-card:hover .media-img { transform: scale(1.05); }
        .media-video-wrapper { background: #000; aspect-ratio: 16/9; display: flex; align-items: center; justify-content: center; }
        .media-audio-wrapper { padding: 1.5rem; background: #f8fafc; display: flex; flex-direction: column; align-items: center; border-bottom: 1px solid #eee; }
        .media-file-wrapper { padding: 2rem; text-align: center; background: #f3f4f6; flex-grow: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; }
        .btn-download { margin-top: 10px; padding: 8px 16px; background: #2563eb; color: white; text-decoration: none; border-radius: 6px; font-size: 0.9rem; }
        .btn-download:hover { background: #1d4ed8; }

        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
    </style>

    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="/" class="header-link back-link">&larr; Volver a la Lista</a>
    </div>

    <@cmp.hero hecho=hecho />

    <div class="title-actions-container" style="margin-top:20px;">
        <div class="title-content">
            <h1 class="main-title">${hecho.titulo?default("Hecho sin Título")?html}</h1>
            <span class="tag tag-title">${hecho.categoria?default("Sin Categoría")?html}</span>
        </div>
        <@cmp.actionButtons hecho=hecho/>
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
        <div class="tabs-nav">
            <button class="tab-btn active" onclick="showTab('description-content', this)" data-tab="description">Descripción</button>
            <button class="tab-btn" onclick="showTab('multimedia-content', this)" data-tab="multimedia">Multimedia</button>
        </div>

        <div id="description-content" class="tab-content active">
            <div class="description-text">
                <#if hecho.descripcion?has_content>
                    <p>${hecho.descripcion}</p>
                <#else>
                    <em style="color: #999;">No hay descripción detallada disponible.</em>
                </#if>
            </div>

            <hr class="separator" style="border: 0; border-top: 1px solid #e5e7eb; margin: 25px 0;">

            <div class="tags-section">
                <p class="card-subtitle" style="color: #64748b; font-size: 0.85rem; font-weight: 700; letter-spacing: 0.05em; margin-bottom: 12px; text-transform: uppercase;">
                    🏷️ Etiquetas
                </p>
                <@cmp.tagsList etiquetas=hecho.etiquetas />
            </div>
        </div>

        <div id="multimedia-content" class="tab-content">
            <@cmp.mediaGrid hecho=hecho />
        </div>
    </div>

    <script>
        function showTab(tabId, btnElement) {
            const contents = document.querySelectorAll('.tab-content');
            contents.forEach(div => div.classList.remove('active'));

            const buttons = document.querySelectorAll('.tabs-nav .tab-btn');
            buttons.forEach(btn => btn.classList.remove('active'));

            document.getElementById(tabId).classList.add('active');
            btnElement.classList.add('active');
        }

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