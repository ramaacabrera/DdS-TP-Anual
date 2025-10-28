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
                <label><input type="radio" disabled /> Ubicación: ${ubicacion?html}</label>
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

<#-- RENDER DE UN CAMPO DE FILTRO SEGÚN SU TIPO -->
<#macro filterField f values>
<#-- helpers -->
    <#function val k>
        <#return (values[k]!"")>
    </#function>
    <#function vals k>
    <#-- para multiselect/checkbox group -->
        <#return (values[k]??)?then(values[k], [])>
    </#function>

    <div class="filter-item">
        <label class="filter-label">${f.label}</label>

        <#switch f.type>
            <#case "search">
                <input class="input w-100" type="search" name="${f.key}" value="${val(f.key)}" placeholder="${f.placeholder!'Buscar…'}"/>
                <#break>

            <#case "select">
                <select class="input" name="${f.key}">
                    <option value="">--</option>
                    <#list (f.options!?[]) as opt>
                        <option value="${opt}" <#if val(f.key)==opt>selected</#if>>${opt}</option>
                    </#list>
                </select>
                <#break>

            <#case "multiselect">
                <select class="input" name="${f.key}" multiple size="${(f.size!6)?c}">
                    <#list (f.options!?[]) as opt>
                        <option value="${opt}" <#if (vals(f.key)?seq_contains(opt))>selected</#if>>${opt}</option>
                    </#list>
                </select>
                <#break>

            <#case "date">
                <input class="input" type="date" name="${f.key}" value="${val(f.key)}"/>
                <#break>

            <#case "radio-group">
                <div class="row gap-12">
                    <#list (f.options!?[]) as opt>
                        <label class="chip">
                            <input type="radio" name="${f.key}" value="${opt.value}" <#if val(f.key)==opt.value>checked</#if>/>
                            ${opt.label}
                        </label>
                    </#list>
                </div>
                <#break>

            <#case "checkbox-group">
                <div class="row gap-12">
                    <#list (f.options!?[]) as opt>
                        <label class="chip">
                            <input type="checkbox" name="${f.key}" value="${opt.value}" <#if (vals(f.key)?seq_contains(opt.value))>checked</#if>/>
                            ${opt.label}
                        </label>
                    </#list>
                </div>
                <#break>
        </#switch>
    </div>
</#macro>

<#-- RENDER DEL GRUPO DE FILTROS -->
<#macro filterGroup filters values>
    <div class="filters-grid">
        <#list filters as f>
            <@filterField f=f values=values />
        </#list>
        <div class="filter-actions">
            <button class="btn" type="submit">Aplicar</button>
            <a class="btn btn-ghost" href="${(baseHref!'/api/hechos')}">Limpiar</a>
        </div>
    </div>
</#macro>

<#-- PAGINADOR genérico (preserva TODO el mapa de filtros) -->
<#macro pager page size totalPages baseHref keep>
    <#function qs k v>
    <#-- k puede repetirse (multivalor) -->
        <#if v?is_sequence>
            <#return v?map(x -> (k?url) + "=" + (x?url))?join("&")>
        <#else>
            <#return (k?url) + "=" + (v?url)>
        </#if>
    </#function>

    <#function keepToQuery keep>
        <#assign parts=[] />
        <#list keep?keys as k>
            <#if keep[k]?? && keep[k] != "" && !(keep[k]?is_sequence && keep[k]?size==0)>
                <#assign parts += [qs(k, keep[k])] />
            </#if>
        </#list>
        <#return parts?join("&")>
    </#function>

    <#if totalPages?number > 1>
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

