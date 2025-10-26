
<#assign pageTitle = hecho.titulo?default("Hecho Detallado")>
<#assign content>
    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="/" class="header-link back-link">&larr; Volver a la Lista</a>
    </div>

<#-- Importo macros -->
    <#import "fragments/components.ftl" as cmp>

    <@cmp.hero hecho=hecho />

    <!-- Título y acciones -->
    <div class="title-actions-container" style="margin-top:20px;">
        <div class="title-content">
            <h1 class="main-title">${hecho.titulo?default("Hecho sin Título")?html}</h1>
            <span class="tag tag-title">${hecho.categoria?default("Sin Categoría")?html}</span>
        </div>
        <@cmp.actionButtons hecho=hecho />
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
                    (${hecho.ubicacion.descripcion?default("Coordenadas")?html})
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
                    ${hecho.fuente.tipoDeFuente?default("Metamapa Local")?html}
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
</#assign>

<#include "layout.ftl">