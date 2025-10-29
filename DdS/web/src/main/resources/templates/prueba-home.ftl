<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MetaMapa - Sistema de Gestión de Mapeos Colaborativos</title>
    <link rel="stylesheet" href="/css/stylePruebaHome.css">
</head>
<body>
<!-- Navbar -->
<nav class="navbar">
    <a href="/" class="navbar-brand">MetaMapa</a>
    <div class="navbar-actions">
        <a href="/crear-hecho" class="btn btn-primary">+ Crear Hecho</a>

        <#if usuario??>
            <!-- Usuario logueado -->
            <div class="user-dropdown">
                <div class="user-button" onclick="toggleDropdown()">
                    <span>${usuario.nombre}</span>
                    <span>▼</span>
                </div>
                <div class="dropdown-menu" id="userDropdown">
                    <#if usuario.esAdmin>
                        <div class="dropdown-submenu">
                            <div class="dropdown-item">
                                Funciones de Administrador →
                            </div>
                            <div class="dropdown-submenu-content">
                                <a href="/admin/crear-coleccion" class="dropdown-item">Crear Colecciones</a>
                                <a href="/admin/editar-colecciones" class="dropdown-item">Editar Colecciones</a>
                                <a href="/admin/revisar-solicitudes" class="dropdown-item">Revisar Solicitudes</a>
                            </div>
                        </div>
                        <div class="dropdown-divider"></div>
                    </#if>
                    <a href="/perfil/editar" class="dropdown-item">Editar Perfil</a>
                    <div class="dropdown-divider"></div>
                    <a href="/logout" class="dropdown-item">Cerrar Sesión</a>
                </div>
            </div>
        <#else>
            <!-- Usuario no logueado -->
            <a href="/login" class="btn btn-secondary">Iniciar Sesión</a>
        </#if>
    </div>
</nav>

<!-- Main Container -->
<div class="main-layout">
    <!-- Sidebar con filtros -->
    <aside class="sidebar">
        <div class="box">
            <h2 class="section-title">Filtros</h2>
            <form class="filters-form" method="get" action="${baseHref}">
                <div class="filters-grid">
                    <#list filters as filter>
                        <div class="filter-item">
                            <label class="filter-label">${filter.label}</label>
                            <#if filter.type == "select">
                                <select name="${filter.key}" class="filter-input">
                                    <option value="">Todos</option>
                                    <#list filter.options as option>
                                        <option value="${option}"
                                                <#if filterValues[filter.key]?? && filterValues[filter.key] == option>selected</#if>>
                                            ${option}
                                        </option>
                                    </#list>
                                </select>
                            <#elseif filter.type == "date">
                                <input type="date"
                                       name="${filter.key}"
                                       class="filter-input"
                                       value="${filterValues[filter.key]!''}" />
                            <#else>
                                <input type="text"
                                       name="${filter.key}"
                                       class="filter-input"
                                       placeholder="Buscar..."
                                       value="${filterValues[filter.key]!''}" />
                            </#if>
                        </div>
                    </#list>
                </div>

                <div class="filters-actions">
                    <button type="submit" class="btn btn-primary">Aplicar</button>
                    <a href="${baseHref}" class="btn btn-secondary">Limpiar</a>
                </div>
            </form>
        </div>
    </aside>

    <!-- Contenido principal -->
    <main class="main-content">
        <!-- Barra de búsqueda rápida (opcional) -->
        <div class="box" style="margin-bottom: 2rem;">
            <form method="get" action="${baseHref}">
                <div class="search-bar">
                    <input type="search"
                           name="q"
                           placeholder="Buscar hechos..."
                           class="search-input"
                           value="${filterValues['q']!''}">
                    <button type="submit" class="btn btn-primary">Buscar</button>
                </div>
            </form>
        </div>

        <!-- Grid de hechos -->
        <div class="hechos-grid">
            <#if hechos?? && (hechos?size > 0)>
                <#list hechos as hecho>
                    <!-- tu card de hecho aquí -->
                </#list>
            <#else>
                <div class="empty-state">
                    <p>No se encontraron hechos</p>
                </div>
            </#if>
        </div>

        <!-- Paginador -->
        <#if totalPages gt 1>
            <!-- tu paginador aquí -->
        </#if>
    </main>
</div>

<script>
    // Toggle User Dropdown
    function toggleDropdown() {
        const dropdown = document.getElementById('userDropdown');
        dropdown.classList.toggle('show');
    }

    // Close dropdown when clicking outside
    window.addEventListener('click', function(e) {
        if (!e.target.closest('.user-dropdown')) {
            const dropdown = document.getElementById('userDropdown');
            dropdown.classList.remove('show');
        }
    });

    // Clear Filters
    function clearFilters() {
        document.getElementById('filterForm').reset();
        window.location.href = '/';
    }

    // Change Page
    function cambiarPagina(pagina) {
        const url = new URL(window.location.href);
        url.searchParams.set('pagina', pagina);
        window.location.href = url.toString();
    }

    // Ver Hecho
    function verHecho(id) {
        window.location.href = '/hecho/' + id;
    }

    // Handle Filter Form Submit
    document.getElementById('filterForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(this);
        const params = new URLSearchParams();

        for (let [key, value] of formData.entries()) {
            if (value) {
                params.append(key, value);
            }
        }

        window.location.href = '/?' + params.toString();
    });

    // Handle Search Form Submit
    document.getElementById('searchForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const busqueda = document.getElementById('searchInput').value;
        const url = new URL(window.location.href);

        if (busqueda) {
            url.searchParams.set('busqueda', busqueda);
        } else {
            url.searchParams.delete('busqueda');
        }

        window.location.href = url.toString();
    });
</script>
</body>
</html>