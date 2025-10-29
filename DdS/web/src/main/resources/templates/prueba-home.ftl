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
<div class="container">
    <!-- Sidebar con Filtros -->
    <aside class="sidebar">
        <h2 class="sidebar-title">Filtros</h2>
        <form id="filterForm">
            <div class="filter-group">
                <label class="filter-label" for="categoria">Categoría</label>
                <select class="filter-select" id="categoria" name="categoria">
                    <option value="">Todas las categorías</option>
                    <#list categorias as categoria>
                        <option value="${categoria}" <#if filtros.categoria?? && filtros.categoria == categoria>selected</#if>>
                            ${categoria}
                        </option>
                    </#list>
                </select>
            </div>

            <div class="filter-group">
                <label class="filter-label" for="ubicacion">Ubicación</label>
                <input type="text" class="filter-input" id="ubicacion" name="ubicacion"
                       placeholder="Ej: Buenos Aires" value="${filtros.ubicacion!''}">
            </div>

            <div class="filter-group">
                <label class="filter-label" for="fechaDesde">Fecha Acontecimiento (Desde)</label>
                <input type="date" class="filter-input" id="fechaDesde" name="fechaDesde"
                       value="${filtros.fechaDesde!''}">
            </div>

            <div class="filter-group">
                <label class="filter-label" for="fechaHasta">Fecha Acontecimiento (Hasta)</label>
                <input type="date" class="filter-input" id="fechaHasta" name="fechaHasta"
                       value="${filtros.fechaHasta!''}">
            </div>

            <div class="filter-group">
                <label class="filter-label" for="fuente">Fuente</label>
                <select class="filter-select" id="fuente" name="fuente">
                    <option value="">Todas las fuentes</option>
                    <#list fuentes as fuente>
                        <option value="${fuente}" <#if filtros.fuente?? && filtros.fuente == fuente>selected</#if>>
                            ${fuente}
                        </option>
                    </#list>
                </select>
            </div>

            <div class="filter-group">
                <label class="filter-label" for="contribuyente">Contribuyente</label>
                <input type="text" class="filter-input" id="contribuyente" name="contribuyente"
                       placeholder="Nombre del contribuyente" value="${filtros.contribuyente!''}">
            </div>

            <div class="filter-group">
                <label class="filter-label" for="etiquetas">Etiquetas</label>
                <input type="text" class="filter-input" id="etiquetas" name="etiquetas"
                       placeholder="Separadas por comas" value="${filtros.etiquetas!''}">
            </div>

            <div class="filter-group">
                <label class="filter-label" for="coleccion">Colección</label>
                <select class="filter-select" id="coleccion" name="coleccion">
                    <option value="">Todas las colecciones</option>
                    <#list colecciones as coleccion>
                        <option value="${coleccion.id}" <#if filtros.coleccion?? && filtros.coleccion == coleccion.id?string>selected</#if>>
                            ${coleccion.nombre}
                        </option>
                    </#list>
                </select>
            </div>

            <button type="submit" class="btn btn-filter">Aplicar Filtros</button>
            <button type="button" class="btn btn-clear" onclick="clearFilters()">Limpiar Filtros</button>
        </form>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
        <!-- Search Bar -->
        <div class="search-container">
            <form class="search-bar" id="searchForm">
                <input type="text" class="search-input" id="searchInput" name="busqueda"
                       placeholder="Buscar por título, descripción o categoría..."
                       value="${busqueda!''}">
                <button type="submit" class="btn btn-search">Buscar</button>
            </form>
        </div>

        <!-- Hechos Grid -->
        <div class="hechos-grid" id="hechosGrid">
            <#if cargando>
                <!-- Skeleton Loaders -->
                <#list 1..9 as i>
                    <div class="hecho-card skeleton">
                        <div class="hecho-image-container">
                            <div class="hecho-image skeleton"></div>
                            <div class="hecho-category skeleton">Categoría</div>
                        </div>
                        <div class="hecho-content">
                            <h3 class="hecho-title skeleton">Título del hecho</h3>
                            <div class="hecho-info">
                                <div class="hecho-info-item skeleton">Fecha</div>
                                <div class="hecho-info-item skeleton">Ubicación</div>
                                <div class="hecho-info-item skeleton">Contribuyente</div>
                            </div>
                            <div class="hecho-tags">
                                <span class="tag skeleton">etiqueta</span>
                                <span class="tag skeleton">etiqueta</span>
                                <span class="tag skeleton">etiqueta</span>
                            </div>
                        </div>
                    </div>
                </#list>
            <#elseif hechos?? && (hechos?size > 0)>
                <!-- Hechos Reales -->
                <#list hechos as hecho>
                    <div class="hecho-card" onclick="verHecho('${hecho.id}')">
                        <div class="hecho-image-container">
                            <img src="${hecho.imagenUrl!'/placeholder.svg?height=200&width=400'}"
                                 alt="${hecho.titulo}" class="hecho-image">
                            <span class="hecho-category">${hecho.categoria}</span>
                        </div>
                        <div class="hecho-content">
                            <h3 class="hecho-title">${hecho.titulo}</h3>
                            <div class="hecho-info">
                                <div class="hecho-info-item">
                                    <svg class="hecho-info-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                              d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                                    </svg>
                                    <span>${hecho.fechaAcontecimiento}</span>
                                </div>
                                <div class="hecho-info-item">
                                    <svg class="hecho-info-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                              d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                              d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                                    </svg>
                                    <span>${hecho.ubicacion}</span>
                                </div>
                                <div class="hecho-info-item">
                                    <svg class="hecho-info-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                              d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                                    </svg>
                                    <span>${hecho.contribuyente!'Anónimo'}</span>
                                </div>
                            </div>
                            <#if hecho.etiquetas?? && (hecho.etiquetas?size > 0)>
                                <div class="hecho-tags">
                                    <#list hecho.etiquetas as etiqueta>
                                        <#if etiqueta?index < 3>
                                            <span class="tag">${etiqueta}</span>
                                        </#if>
                                    </#list>
                                </div>
                            </#if>
                        </div>
                    </div>
                </#list>
            <#else>
                <!-- Empty State -->
                <div class="empty-state" style="grid-column: 1 / -1;">
                    <div class="empty-state-icon">🔍</div>
                    <h3 class="empty-state-title">No se encontraron hechos</h3>
                    <p>Intenta ajustar los filtros o realizar una nueva búsqueda</p>
                </div>
            </#if>
        </div>

        <!-- Pagination -->
        <#if !cargando && hechos?? && (totalPaginas > 1)>
            <div class="pagination">
                <button class="pagination-btn" onclick="cambiarPagina(${paginaActual - 1})"
                        <#if paginaActual == 1>disabled</#if>>
                    ← Anterior
                </button>

                <#list 1..totalPaginas as pagina>
                    <#if (pagina == 1) || (pagina == totalPaginas) ||
                    ((pagina >= paginaActual - 2) && (pagina <= paginaActual + 2))>
                        <button class="pagination-btn <#if pagina == paginaActual>active</#if>"
                                onclick="cambiarPagina(${pagina})">
                            ${pagina}
                        </button>
                    <#elseif (pagina == paginaActual - 3) || (pagina == paginaActual + 3)>
                        <span style="padding: 0.5rem;">...</span>
                    </#if>
                </#list>

                <button class="pagination-btn" onclick="cambiarPagina(${paginaActual + 1})"
                        <#if paginaActual == totalPaginas>disabled</#if>>
                    Siguiente →
                </button>
            </div>
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