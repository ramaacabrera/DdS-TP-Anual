<script src="/js/coleccion.js" defer></script>

<#assign pageTitle = (coleccion.titulo)!?default("Colección Detallada")>
<#assign content>
    <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
        <a href="/colecciones" class="header-link back-link">&larr; Volver a colecciones</a>
    </div>

<#-- HERO / ENCABEZADO -->
    <div class="title-actions-container" style="margin-top:20px;" data-coleccion-id="${(coleccion.id)!}">
        <div class="title-content">
            <h1 class="main-title">${(coleccion.titulo)!?default("Colección sin título")?html}</h1>
            <span class="tag tag-title">${(coleccion.algoritmoDeConsenso)!?default("Sin algoritmo")?html}</span>
        </div>
        <div class="actions" style="margin-top:10px;">
            <#if coleccion?? && coleccion.handle??>
                <button type="button" class="btn btn-secondary" onclick="editarColeccion('${coleccion.handle}')">✏️ Editar</button>
                <button type="button" class="btn btn-primary" onclick="verEstadisticas('${coleccion.handle}')">📊 Ver estadísticas</button>
            <#else>
                <button type="button" class="btn btn-secondary" disabled>✏️ Editar</button>
                <button type="button" class="btn btn-primary" disabled>📊 Ver estadísticas</button>
            </#if>
        </div>
    </div>

    <hr class="separator" style="border: 1px solid #eee; margin-bottom: 25px;">

<#-- DESCRIPCIÓN -->
    <div class="card" style="margin-bottom: 25px;">
        <p class="card-subtitle">📘 Descripción</p>
        <p>${(coleccion.descripcion)!?default("Sin descripción disponible.")?html}</p>
    </div>

<#-- FUENTES -->
    <div class="card" style="margin-bottom: 25px;">
        <p class="card-subtitle">🔗 Fuentes asociadas</p>
        <#if coleccion?? && coleccion.fuentes?? && (coleccion.fuentes?size > 0)>
            <ul class="list-group">
                <#list coleccion.fuentes as fuente>
                    <li class="list-item">${(fuente.tipoDeFuente)!?default("Tipo desconocido")?capitalize}</li>
                </#list>
            </ul>
        <#else>
            <p>No hay fuentes asociadas.</p>
        </#if>
    </div>

<#-- CRITERIOS -->
    <div class="card" style="margin-bottom: 25px;">
        <p class="card-subtitle">🧩 Criterios de pertenencia</p>
        <#if coleccion?? && coleccion.criteriosDePertenencia?? && (coleccion.criteriosDePertenencia?size > 0)>
            <ul class="list-group">
                <#list coleccion.criteriosDePertenencia as c>
                    <li class="list-item">${(c.tipo)!?default("Tipo desconocido")?capitalize}</li>
                </#list>
            </ul>
        <#else>
            <p>No hay criterios definidos.</p>
        </#if>
    </div>

    <hr class="separator" style="border: 1px solid #eee; margin: 30px 0;">

<#-- HECHOS ASOCIADOS -->
    <div class="form-section">
        <h3 class="form-section-title">📂 Hechos asociados</h3>
        <#if coleccion?? && coleccion.hechos?? && (coleccion.hechos?size > 0)>
            <div class="grid-metadata">
                <#list coleccion.hechos as hecho>
                    <div class="card" style="cursor:pointer;" onclick="verHecho('${(hecho.id)!}')">
                        <p class="card-subtitle">${(hecho.categoria)!?default("Sin categoría")?html}</p>
                        <h4>${(hecho.titulo)!?default("Hecho sin título")?html}</h4>
                        <p style="color:#666; font-size:0.9em;">
                            ${(hecho.descripcion)!?truncate(100, "...")?html}
                        </p>
                    </div>
                </#list>
            </div>
        <#else>
            <p class="texto-placeholder">No hay hechos asociados aún.</p>
        </#if>
    </div>

<#-- HECHOS CONSENSUADOS -->
    <div class="form-section" style="margin-top: 40px;">
        <h3 class="form-section-title">🤝 Hechos consensuados</h3>
        <#if coleccion?? && coleccion.hechosConsensuados?? && (coleccion.hechosConsensuados?size > 0)>
            <div class="grid-metadata">
                <#list coleccion.hechosConsensuados as hecho>
                    <div class="card" style="cursor:pointer; border-left:4px solid;"
                         onclick="verHecho('${(hecho.id)!}')">
                        <p class="card-subtitle">${(hecho.categoria)!?default("Sin categoría")?html}</p>
                        <h4>${(hecho.titulo)!?default("Hecho sin título")?html}</h4>
                        <p style="color:#666; font-size:0.9em;">
                            ${(hecho.descripcion)!?truncate(100, "...")?html}
                        </p>
                    </div>
                </#list>
            </div>
        <#else>
            <p class="texto-placeholder">No hay hechos consensuados aún.</p>
        </#if>
    </div>

</#assign>
<#include "layout.ftl">