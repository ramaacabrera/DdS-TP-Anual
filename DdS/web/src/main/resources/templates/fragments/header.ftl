<#-- header.ftl: cabecera común-->
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>${pageTitle!"MetaMapa"?html}</title>

    <!-- CSS -->
    <link rel="stylesheet" href="/css/style.css">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossorigin=""/>

    <!-- Scripts -->
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" crossorigin=""></script>
    <script src="https://unpkg.com/feather-icons"></script>

    <link rel="icon" href="data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>🗺️</text></svg>">

    <#if additionalCss??>
        <#list additionalCss as css>
            <link rel="stylesheet" href="${css}">
        </#list>
    </#if>

    <style>
        /* Variables de colores y fuentes */
        :root {
            --primary: #1d7858;
            --primary-light: #1d7858;
            --secondary: #34a853;
            --dark: #333;
            --light: #f8f9fa;
            --gray: #6c757d;
            --border-radius: 8px;
            --transition: all 0.3s ease;
        }

        /* Header mejorado */
        .site-header {
            background-color: white;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            position: sticky;
            top: 0;
            z-index: 1000;
            padding: 0;
        }

        .header-inner {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            height: 70px;
        }

        .brand {
            font-size: 1.8rem;
            font-weight: 700;
            color: var(--primary);
            text-decoration: none;
            display: flex;
            align-items: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .brand::after {
            content: "MetaMapa";
        }

        .site-nav {
            display: flex;
        }

        .site-nav > div {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .nav-link {
            text-decoration: none;
            color: var(--dark);
            font-weight: 500;
            transition: var(--transition);
            padding: 8px 15px;
            border-radius: var(--border-radius);
            cursor: pointer;
            white-space: nowrap;
        }

        .nav-link:hover {
            color: var(--primary);
            background-color: rgba(44, 90, 160, 0.05);
        }

        .auth-section {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-left: 20px;
            padding-left: 20px;
            border-left: 1px solid #eee;
        }

        .btn {
            padding: 8px 20px;
            border-radius: var(--border-radius);
            font-weight: 500;
            cursor: pointer;
            transition: var(--transition);
            border: none;
            font-size: 0.9rem;
        }

        .btn-outline {
            background: transparent;
            border: 1px solid var(--primary);
            color: var(--primary);
        }

        .btn-outline:hover {
            background: var(--primary);
            color: white;
        }

        .btn-primary {
            background: var(--primary);
            color: white;
        }

        .btn-primary:hover {
            background: var(--primary-light);
        }

        .user-welcome {
            color: var(--dark);
            font-weight: 500;
            margin: 0;
            padding: 8px 15px;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .header-inner {
                flex-direction: column;
                height: auto;
                padding: 15px 20px;
            }

            .site-nav > div {
                flex-wrap: wrap;
                justify-content: center;
                margin-top: 15px;
            }

            .auth-section {
                border-left: none;
                padding-left: 0;
                margin-left: 0;
                justify-content: center;
                width: 100%;
                margin-top: 10px;
            }
        }
    </style>
</head>
<body>
<header class="site-header">
    <div class="header-inner">
        <a href="/" class="brand"></a>
        <nav class="site-nav">
            <div>
                <a href="/home" class="nav-link">Inicio</a>
                <a href="/hechos" class="nav-link">Explorar</a>
                <a href="/crear" class="nav-link">Reportar</a>
                <a href="/estadisticas" class="nav-link">Estadísticas</a>

                <#if username??>
                    <div class="auth-section">
                        <p class="user-welcome">Bienvenido, ${username}</p>
                        <a href="/logout" class="btn btn-outline">Cerrar sesión</a>
                    </div>
                <#else>
                    <div class="auth-section">
                        <a href="/login" class="btn btn-outline">Ingresar</a>
                    </div>
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