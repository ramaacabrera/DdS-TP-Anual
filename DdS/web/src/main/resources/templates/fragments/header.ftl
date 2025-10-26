<#-- header.ftl: cabecera común-->
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>${pageTitle!"MetaMapa"?html}</title>
    <script src="https://unpkg.com/feather-icons"></script>
    <!-- CSS principal (desde src/main/resources/public/css/style.css) -->
    <link rel="stylesheet" href="/css/style.css">

    <!-- Leaflet CSS (necesario en head para evitar flash sin estilos en el mapa) -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
          integrity="sha256-sA+e2e9k1o7f6x5G6kq8jJg1q3nq9xM8yL9w8f5fC80="
          crossorigin=""/>
</head>
<body>
<header class="site-header">
    <div class="header-inner">
        <a href="/" class="brand">MetaMapa</a>
        <nav class="site-nav">
            <a href="/explorar" class="nav-link">Explorar</a>
            <a href="/crear" class="nav-link">Reportar</a>
            <a href="/estadisticas" class="nav-link">Estadísticas</a>
        </nav>
    </div>
</header>