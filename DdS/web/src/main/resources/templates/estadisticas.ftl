<#assign pageTitle = "Estadísticas - Metamapa">
<#assign additionalCss = ["/css/styleEstadisticas.css"]>

<#assign content>

    <header class="hero-header">
        <div class="hero-content">
            <div class="hero-text">
                <h1>Dashboard Analítico</h1>
                <p class="subtitle">Monitoreo de incidentes y datos en tiempo real de Metamapa</p>
            </div>
            <div class="hero-action">
                <a href="/estadisticas/descargar" class="btn-download-glass" target="_blank">
                    <svg width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"></path>
                    </svg>
                    Descargar CSV
                </a>
            </div>
        </div>
    </header>

    <div class="dashboard-container">
        <div class="stats-grid">

            <div id="coleccion-card-container" class="col-span-12">
                <#if uuidColeccion??>
                    <section class="card">
                        <div class="card-header">
                            <h2>Estadísticas de Colección</h2>
                            <span style="font-size:0.85rem; color:#6b7280;">ID: ${uuidColeccion}</span>
                        </div>
                        <div class="kpi-container">
                            <div class="kpi-box">
                                <span class="kpi-label">Nombre</span>
                                <span class="kpi-value" style="font-size: 1.5rem;">
                                    <#if nombreColeccion??>${nombreColeccion}<#elseif statsColeccion?? && statsColeccion.nombre??>${statsColeccion.nombre}<#else>Sin Nombre</#if>
                                </span>
                            </div>
                            <div class="kpi-box">
                                <span class="kpi-label">Provincia Dominante</span>
                                <span class="kpi-value highlight">
                                    <#if statsColeccion??>${statsColeccion.provincia!statsColeccion.estadisticasColeccion_provincia!"N/A"}<#else>N/A</#if>
                                </span>
                            </div>
                        </div>
                    </section>
                </#if>
            </div>

            <section class="card col-span-6">
                <div class="card-header">
                    <h2>Visión General</h2>
                </div>
                <div class="kpi-container">
                    <div class="kpi-box">
                        <span class="kpi-value highlight">${solicitudesSpam!0}</span>
                        <span class="kpi-label">Solicitudes Spam</span>
                    </div>
                    <div class="kpi-box">
                        <span class="kpi-value" style="font-size: 1.2rem; word-break: break-word;">${categoriaMax!"N/A"}</span>
                        <span class="kpi-label">Categoría Más Activa</span>
                    </div>
                </div>
            </section>

            <section class="card col-span-6">
                <div class="card-header">
                    <h2>Explorador por Categoría</h2>
                </div>
                <div class="search-wrapper">
                    <input type="text" id="categoria-select" class="modern-input" placeholder="Ej: Incendio, Choque...">
                </div>
                <button onclick="buscarCategoria()" class="btn-search">Analizar Categoría</button>

                <div id="categoria-results" class="results-box">
                    <p style="color: #9ca3af; font-style: italic;">Los resultados aparecerán aquí...</p>
                </div>
            </section>

            <section class="card col-span-12">
                <div class="card-header">
                    <h2>Resumen de Categorías</h2>
                </div>
                <div id="todas-categorias" class="categories-wrapper">
                    <#if categorias?? && (categorias?size > 0)>
                        <#list categorias as cat>
                            <div class="cat-item">
                                <div class="cat-header">
                                    <h3>${cat.nombre!""}</h3>
                                </div>
                                <div class="cat-stat-row">
                                    <span class="label-mini">Provincia:</span>
                                    <span class="value-mini">${cat.provincia!"N/A"}</span>
                                </div>
                                <div class="cat-stat-row">
                                    <span class="label-mini">Hora Pico:</span>
                                    <span class="value-mini">${cat.hora!"N/A"}:00 hs</span>
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

    <script src="/js/estadisticas.js?v=3"></script>

    <style>
        #coleccion-card-container:empty {
            display: none;
        }
    </style>
    <style>
        body {
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            margin: 0;
            padding-bottom: 0 !important;
        }
        main.container {
            flex: 1;
            width: 100%;
        }
    </style>
</#assign>


<#include "layout.ftl">