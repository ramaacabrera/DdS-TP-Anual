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
                <#if rolUsuario = "ADMINISTRADOR">
                    <button type="button" class="btn btn-secondary" onclick="editarColeccion('${coleccion.handle}')">✏️ Editar</button>
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
            <div class="criterios-container" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 15px;">
                <#list coleccion.criteriosDePertenencia as c>
                    <div class="criterio-card" style="border: 1px solid #e0e0e0; border-radius: 8px; padding: 15px; background-color: #fafafa;">
                        <#-- ICONO Y TIPO -->
                        <div style="display: flex; align-items: center; margin-bottom: 10px; padding-bottom: 8px; border-bottom: 1px solid #eee;">
                            <#-- Iconos según el tipo -->
                            <#if c.tipoContenidoMultimedia??>
                                <span style="font-size: 1.2em; margin-right: 8px;">🎨</span>
                                <strong>TipoMultimedia</strong>
                            <#elseif c.nombreContribuyente??>
                                <span style="font-size: 1.2em; margin-right: 8px;">👤</span>
                                <strong>Contribuyente</strong>
                            <#elseif c.tipoFecha??>
                                <span style="font-size: 1.2em; margin-right: 8px;">📅</span>
                                <strong>Fecha</strong>
                            <#elseif c.ubicacion??>
                                <span style="font-size: 1.2em; margin-right: 8px;">📍</span>
                                <strong>Ubicacion</strong>
                            <#elseif c.tipoDeTexto??>
                                <span style="font-size: 1.2em; margin-right: 8px;">🔍</span>
                                <strong>Texto</strong>
                            <#elseif c.etiquetas??>
                                <span style="font-size: 1.2em; margin-right: 8px;">🏷️</span>
                                <strong>Etiquetas</strong>
                            <#elseif c.fuente??>
                                <span style="font-size: 1.2em; margin-right: 8px;">🗄️</span>
                                <strong>Fuente</strong>
                            <#else>
                                <span style="font-size: 1.2em; margin-right: 8px;">⚙️</span>
                            </#if>
                        </div>

                        <#-- CONTENIDO ESPECÍFICO SEGÚN TIPO -->
                        <div style="font-size: 0.9em; color: #555;">

                            <#-- CRITERIO TIPO MULTIMEDIA -->
                            <#if c.tipoContenidoMultimedia??>
                                <div>Tipo de contenido:</div>
                                <div style="font-weight: bold; color: #1976d2; margin-top: 3px;">
                                    <#if c.tipoContenidoMultimedia == 'VIDEO'>
                                        🎥 VIDEO
                                    <#elseif c.tipoContenidoMultimedia == 'AUDIO'>
                                        🔊 AUDIO
                                    <#elseif c.tipoContenidoMultimedia == 'IMAGEN'>
                                        🖼️ IMAGEN
                                    <#else>
                                        ${(c.tipoContenidoMultimedia)!?html}
                                    </#if>
                                </div>

                            <#-- CRITERIO CONTRIBUYENTE -->
                            <#elseif c.nombreContribuyente??>
                                <div>Contribuyente:</div>
                                <div style="font-weight: bold; color: #1976d2; margin-top: 3px;">
                                    "${(c.nombreContribuyente)!?html}"
                                </div>

                            <#-- CRITERIO FECHA -->
                            <#elseif c.tipoFecha??>
                                <div>Tipo de fecha: <em>${(c.tipoFecha)!?default("Fecha")?html}</em></div>
                                <div style="margin-top: 5px;">
                                    <#if c.fechaInicio?? && c.fechaFin??>
                                        Desde: <strong>${c.fechaInicio?datetime?string('dd/MM/yyyy')}</strong><br>
                                        Hasta: <strong>${c.fechaFin?datetime?string('dd/MM/yyyy')}</strong>
                                    <#elseif c.fechaInicio??>
                                        Desde: <strong>${c.fechaInicio?datetime?string('dd/MM/yyyy')}</strong>
                                    <#elseif c.fechaFin??>
                                        Hasta: <strong>${c.fechaFin?datetime?string('dd/MM/yyyy')}</strong>
                                    </#if>
                                </div>

                            <#-- CRITERIO UBICACIÓN -->
                            <#elseif c.ubicacion??>
                                <div>Ubicación específica:</div>
                                <div><strong>${(c.ubicacion.descripcion)!?html}</strong></div>

                            <#-- CRITERIO DE TEXTO -->
                            <#elseif c.tipoDeTexto??>
                                <div>Tipo de búsqueda: <em>${(c.tipoDeTexto)!?default("Texto")?html}</em></div>
                                <#if c.palabras?? && (c.palabras?size > 0)>
                                    <div style="margin-top: 5px;">
                                        <div>Palabras clave:</div>
                                        <div style="display: flex; flex-wrap: wrap; gap: 5px; margin-top: 3px;">
                                            <#list c.palabras as palabra>
                                                <span class="badge" style="background-color: #e8f5e8; color: #2e7d32; border: 1px solid #c8e6c9;">
                                                "${palabra?html}"
                                            </span>
                                            </#list>
                                        </div>
                                    </div>
                                </#if>

                            <#-- CRITERIO ETIQUETAS -->
                            <#elseif c.etiquetas??>
                                <#if c.etiquetas?? && (c.etiquetas?size > 0)>
                                    <div>Etiquetas requeridas:</div>
                                    <div style="display: flex; flex-wrap: wrap; gap: 5px; margin-top: 5px;">
                                        <#list c.etiquetas as etiqueta>
                                            <span class="badge" style="background-color: #e3f2fd; color: #1976d2; border: 1px solid #bbdefb;">
                                            ${(etiqueta.nombre)!?html}
                                        </span>
                                        </#list>
                                    </div>
                                <#else>
                                    <div><em>Sin etiquetas específicas</em></div>
                                </#if>

                            <#-- CRITERIO TIPO DE FUENTE -->
                            <#elseif c.fuente??>
                                <div>Tipo de fuente requerida:</div>
                                    <div style="margin-top: 5px;">
                                        <div style="font-weight: bold; color: #1976d2;">
                                            <#-- Mostrar el tipo de fuente con icono correspondiente -->
                                            <#if c.fuente == 'ESTATICA'>
                                                <span style="font-size: 1.1em; margin-right: 6px;">💾</span> ESTÁTICA
                                            <#elseif c.fuente == 'DINAMICA'>
                                                <span style="font-size: 1.1em; margin-right: 6px;">⚡</span> DINÁMICA
                                            <#elseif c.fuente == 'METAMAPA'>
                                                <span style="font-size: 1.1em; margin-right: 6px;">🗺️</span> METAMAPA
                                            <#elseif c.fuente == 'DEMO'>
                                                <span style="font-size: 1.1em; margin-right: 6px;">🧪</span> DEMO
                                            <#else>
                                                ${(c.fuente)!?html}
                                            </#if>
                                        </div>
                                </div>
                            <#-- TIPO DESCONOCIDO -->
                            <#else>
                                <div><em>Criterio sin detalles específicos</em></div>
                            </#if>
                        </div>

                        <#-- CONDICIÓN DE QUERY (opcional, para debugging) -->
                        <#if false>  <#-- Cambia a true si quieres mostrar la query para debugging -->
                            <div style="margin-top: 10px; padding-top: 8px; border-top: 1px dashed #ddd; font-size: 0.7em; color: #888; font-family: monospace;">
                                ${(c.queryCondition)!?truncate(50, "...")}
                            </div>
                        </#if>
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
            <div class="grid-metadata">
                <#list coleccion.hechos as hecho>
                    <div class="card" style="cursor:pointer;" onclick="verHecho('${(hecho.hechoId)!}')">
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

<style>
.badge {
display: inline-block;
padding: 2px 8px;
margin: 2px;
border-radius: 12px;
background-color: #f0f0f0;
color: #555;
font-size: 0.85em;
border: 1px solid #ddd;
}

.list-item {
padding: 12px 15px;
border-bottom: 1px solid #eee;
}

.list-item:last-child {
border-bottom: none;
}

.list-item:hover {
background-color: #f9f9f9;
}
</style>
<#include "layout.ftl">