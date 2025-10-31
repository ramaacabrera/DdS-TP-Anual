<#assign pageTitle = "Estadísticas - Metamapa">
<#assign additionalCss = ["/css/styleEstadisticas.css"]>

<#assign content>
    <div class="container">
        <header class="header-estadisticas">
            <h1>Dashboard de Estadísticas - Metamapa</h1>
            <p class="subtitle">Análisis de datos en tiempo real</p>
        </header>

        <div class="stats-grid">
            <!-- Estadísticas de Colección (solo si hay UUID) -->
            <div id="coleccion-card-container">
                <#if uuidColeccion??>
                    <section class="card card-highlight">
                        <h2>Estadísticas de la Colección</h2>
                        <div class="general-stats">
                            <div class="stat-item">
                                <span class="stat-label">Nombre de la Colección:</span>
                                <span class="stat-value">
                                <#if nombreColeccion??>
                                    ${nombreColeccion}
                                <#elseif statsColeccion?? && statsColeccion.nombre??>
                                    ${statsColeccion.nombre}
                                <#else>
                                    ${uuidColeccion}
                                </#if>
                            </span>
                            </div>
                            <div class="stat-item">
                                <span class="stat-label">Provincia con más hechos:</span>
                                <span class="stat-value">
                                <#if statsColeccion??>
                                    ${statsColeccion.provincia!statsColeccion.estadisticasColeccion_provincia!"N/A"}
                                <#else>
                                    N/A
                                </#if>
                            </span>
                            </div>
                        </div>
                    </section>
                </#if>
            </div>

            <div class="main-cards-grid">
                <!-- Estadísticas Generales -->
                <section class="card card-highlight">
                    <h2>Estadísticas Generales</h2>
                    <div class="general-stats">
                        <div class="stat-item">
                            <span class="stat-label">Solicitudes Spam</span>
                            <span id="spam-count" class="stat-value">${solicitudesSpam!0}</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-label">Categoría con Más Hechos</span>
                            <span id="categoria-max" class="stat-value">${categoriaMax!"N/A"}</span>
                        </div>
                    </div>
                </section>

                <!-- Estadísticas por Categoría -->
                <section class="card">
                    <h2>Estadísticas por Categoría</h2>
                    <div class="input-group">
                        <label for="categoria-select">Seleccionar Categoría:</label>
                        <input type="text" id="categoria-select" placeholder="Ej: Incendio, Choque, etc.">
                        <button onclick="buscarCategoria()" class="btn-primary">Buscar</button>
                    </div>
                    <div id="categoria-results" class="results-container">
                        <p class="placeholder-text">Ingresa una categoría para ver sus estadísticas</p>
                    </div>
                </section>

                <!-- Listado de Todas las Categorías -->
                <section class="card card-full">
                    <h2>Resumen de Todas las Categorías</h2>
                    <div id="todas-categorias" class="categorias-grid">
                        <#if categorias?? && (categorias?size > 0)>
                            <#list categorias as cat>
                                <div class="categoria-card">
                                    <h3>${cat.nombre!""}</h3>
                                    <div class="categoria-info">
                                        <div class="info-row">
                                            <span class="result-label">Provincia:</span>
                                            <span class="result-value">${cat.provincia!"N/A"}</span>
                                        </div>
                                        <div class="info-row">
                                            <span class="result-label">Hora pico:</span>
                                            <span class="result-value">${cat.hora!"N/A"}:00 hs</span>
                                        </div>
                                    </div>
                                </div>
                            </#list>
                        <#else>
                            <p class="placeholder-text">No hay categorías disponibles</p>
                        </#if>
                    </div>
                </section>
            </div>
        </div>

        <div class="loading-overlay" id="loading-overlay">
            <div class="spinner"></div>
        </div>
    </div>

    <script src="/js/estadisticas.js"></script>

    <style>
        #coleccion-card-container:empty {
            display: none;
        }
    </style>
</#assign>

<#include "layout.ftl">