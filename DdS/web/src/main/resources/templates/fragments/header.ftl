<#-- header.ftl: cabecera común-->
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>${pageTitle!"MetaMapa"?html}</title>

    <!-- CSS primero -->
    <link rel="stylesheet" href="/css/style.css">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossorigin=""/>

    <!-- Scripts después -->
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
        <a href="/" class="brand" style="font-family: 'Georgia', serif;">MetaMapa</a>
        <nav class="site-nav">
            <div>
                <a href="/hechos" class="nav-link">Explorar</a>
                <a href="/crear" class="nav-link">Reportar</a>
                <a href="/estadisticas" class="nav-link">Estadísticas</a>
                <#if username??>
                    <p class="nav-link" onclick="cerrarSesion()">Cerrar sesión</p>
                    <p class="nav-link">Bienvenido, ${username}</p>
                <#else>
                    <a href="/login" class="nav-link">Iniciar sesión</a>
                </#if>
            </div>
        </nav>
    </div>
</header>
<script src="https://unpkg.com/feather-icons"></script>
<script>
    feather.replace();
</script>
<script>
    async function cerrarSesion() {

        await fetch('/logout', { method: 'GET' });

        location.reload();
    }
</script>