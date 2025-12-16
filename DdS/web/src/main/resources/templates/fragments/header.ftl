<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>${pageTitle!"MetaMapa"?html}</title>

    <link rel="stylesheet" href="/css/style.css">

    <!-- Leaflet -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossorigin=""/>
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" crossorigin=""></script>

    <!-- Feather Icons -->
    <script src="https://unpkg.com/feather-icons"></script>

    <link rel="icon" href="data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 width=%2224%22 height=%2224%22 viewBox=%220 0 24 24%22 fill=%22none%22 stroke=%22%231d7858%22 stroke-width=%222%22 stroke-linecap=%22round%22 stroke-linejoin=%22round%22><polygon points=%221 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6%22></polygon><line x1=%228%22 y1=%222%22 x2=%228%22 y2=%2218%22></line><line x1=%2216%22 y1=%226%22 x2=%2216%22 y2=%2222%22></line></svg>">

    <#if additionalCss??>
        <#list additionalCss as css>
            <link rel="stylesheet" href="${css}">
        </#list>
    </#if>
</head>

<body>

<header class="site-header">
    <div class="header-inner">

        <!-- LEFT -->
        <div class="header-left">
            <a href="/" class="brand">
                <span class="brand-icon"><i data-feather="map"></i></span> MetaMapa
            </a>
        </div>

        <!-- CENTER (solo desktop) -->
        <nav class="header-center">
            <div class="nav-links-group">
                <a href="/home" class="nav-link">Inicio</a>
                <a href="/hechos" class="nav-link">Explorar</a>
                <a href="/crear" class="nav-link">Reportar</a>
                <a href="/estadisticas" class="nav-link">Estadísticas</a>
            </div>
        </nav>

        <!-- RIGHT -->
        <div class="header-right">
            <div class="auth-section">
                <#if username??>
                    <div class="user-profile">
                        <div class="user-info">
                            <span class="user-name">
                                <i data-feather="user" class="icon-sm"></i> ${username}
                            </span>
                            <#if rolUsuario?? && rolUsuario == "ADMINISTRADOR">
                                <span class="badge-admin">ADMIN</span>
                            </#if>
                        </div>

                        <div class="user-actions">
                            <#if rolUsuario?? && rolUsuario == "ADMINISTRADOR">
                                <a href="/admin/panel" class="btn-header btn-header-primary" title="Panel de Control">
                                    <i data-feather="settings" class="icon-xs"></i> Panel
                                </a>
                            </#if>
                            <a href="/logout" class="btn-header btn-header-outline" title="Cerrar sesión">
                                <i data-feather="log-out" class="icon-xs"></i> Salir
                            </a>
                        </div>
                    </div>
                <#else>
                    <div class="guest-actions">
                        <a href="/login" class="btn-header btn-header-primary">Iniciar Sesión</a>
                    </div>
                </#if>
            </div>

            <!-- ICONO HAMBURGUESA (MOBILE, A LA DERECHA) -->
            <button class="hamburger" id="hamburgerBtn">
                <i data-feather="menu"></i>
            </button>
        </div>

    </div>

    <!-- MENÚ MÓVIL -->
    <nav class="mobile-menu" id="mobileMenu">
        <a href="/home" class="mobile-link">Inicio</a>
        <a href="/hechos" class="mobile-link">Explorar</a>
        <a href="/crear" class="mobile-link">Reportar</a>
        <a href="/estadisticas" class="mobile-link">Estadísticas</a>
    </nav>
</header>


<!-- JS DEL NAVBAR -->
<script>
    feather.replace();

    const btn = document.getElementById('hamburgerBtn');
    const menu = document.getElementById('mobileMenu');

    btn.addEventListener('click', () => {
        menu.classList.toggle('open');
    });
</script>

</body>
</html>

