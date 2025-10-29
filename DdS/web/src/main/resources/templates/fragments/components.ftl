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

<#-- ===== Campo de filtro (robusto) ===== -->
<#macro filterField f values>
<#-- si f es null, salir silenciosamente -->
    <#if !f??>
        <#return>
    </#if>

<#-- normalizar acceso a claves -->
    <#assign key   = (f.key)! (f['key'])!''>
    <#assign label = (f.label)! (f['label'])! (key?cap_first) >
    <#assign type  = (f.type)! (f['type'])!'search' >
    <#assign options = (f.options)! (f['options'])![] >
    <#assign optionObjs = (f.optionObjs)! (f['optionObjs'])![] >
    <#assign sizeV = (f.size!6)?c >

    <div class="filter-item">
        <label class="filter-label">${label}</label>

        <#if type??>
            <#switch type>
                <#case "search">
                    <input class="input w-100" type="search" name="${key}"
                           value="${_val(values, key)}"
                           placeholder="${f.placeholder!'Buscar…'}"/>
                    <#break>

                <#case "select">
                    <select class="input" name="${key}">
                        <option value="">--</option>
                        <#list options as opt>
                            <option value="${opt}" <#if _val(values, key)==opt>selected</#if>>${opt}</option>
                        </#list>
                    </select>
                    <#break>

                <#case "multiselect">
                    <select class="input" name="${key}" multiple size="${sizeV}">
                        <#list options as opt>
                            <option value="${opt}" <#if (_vals(values, key)?seq_contains(opt))>selected</#if>>${opt}</option>
                        </#list>
                    </select>
                    <#break>

                <#case "date">
                    <input class="input" type="date" name="${key}" value="${_val(values, key)}"/>
                    <#break>

                <#case "radio-group">
                    <div class="row gap-12">
                        <#if optionObjs?has_content>
                            <#list optionObjs as opt>
                                <label class="chip">
                                    <input type="radio" name="${key}" value="${opt.value}"
                                           <#if _val(values, key)==opt.value>checked</#if>/> ${opt.label}
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
                                    <input type="checkbox" name="${key}" value="${opt.value}"
                                           <#if (_vals(values, key)?seq_contains(opt.value))>checked</#if>/> ${opt.label}
                                </label>
                            </#list>
                        <#else>
                            <#list options as opt>
                                <label class="chip">
                                    <input type="checkbox" name="${key}" value="${opt}"
                                           <#if (_vals(values, key)?seq_contains(opt))>checked</#if>/> ${opt}
                                </label>
                            </#list>
                        </#if>
                    </div>
                    <#break>

                <#default>
                    <input class="input w-100" type="search" name="${key}"
                           value="${_val(values, key)}"
                           placeholder="${f.placeholder!'Buscar…'}"/>
            </#switch>
        </#if>  <#-- 👈 CIERRE del <#if f.type??> que faltaba -->
    </div>
</#macro>

<#-- ===== Grupo de filtros (filtra nulos/strings) ===== -->
<#macro filterGroup filters values>
    <div class="filters-grid">
        <#if filters?has_content>
            <#list filters?filter(x -> x??) as f>
            <#-- Si viene un string, lo convierto a filtro search simple -->
                <#if f?is_string>
                    <@filterField f={"key": f, "label": f?cap_first, "type": "search"} values=values />
                <#elseif f?is_hash || f?is_hash_ex>
                    <@filterField f=f values=values />
                <#else>
                <#-- item desconocido: lo ignoro -->
                </#if>
            </#list>
        </#if>

        <div class="filter-actions">
            <button class="btn" type="submit">Aplicar</button>
            <a class="btn btn-ghost" href="${(baseHref!'/api/hechos')}">Limpiar</a>
        </div>
    </div>
</#macro>
