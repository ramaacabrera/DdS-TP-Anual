<#assign pageTitle = "Hechos" />
<#assign additionalCss = ["/css/styleHome.css"]>

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
                                <@cmp.hechoCard
                                id=h.hechoId
                                titulo=h.titulo
                                resumen=h.descripcion!""
                                fecha=h.fechaDeAcontecimiento!""
                                categoria=h.categoria!""
                                ubicacion=h.ubicacion!""
                                etiquetas=h.etiquetas![]
                                verHref="/hechos/${h.hechoId}"
                                editarHref="/editar-hecho/${h.hechoId}" />
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
    </div>
</#assign>

<script>
    function toggleFilters() {
        const filtersContent = document.getElementById('filtersContent');
        const toggleBtn = document.querySelector('.filters-header button');

        filtersContent.classList.toggle('collapsed');
        toggleBtn.classList.toggle('rotated');
    }

    // Eliminar la aplicación automática de filtros
    document.addEventListener('DOMContentLoaded', function() {
        // Ya no hay event listeners para cambios automáticos en filtros
        // Los filtros solo se aplican al hacer clic en "Aplicar" o "Buscar"

        // Opcional: puedes agregar funcionalidad para limpiar filtros si lo deseas
        const clearButtons = document.querySelectorAll('.btn-ghost');
        clearButtons.forEach(button => {
            if (button.textContent.includes('Limpiar')) {
                button.addEventListener('click', function(e) {
                    e.preventDefault();
                    // Redirigir a la página sin parámetros
                    window.location.href = '${baseHref!"/hechos"}';
                });
            }
        });
    });
</script>

<#include "layout.ftl">
