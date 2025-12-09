<#-- header.ftl: cabecera común -->
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>${pageTitle!"MetaMapa"?html}</title>

    <link rel="stylesheet" href="/css/style.css">

    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossorigin=""/>
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" crossorigin=""></script>
    <script src="https://unpkg.com/feather-icons"></script>

    <link rel="icon" href="data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>🗺️</text></svg>">

    <#if additionalCss??>
        <#list additionalCss as css>
            <link rel="stylesheet" href="${css}">
        </#list>
    </#if>
</head>
<body>

<header class="site-header">
    <div class="header-inner">
        <div class="header-left">
            <a href="/" class="brand">
                <span class="brand-icon">🗺️</span> MetaMapa
            </a>
        </div>

        <nav class="header-center">
            <div class="nav-links-group">
                <a href="/home" class="nav-link">Inicio</a>
                <a href="/hechos" class="nav-link">Explorar</a>
                <a href="/crear" class="nav-link">Reportar</a>
                <a href="/estadisticas" class="nav-link">Estadísticas</a>
            </div>
        </nav>

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
                                <a href="/admin/panel" class="btn-header btn-sm btn-primary" title="Panel de Control">
                                    <i data-feather="settings" class="icon-xs"></i> Panel
                                </a>
                            </#if>
                            <a href="/logout" class="btn-header btn-sm btn-outline-danger" title="Cerrar sesión">
                                <i data-feather="log-out" class="icon-xs"></i> Salir
                            </a>
                        </div>
                    </div>
                <#else>
                    <div class="guest-actions">
                        <a href="/login" class="btn-header btn-primary btn-sm">Iniciar Sesión</a>
                    </div>
                </#if>
            </div>
        </div>
    </div>
</header>

<script>
    feather.replace();
</script>