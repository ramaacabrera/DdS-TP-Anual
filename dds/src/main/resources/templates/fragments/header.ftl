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

    <!--<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
          integrity="sha256-sA+e2e9k1o7f6x5G6kq8jJg1q3nq9xM8yL9w8f5fC80="
          crossorigin=""/> -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
          crossorigin=""/>
    <link rel="icon" href="data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>🗺️</text></svg>">

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
<!--doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>${pageTitle?if_exists?html!("MetaMapa")}</title>

    <link rel="stylesheet" href="/css/style.css">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
          integrity="sha256-sA+e2e9k1o7f6x5G6kq8jJg1q3nq9xM8yL9w8f5fC80="
          crossorigin=""/>

    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
            integrity="sha256-o9N1j6Ux5XvFQksZfY4V1S0r1w6p6bqbgXfQ/h17ZjY="
            crossorigin=""></script>


    <#-- por ahora no tengo  esto-->
    <script defer src="/js/hecho.js"></script>
</head>
<body>
<header class="site-header">
    <div class="container">
        <a href="/" class="brand">MetaMapa</a>
        <nav class="main-nav">
            <a href="/explorar">Explorar</a>
            <a href="/crear">Reportar</a>
            <a href="/estadisticas">Estadísticas</a>
        </nav>
    </div>
</header>
<main class="container"style="max-width:1000px; margin:30px auto; padding:30px;"-->
    <!--queda abierto para cerrarlo en el footer -->
