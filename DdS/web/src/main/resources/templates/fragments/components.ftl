<#macro hero hecho>
    <div class="hero">
        <#-- lógica: si hay multimedia, usá la primera imagen; si no, fallback -->
        <#if hecho.getContenidoMultimedia()?? && (hecho.getContenidoMultimedia()?size > 0)>
            <#assign primerMedia = hecho.getContenidoMultimedia()[0]>
            <#if (primerMedia.getTipoContenido()?upper_case == "IMAGEN") && primerMedia.contenido?has_content>
                <img src="${primerMedia.contenido?html}" alt="Imagen principal del hecho" style="width:100%; height:100%; object-fit:cover;">
            <#else>
                <div class="hero-default"> <img src="/img/noimg-default.png"alt="Sin imagen principal"></div>
            </#if>
        <#else>
            <div class="hero-default"> <img src="/img/noimg-default.png"alt="Sin imagen principal"></div>
        </#if>
    </div>
</#macro>
<#macro actionButtons hecho>
    <div class="action-buttons-group" data-hecho-id="${hecho.hecho_id?html}">
        <!-- Compartir: icono "share" (flecha saliendo de caja) -->
        <button class="icon-btn" data-action="share" aria-label="Compartir hecho" title="Compartir">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path d="M4 12v7a1 1 0 001 1h14a1 1 0 001-1v-7" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M16 6l-4-4-4 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M12 2v13" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
        </button>

        <!-- Editar: lápiz claro -->
        <button class="icon-btn" data-action="edit" aria-label="Solicitar modificación" title="Solicitar modificación">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path d="M3 21l3-1 11-11 1-3-3 1L4 20z" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M14 6l4 4" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
        </button>

        <!-- Eliminar: tacho de basura  -->
        <button class="icon-btn danger" data-action="delete" aria-label="Solicitar eliminación" title="Solicitar eliminación">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path d="M3 6h18" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M8 6v14a1 1 0 001 1h6a1 1 0 001-1V6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M10 11v6M14 11v6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
        </button>
    </div>
</#macro>

<#macro tagsList etiquetas>
    <div class="tag-list">
        <#if etiquetas?? && (etiquetas?size > 0)>
            <#list etiquetas as etiqueta>
                <span class="tag">${etiqueta.nombre?html}</span>
            </#list>
        <#else>
            <p class="card-subtitle" style="color:#999;">Sin etiquetas.</p>
        </#if>
    </div>
</#macro>

<#macro mediaGrid hecho>
    <div class="media-grid">
        <#if hecho.getContenidoMultimedia()?? && (hecho.getContenidoMultimedia()?size > 0)>
            <#list hecho.getContenidoMultimedia() as media>
                <div class="media-item">
                    <#if media.getTipoContenido()?upper_case == "IMAGEN">
                        <img src="${media.contenido?html}" alt="Media" style="width:100%; height:100%; object-fit:cover;">
                    <#elseif media.getTipoContenido()?upper_case == "AUDIO">
                        <div style="padding:10px; font-size:0.9rem; color:var(--muted-color);">
                            🎧 <a href="${media.contenido?html}" target="_blank" rel="noopener">Audio</a>
                        </div>
                    <#elseif media.getTipoContenido()?upper_case == "VIDEO">
                        <div style="padding:10px; font-size:0.9rem; color:var(--muted-color);">
                            📹 <a href="${media.contenido?html}" target="_blank" rel="noopener">Video</a>
                        </div>
                    <#else>
                        <div style="padding:10px; font-size:0.9rem; color:var(--muted-color);">
                            📎 <a href="${media.contenido?html}" target="_blank" rel="noopener">Archivo</a>
                        </div>
                    </#if>
                </div>
            </#list>
        <#else>
            <div style="grid-column:1 / -1; color:#999;">No hay contenido multimedia adjunto.</div>
        </#if>
    </div>
</#macro>

<#-- ===== FUNCIONES HELPER PARA FILTROS ===== -->
<#-- Función para obtener un valor simple -->
<#function _val values key>
    <#if values?? && values[key]??>
        <#return values[key]>
    </#if>
    <#return "">
</#function>

<#-- Función para obtener múltiples valores (array/lista) -->
<#function _vals values key>
    <#if values?? && values[key]??>
        <#if values[key]?is_sequence>
            <#return values[key]>
        <#else>
            <#return [values[key]]>
        </#if>
    </#if>
    <#return []>
</#function>

<#-- Función para obtener nombre legible del filtro -->
<#function getFilterLabel filter>
    <#if filter??>
        <#if filter.label??>
            <#return filter.label>
        <#elseif filter.key??>
            <#return filter.key?replace("_", " ")?cap_first>
        <#else>
            <#return "Filtro">
        </#if>
    </#if>
    <#return "Filtro">
</#function>

<#-- ===== Campo de filtro (robusto) ===== -->
<#macro filterField f values>
<#-- si f es null, salir silenciosamente -->
    <#if !f??>
        <#return>
    </#if>

<#-- normalizar acceso a claves -->
    <#assign key   = (f.key)! (f['key'])!''>
    <#assign label = getFilterLabel(f) >
    <#assign type  = (f.type)! (f['type'])!'search' >
    <#assign options = (f.options)! (f['options'])![] >
    <#assign optionObjs = (f.optionObjs)! (f['optionObjs'])![] >
    <#assign sizeV = (f.size!6)?c >
    <#assign placeholder = (f.placeholder)! (f['placeholder'])!'Buscar…'>

    <div class="filter-item">
        <label class="filter-label">${label}</label>

        <#switch type>
            <#case "search">
                <input class="input w-100 filter-input" type="search" name="${key}"
                       value="${_val(values, key)}"
                       placeholder="${placeholder}"/>
                <#break>

            <#case "select">
                <select class="input filter-input" name="${key}">
                    <option value="">--</option>
                    <#list options as opt>
                        <option value="${opt}" <#if _val(values, key)==opt>selected</#if>>${opt}</option>
                    </#list>
                </select>
                <#break>

            <#case "multiselect">
                <select class="input filter-input" name="${key}" multiple size="${sizeV}">
                    <#list options as opt>
                        <option value="${opt}" <#if _vals(values, key)?seq_contains(opt)>selected</#if>>${opt}</option>
                    </#list>
                </select>
                <#break>

            <#case "date">
                <input class="input filter-input" type="date" name="${key}" value="${_val(values, key)}"/>
                <#break>

            <#case "radio-group">
                <div class="row gap-12">
                    <#if optionObjs?has_content>
                        <#list optionObjs as opt>
                            <label class="chip">
                                <input type="radio" name="${key}" value="${opt.value!opt}"
                                       <#if _val(values, key)==(opt.value!opt)>checked</#if>/> ${opt.label!opt}
                            </label>
                        </#list>
                    <#else>
                        <#list options as opt>
                            <label class="chip">
                                <input type="radio" name="${key}" value="${opt}"
                                       <#if _val(values, key)==opt>checked</#if>/> ${opt}
                            </label>
                        </#list>
                    </#if>
                </div>
                <#break>

            <#case "checkbox-group">
                <div class="row gap-12">
                    <#if optionObjs?has_content>
                        <#list optionObjs as opt>
                            <label class="chip">
                                <input type="checkbox" name="${key}" value="${opt.value!opt}"
                                       <#if _vals(values, key)?seq_contains(opt.value!opt)>checked</#if>/> ${opt.label!opt}
                            </label>
                        </#list>
                    <#else>
                        <#list options as opt>
                            <label class="chip">
                                <input type="checkbox" name="${key}" value="${opt}"
                                       <#if _vals(values, key)?seq_contains(opt)>checked</#if>/> ${opt}
                            </label>
                        </#list>
                    </#if>
                </div>
                <#break>

            <#default>
                <input class="input w-100 filter-input" type="search" name="${key}"
                       value="${_val(values, key)}"
                       placeholder="${placeholder}"/>
        </#switch>
    </div>
</#macro>

<#-- ===== Grupo de filtros (filtra nulos/strings) ===== -->
<#macro filterGroup filters values>
    <div class="filters-grid">
        <#if filters?has_content>
            <#list filters?filter(x -> x??) as f>
            <#-- Ahora f siempre es un Map que FreeMarker puede manejar -->
                <@filterField f=f values=values />
            </#list>
        </#if>

        <div class="filter-actions">
            <button class="btn" type="submit">Aplicar</button>
            <a class="btn btn-ghost" href="${(baseHref!'/hechos')}">Limpiar</a>
        </div>
    </div>
</#macro>

<#-- CARD DE HECHO -------------------------------------------------------->
<#macro hechoCard id titulo resumen fecha categoria ubicacion etiquetas verHref editarHref>
    <article class="card card-hecho">
        <div class="card-media skeleton">IMAGEN</div>

        <div class="card-body">
            <div class="row space-between align-center">
                <h3 class="card-title">${titulo?html}</h3>
                <div class="row gap-8">
                    <a href="${verHref}"   class="btn btn-sm">Ver</a>
                    <a href="${editarHref}" class="btn btn-sm btn-ghost">Editar</a>
                </div>
            </div>

            <p class="muted">${resumen?html}</p>

            <div class="row gap-16 mt-8 small muted">
                <label><input type="radio" disabled /> Fecha: ${fecha?html}</label>
                <label><input type="radio" disabled /> Latitud: ${ubicacion.latitud?html}</label>
                <label><input type="radio" disabled /> Longitud: ${ubicacion.longitud?html}</label>
                <label><input type="radio" disabled /> Categoría: ${categoria?html}</label>
            </div>

            <div class="row gap-8 mt-8">
                <#list etiquetas as e>
                    <span class="tag">${e?html}</span>
                </#list>
            </div>
        </div>
    </article>
</#macro>

<#-- Función fuera del macro -->
<#function qs k v>
    <#if v?is_sequence>
        <#return v?map(x -> (k?url) + "=" + (x?url))?join("&")>
    <#else>
        <#return (k?url) + "=" + (v?url)>
    </#if>
</#function>

<#-- Función fuera del macro -->
<#function keepToQuery keep>
    <#assign parts=[] />
    <#list keep?keys as k>
        <#if keep[k]?? && keep[k] != "" && !(keep[k]?is_sequence && keep[k]?size==0)>
            <#assign parts += [qs(k, keep[k])] />
        </#if>
    </#list>
    <#return parts?join("&")>
</#function>

<#-- Macro principal sin funciones anidadas -->
<#macro pager page size totalPages baseHref keep>
    <#if (totalPages?number > 1)>
        <#assign q = keepToQuery(keep) />
        <#assign prev = (page > 1)?then(page-1,1) />
        <#assign next = (page < totalPages)?then(page+1,totalPages) />

        <nav class="pager row gap-8 mt-16">
            <a class="btn btn-sm" href="${baseHref}?page=1&size=${size}<#if q?has_content>&${q}</#if>">« Primero</a>
            <a class="btn btn-sm" href="${baseHref}?page=${prev}&size=${size}<#if q?has_content>&${q}</#if>">‹ Anterior</a>
            <span class="muted small">Página ${page} de ${totalPages}</span>
            <a class="btn btn-sm" href="${baseHref}?page=${next}&size=${size}<#if q?has_content>&${q}</#if>">Siguiente ›</a>
            <a class="btn btn-sm" href="${baseHref}?page=${totalPages}&size=${size}<#if q?has_content>&${q}</#if>">Último »</a>
        </nav>
    </#if>
</#macro>
