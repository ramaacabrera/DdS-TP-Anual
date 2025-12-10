<script>
    const URL_ADMIN = '${urlAdmin}';
    const ACCESS_TOKEN = '${accessToken!""}';
    const BASE_HREF = '${baseHref!""}';
    console.log(BASE_HREF);

    document.addEventListener('DOMContentLoaded', function() {
        const botonLimpiar = document.getElementById('limpiar-filtros');
        if (botonLimpiar) {
            console.log('Boton Limpiar encontrado, href:', botonLimpiar.href);
            botonLimpiar.addEventListener('click', function(e) {
                console.log('Click en Limpiar');
                e.preventDefault();
                window.location.href = '/hechos';
            });
        }
    });
</script>

<script src="/js/hechos.js" defer></script>

<#assign pageTitle = "Hechos" />
<#assign additionalCss = ["/css/styleHechos.css"]>

<#assign content>
    <div class="container">
        <!-- Formulario único que incluye tanto búsqueda como filtros -->
        <form method="get" action="${baseHref}" id="mainSearchForm">
            <!-- Barra de búsqueda principal -->
            <div class="search-hero">
                <div class="box">
                    <h2 class="section-title">Buscador de Hechos</h2>
                    <!-- 🔗 Botón para ir a las colecciones -->
                    <div class="search-container">
                        <div class="search-input-group">
                            <input type="search"
                                   name="textoBusqueda"
                                   placeholder="Buscar por título, descripción, categoría..."
                                   class="search-main-input"
                                   value="${filterValues['textoBusqueda']!''}">
                            <button type="submit" class="btn btn-primary search-btn">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                    <path d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                                </svg>
                                Buscar
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="main-layout">
                <!-- Sidebar con filtros avanzados -->
                <aside class="sidebar">
                    <div class="box">
                        <div class="filters-header">
                            <h2 class="section-title">Filtros Avanzados</h2>
                            <button type="button" class="btn btn-sm btn-ghost" onclick="toggleFilters()">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                    <path d="M19 9l-7 7-7-7"/>
                                </svg>
                            </button>
                        </div>

                        <div class="filters-content" id="filtersContent">
                            <#import "fragments/components.ftl" as cmp>
                            <@cmp.filterGroup filters=filters values=filterValues />
                        </div>
                    </div>
                </aside>

                <!-- Contenido principal -->
                <main class="main-content">
                    <!-- Información de resultados -->
                    <#if total??>
                        <div class="results-info">
                            <span class="muted">Mostrando ${(fromIndex!0)+1}-${(toIndex!0)} de ${total} hechos</span>
                        </div>
                    </#if>

                    <!-- Grid de hechos -->
                    <#if hechos?? && (hechos?size > 0)>
                        <div class="list column gap-16">
                            <#import "fragments/components.ftl" as cmp>
                            <#list hechos as h>
                                <#assign imagenUrl = "">

                                <#if h.contenidoMultimedia?has_content>
                                    <#list h.contenidoMultimedia as media>
                                        <#if (media.tipoContenido!"")?string?upper_case == "IMAGEN">
                                            <#assign imagenUrl = media.contenido>
                                            <#break>
                                        </#if>
                                    </#list>
                                </#if>

                                <@cmp.hechoCard
                                id=h.hechoId
                                titulo=h.titulo
                                resumen=h.descripcion!""
                                fecha=h.fechaDeAcontecimiento!""
                                categoria=h.categoria!""
                                ubicacion=h.ubicacion!""
                                etiquetas=h.etiquetas![]
                                imagen=imagenUrl
                                verHref="/hechos/${h.hechoId}"
                                rolUsuario='${rolUsuario}'!""
                                />
                            </#list>
                        </div>
                    <#else>
                        <div class="empty-state">
                            <div class="empty-state-icon">🔍</div>
                            <h3>No se encontraron hechos</h3>
                            <p>Intenta ajustar los filtros o realizar una nueva búsqueda</p>
                        </div>
                    </#if>

                    <!-- Paginador -->
                    <#if totalPages?? && (totalPages > 1)>
                        <#import "fragments/components.ftl" as cmp>
                        <@cmp.pager
                        page=(page!1)
                        size=(size!10)
                        totalPages=(totalPages!1)
                        baseHref=(baseHref!"/hechos")
                        keep=filterValues />
                    </#if>
                </main>
            </div>
        </form>

        <div id="modalEtiqueta" class="modal-overlay hidden">
            <div class="modal-content zoom-in">
                <div class="modal-header">
                    <h3>Nueva Etiqueta</h3>
                    <button type="button" class="btn-close-modal" onclick="cerrarModalEtiqueta()">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="18" y1="6" x2="6" y2="18"></line>
                            <line x1="6" y1="6" x2="18" y2="18"></line>
                        </svg>
                    </button>
                </div>

                <form id="formAgregarEtiqueta" onsubmit="enviarEtiqueta(event)">
                    <input type="hidden" id="modalHechoId" name="hechoId">

                    <div class="form-group">
                        <label for="inputEtiqueta" class="filter-label">Nombre de la etiqueta</label>
                        <input type="text" id="inputEtiqueta" name="etiqueta" class="input" placeholder="Ej: ambiental, peligro, desastre, etc" required autocomplete="off">
                    </div>

                    <div class="modal-actions">
                        <button type="button" class="btn btn-ghost" onclick="cerrarModalEtiqueta()">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Guardar Etiqueta</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</#assign>

<script>

</script>

<#include "layout.ftl">
