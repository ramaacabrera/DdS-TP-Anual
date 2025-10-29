<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard de Estadísticas - Metamapa</title>
    <link rel="stylesheet" href="/css/styleEstadisticas.css">
</head>
<body>
<div class="container">
    <header class="header">
        <h1>Dashboard de Estadísticas - Metamapa</h1>
        <p class="subtitle">Análisis de datos en tiempo real</p>
    </header>

    <div class="stats-grid">
        <!-- Estadísticas Generales -->
        <section class="card card-highlight">
            <h2>Estadísticas Generales</h2>
            <div class="general-stats">
                <div class="stat-item">
                    <span class="stat-label">Solicitudes Spam</span>
                    <span class="stat-value">${solicitudesSpam}</span>
                </div>
                <div class="stat-item">
                    <span class="stat-label">Categoría con Más Hechos</span>
                    <span class="stat-value">${categoriaMax}</span>
                </div>
            </div>
        </section>

        <!-- Estadísticas por Categoría -->
        <section class="card">
            <h2>Estadísticas por Categoría</h2>
            <div class="input-group">
                <label for="categoria-select">Seleccionar Categoría:</label>
                <input type="text" id="categoria-select" placeholder="Ej: ROBO, VANDALISMO, etc.">
                <button onclick="buscarCategoria()" class="btn-primary">Buscar</button>
            </div>
            <div id="categoria-results" class="results-container">
                <p class="placeholder-text">Ingresa una categoría para ver sus estadísticas</p>
            </div>
        </section>

        <!-- Estadísticas por Colección -->
        <section class="card">
            <h2>Estadísticas por Colección</h2>
            <div class="input-group">
                <label for="coleccion-select">ID de Colección:</label>
                <input type="text" id="coleccion-select" placeholder="Ingresa el UUID de la colección">
                <button onclick="buscarColeccion()" class="btn-primary">Buscar</button>
            </div>
            <div id="coleccion-results" class="results-container">
                <p class="placeholder-text">Ingresa una colección para ver sus estadísticas</p>
            </div>
        </section>

        <!-- Listado de Todas las Categorías -->
        <section class="card card-full">
            <h2>Resumen de Todas las Categorías</h2>
            <div id="todas-categorias" class="categorias-grid">
                <#-- Renderizar categorías desde el servidor -->
                <#if categorias?? && (categorias?size > 0)>
                    <#list categorias as cat>
                        <div class="categoria-card">
                            <h3>${cat.nombre}</h3>
                            <div class="categoria-info">
                                <div class="info-row">
                                    <span class="result-label">Provincia:</span>
                                    <span class="result-value">${cat.provincia}</span>
                                </div>
                                <div class="info-row">
                                    <span class="result-label">Hora pico:</span>
                                    <span class="result-value">${cat.hora}:00 hs</span>
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

    <div class="loading-overlay" id="loading-overlay">
        <div class="spinner"></div>
    </div>
</div>

<script src="/js/estadisticas.js"></script>
</body>
</html>

