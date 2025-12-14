<script src="/js/coleccion.js" defer></script>

<#assign pageTitle = (coleccion.titulo)!?default("Colección Detallada")>
<#assign additionalCss = ["/css/styleCrearHechoSolEliminacion.css"]>
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
                <#if rolUsuario?? && rolUsuario == "ADMINISTRADOR">
                    <button type="button" class="btn btn-secondary" onclick="window.location.href='/colecciones/${coleccion.handle}/editar'">✏️ Editar</button>
                </#if>
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
        <#if coleccion?? && coleccion.fuente?? && (coleccion.fuente?size > 0)>
            <ul class="list-group">
                <#list coleccion.fuente as f>
                    <li class="list-item">
                        <strong>${(f.tipoDeFuente)!?default("GENERAL")?capitalize}:</strong>
                        ${(f.descriptor)!?html}
                    </li>
                </#list>
            </ul>
        <#else>
            <p style="color: #666; font-style: italic;">No hay fuentes asociadas.</p>
        </#if>
    </div>

<#-- CRITERIOS -->
    <div class="card" style="margin-bottom: 25px;">
        <p class="card-subtitle">🧩 Criterios de pertenencia</p>
        <#if coleccion?? && coleccion.criteriosDePertenencia?? && (coleccion.criteriosDePertenencia?size > 0)>
            <div class="criterios-container" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 15px;">
                <#list coleccion.criteriosDePertenencia as c>

                <#-- Detectamos el tipo de forma robusta -->
                    <#assign tipo = c.tipoCriterio!c['@type']!"Desconocido">

                    <div class="criterio-card" style="border: 1px solid #e0e0e0; border-radius: 8px; padding: 15px; background-color: #fafafa; position: relative; overflow: hidden;">

                        <#-- Barra lateral de color -->
                        <#assign colorBorde = "#ccc">
                        <#if tipo == "CriterioDeTexto"> <#assign colorBorde = "#3498db">
                        <#elseif tipo == "CriterioEtiquetas"> <#assign colorBorde = "#9b59b6">
                        <#elseif tipo == "CriterioFecha"> <#assign colorBorde = "#2ecc71">
                        <#elseif tipo == "CriterioUbicacion"> <#assign colorBorde = "#e74c3c">
                        <#elseif tipo == "CriterioTipoMultimedia"> <#assign colorBorde = "#e67e22">
                        <#elseif tipo == "CriterioContribuyente"> <#assign colorBorde = "#34495e">
                        </#if>
                        <div style="position: absolute; left: 0; top: 0; bottom: 0; width: 4px; background-color: ${colorBorde};"></div>

                        <#-- HEADER DEL CRITERIO -->
                        <div style="display: flex; align-items: center; margin-bottom: 10px; padding-bottom: 8px; border-bottom: 1px solid #eee; padding-left: 10px;">
                            <#if tipo == "CriterioTipoMultimedia">
                                <span style="font-size: 1.2em; margin-right: 8px;">📷</span> <strong>Multimedia</strong>
                            <#elseif tipo == "CriterioContribuyente">
                                <span style="font-size: 1.2em; margin-right: 8px;">👤</span> <strong>Contribuyente</strong>
                            <#elseif tipo == "CriterioFecha">
                                <span style="font-size: 1.2em; margin-right: 8px;">📅</span> <strong>Fecha</strong>
                            <#elseif tipo == "CriterioUbicacion">
                                <span style="font-size: 1.2em; margin-right: 8px;">📍</span> <strong>Ubicación</strong>
                            <#elseif tipo == "CriterioDeTexto">
                                <span style="font-size: 1.2em; margin-right: 8px;">📝</span> <strong>Texto</strong>
                            <#elseif tipo == "CriterioEtiquetas">
                                <span style="font-size: 1.2em; margin-right: 8px;">🏷️</span> <strong>Etiquetas</strong>
                            <#else>
                                <span style="font-size: 1.2em; margin-right: 8px;">⚙️</span> <strong>${tipo}</strong>
                            </#if>
                        </div>

                        <#-- BODY DEL CRITERIO -->
                        <div style="font-size: 0.9em; color: #555; padding-left: 10px;">

                            <#if tipo == "CriterioTipoMultimedia">
                                <div>Formato requerido:</div>
                                <div style="font-weight: bold; color: #333; margin-top: 3px;">
                                    ${(c.tipoContenidoMultimedia)!?html}
                                </div>

                            <#elseif tipo == "CriterioContribuyente">
                                <div>Usuario:</div>
                                <div style="font-weight: bold; color: #333; margin-top: 3px;">
                                    @${(c.nombreContribuyente)!?html}
                                </div>

                            <#elseif tipo == "CriterioFecha">
                                <#assign fInicio = "">
                                <#assign fFin = "">

                            <#-- Manejo robusto de fechas (Timestamp o Date) -->
                                <#if c.fechaInicio??>
                                    <#if c.fechaInicio?is_number>
                                        <#assign fInicio = c.fechaInicio?long?number_to_datetime?string('dd/MM/yyyy')>
                                    <#else>
                                        <#assign fInicio = c.fechaInicio?string('dd/MM/yyyy')>
                                    </#if>
                                </#if>

                                <#if c.fechaFin??>
                                    <#if c.fechaFin?is_number>
                                        <#assign fFin = c.fechaFin?long?number_to_datetime?string('dd/MM/yyyy')>
                                    <#else>
                                        <#assign fFin = c.fechaFin?string('dd/MM/yyyy')>
                                    </#if>
                                </#if>

                                <div>Rango:</div>
                                <div style="margin-top: 2px;"><strong>${fInicio}</strong> al <strong>${fFin}</strong></div>
                                <div style="margin-top: 5px; font-size: 0.85em; color: #888;">
                                    Aplica a: ${(c.tipoFecha == 'fechaDeCarga')?then('Fecha de Carga', 'Fecha del Hecho')}
                                </div>

                            <#elseif tipo == "CriterioUbicacion">
                                <div>Lugar contiene:</div>
                            <#-- CAMBIO IMPORTANTE: Usamos .descripcion directo -->
                                <div style="font-weight: bold; color: #333; margin-top: 3px;">
                                    "${(c.descripcion)!?html}"
                                </div>

                            <#elseif tipo == "CriterioDeTexto">
                                <div>Busca en: <strong>${(c.tipoDeTexto)!?default("Texto")?html}</strong></div>
                                <#if c.palabras?? && (c.palabras?size > 0)>
                                    <div style="margin-top: 8px;">
                                        <div style="display: flex; flex-wrap: wrap; gap: 5px;">
                                            <#list c.palabras as palabra>
                                                <span class="badge" style="background-color: #e3f2fd; color: #1565c0; border: 1px solid #bbdefb;">
                                                    ${palabra?html}
                                                </span>
                                            </#list>
                                        </div>
                                    </div>
                                </#if>

                            <#elseif tipo == "CriterioEtiquetas">
                                <#if c.etiquetas?? && (c.etiquetas?size > 0)>
                                    <div>Etiquetas requeridas:</div>
                                    <div style="display: flex; flex-wrap: wrap; gap: 5px; margin-top: 5px;">
                                        <#list c.etiquetas as etiqueta>
                                            <span class="badge" style="background-color: #f3e5f5; color: #7b1fa2; border: 1px solid #e1bee7;">
                                                #${(etiqueta.nombre)!?html}
                                            </span>
                                        </#list>
                                    </div>
                                <#else>
                                    <div><em>Sin etiquetas específicas</em></div>
                                </#if>
                            </#if>
                        </div>
                    </div>
                </#list>
            </div>
        <#else>
            <p style="color: #666; font-style: italic;">No hay criterios definidos para esta colección.</p>
        </#if>
    </div>

    <hr class="separator" style="border: 1px solid #eee; margin: 30px 0;">

<#-- HECHOS ASOCIADOS -->
    <div class="form-section">
        <h3 class="form-section-title">📂 Hechos asociados</h3>
        <#if coleccion?? && coleccion.hechos?? && (coleccion.hechos?size > 0)>
            <div class="grid-metadata" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px;">
                <#list coleccion.hechos as hecho>
                    <div class="card" style="cursor:pointer; transition: transform 0.2s;" onclick="window.location.href='/hechos/${(hecho.hechoId)!}'">
                        <p class="card-subtitle" style="font-size: 0.8rem; text-transform: uppercase; color: #888; margin-bottom: 5px;">${(hecho.categoria)!?default("GENERAL")?html}</p>
                        <h4 style="margin: 0 0 10px 0; color: #333;">${(hecho.titulo)!?default("Hecho sin título")?html}</h4>
                        <p style="color:#666; font-size:0.9em; line-height: 1.4;">
                            ${(hecho.descripcion)!?truncate(80, "...")?html}
                        </p>
                    </div>
                </#list>
            </div>
        <#else>
            <p class="texto-placeholder" style="color: #999;">No hay hechos asociados aún.</p>
        </#if>
    </div>

</#assign>

<style>
    .badge {
        display: inline-block;
        padding: 3px 10px;
        border-radius: 12px;
        font-size: 0.85em;
        font-weight: 500;
    }

    .list-item {
        padding: 10px 0;
        border-bottom: 1px solid #eee;
        list-style: none;
    }

    .list-group {
        padding-left: 0;
        margin: 0;
    }
</style>
<#include "layout.ftl">