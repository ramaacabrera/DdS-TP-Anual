<#macro quickNavCards rolUsuario>
    <div class="home-quick-nav-section">
        <h2 class="home-section-title">Navegación Rápida</h2>
        <div class="home-quick-nav-cards">
            <a href="/hechos" class="home-nav-card">
                <div class="home-nav-card-icon">📋</div>
                <h3>Ver Hechos</h3>
                <p>Explora todos los hechos registrados en formato de lista</p>
            </a>
            <a href="/colecciones" class="home-nav-card">
                <div class="home-nav-card-icon">📚</div>
                <h3>Ver Colecciones</h3>
                <p>Accede a las colecciones de hechos creadas por nuestros administradores</p>
            </a>
            <a href="/estadisticas" class="home-nav-card">
                <div class="home-nav-card-icon">📊</div>
                <h3>Visualizador de Estadísticas</h3>
                <p>Explora datos y estadisticas actualizadas sobre hechos reportados</p>
            </a>
            <#if rolUsuario == "ADMINISTRADOR">
                <a href="/admin/solicitudes" class="home-nav-card">
                    <div class="home-nav-card-icon">📋</div>
                    <h3>Ver Solicitudes</h3>
                    <p>Consulta y gestiona las solicitudes pendientes</p>
                </a>
                <a href="/crear-coleccion" class="home-nav-card">
                    <div class="home-nav-card-icon">🗂️</div>
                    <h3>Crear Colección</h3>
                    <p>Organiza y agrupa hechos relacionados en colecciones temáticas</p>
                </a>
            </#if>
        </div>
    </div>
</#macro>

<#macro heroSection title subtitle>
    <section class="home-hero-section">
        <div class="home-hero-content">
            <h1>${title}</h1>
            <p>${subtitle}</p>
            <div class="home-hero-buttons">
                <a href="/hechos" class="home-btn home-btn-primary home-btn-large">Explorar Hechos</a>
                <a href="/crear" class="home-btn home-btn-outline home-btn-large">Reportar Hecho</a>
            </div>
        </div>
    </section>
</#macro>

<#macro mapSection title totalHechos displayedHechos>
    <section class="home-map-section">
        <div class="home-container">
            <h2 class="home-section-title">${title}</h2>
            <div class="home-map-container">
                <div id="map" class="home-map-placeholder">
                    <div class="home-map-loading">Cargando mapa...</div>
                </div>
                <div class="home-map-info">
                    <p><strong>${displayedHechos}</strong> de <strong>${totalHechos}</strong> hechos mostrados en el mapa</p>
                </div>
            </div>
        </div>
    </section>
</#macro>

<#macro hero hecho>
    <div class="hero">
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
        <!-- Compartir: chain -->
        <button class="icon-btn" data-action="share" aria-label="Compartir hecho" title="Compartir">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path d="M10.59 13.41a1 1 0 001.41 0l4.59-4.59a3 3 0 10-4.24-4.24l-1.3 1.3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M13.41 10.59a1 1 0 00-1.41 0L7.41 15.18a3 3 0 104.24 4.24l1.3-1.3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
        </button>

        <!-- Editar: lápiz -->
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
                <h3 class="card-title" title="${titulo?html}">${titulo?html}</h3>
                <div class="row gap-8">
                    <a href="${verHref}" class="btn btn-sm">Ver</a>
                    <a href="${editarHref}" class="btn btn-sm btn-ghost">Editar</a>
                </div>
            </div>

            <#if resumen?has_content>
                <p class="muted">${resumen?html}</p>
            </#if>

            <!-- Información de metadata - Solo fecha y categoría -->
            <div class="card-meta">
                <#if fecha??>
                    <div class="meta-item">
                        <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                        </svg>
                        <span class="meta-label">Fecha:</span>
                        <span class="meta-value">
                            <#-- Convertir timestamp a fecha legible -->
                            <#if fecha?is_number>
                                ${(fecha?number)?number_to_date?string('dd/MM/yyyy')}
                            <#elseif fecha?is_date_like>
                                ${fecha?datetime?string('dd/MM/yyyy')}
                            <#else>
                                ${fecha?string!''}
                            </#if>
                        </span>
                    </div>
                </#if>

                <#if categoria?has_content>
                    <div class="meta-item">
                        <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
                        </svg>
                        <span class="meta-label">Categoría:</span>
                        <span class="meta-value">${categoria?html}</span>
                    </div>
                </#if>
            </div>

            <!-- Etiquetas -->
            <#if etiquetas?? && (etiquetas?size > 0)>
                <div class="card-tags">
                    <#list etiquetas as e>
                        <span class="tag">${e?html}</span>
                    </#list>
                </div>
            </#if>
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
