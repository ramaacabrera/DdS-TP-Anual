<script src="/js/solicitud-detalle.js" defer></script>

<#assign pageTitle = "Detalle de Solicitud">
<#assign additionalCSS>
    <link rel="stylesheet" href="/css/styleSolicitudes.css">
</#assign>

<#assign content>
    <div class="container">
        <div class="header">
            <a href="/admin/solicitudes" class="header-link back-link">&larr; Volver a Solicitudes</a>
        </div>

        <div class="title-actions-container">
            <div class="title-content">
                <h1 class="main-title">
                    <#if tipo == "eliminacion">
                        Solicitud de Eliminación
                    <#else>
                        Solicitud de Modificación
                    </#if>
                </h1>
                <span class="estado-badge estado-${(solicitud.estado!solicitud.estado!'PENDIENTE')?lower_case}">
                    ${(solicitud.estado!solicitud.estado!'PENDIENTE')}
                </span>
            </div>
        </div>

        <!-- Información del Hecho -->
        <div class="card" style="margin-bottom: 20px;">
            <h3 class="card-subtitle">INFORMACIÓN DEL HECHO</h3>
            <div class="grid-metadata">
                <div class="info-item">
                    <strong>Título:</strong> ${hecho.titulo!"No disponible"}
                </div>
                <div class="info-item">
                    <strong>Categoría:</strong> ${hecho.categoria!"No disponible"}
                </div>
                <div class="info-item">
                    <strong>Fecha de carga:</strong>
                    ${hecho.fechaDeCargaFormateada!"Sin fecha"}
                </div>
                <div class="info-item">
                    <strong>Estado:</strong> ${hecho.estadoHecho!"No disponible"}
                </div>
            </div>
        </div>

        <!-- Información de la Solicitud -->
        <div class="grid-metadata">
            <div class="card">
                <p class="card-subtitle">SOLICITANTE</p>
                <p>
                    <#if solicitud.usuario??>
                        <#if solicitud.usuario?is_hash>
                            ${solicitud.usuario.nombre!""} ${solicitud.usuario.apellido!""}
                        <#else>
                            ${solicitud.usuario!""}
                        </#if>
                    <#else>
                        Anónimo
                    </#if>
                </p>
            </div>

            <div class="card">
                <p class="card-subtitle">ESTADO ACTUAL</p>
                <p>
                    <span class="estado-badge estado-${(solicitud.estado!solicitud.estado!'PENDIENTE')?lower_case}">
                        ${(solicitud.estado!solicitud.estado!'PENDIENTE')}
                    </span>
                </p>
            </div>
        </div>

        <!-- Justificación -->
        <div class="card" style="grid-column: 1 / -1;">
            <p class="card-subtitle">JUSTIFICACIÓN</p>
            <div class="justificacion">
                <p>${solicitud.justificacion!"Sin justificación proporcionada"}</p>
            </div>
        </div>

        <!-- Acciones (solo si está pendiente) -->
        <#if (solicitud.estado!solicitud.estado!'PENDIENTE') == 'PENDIENTE'>
            <div class="acciones-container">
                <h3>Acciones</h3>
                <div class="acciones-botones">
                    <button onclick="aceptarSolicitud()" class="btn btn-success">
                        Aceptar Solicitud
                    </button>
                    <button onclick="rechazarSolicitud()" class="btn btn-cancel">
                        Rechazar Solicitud
                    </button>
                </div>
            </div>
        </#if>
    </div>

    <!-- Script para pasar variables del backend al frontend -->
    <script>
        // Variables globales disponibles para el JS externo
        window.solicitudData = {
            id: '${solicitud.id!"0"}',
            tipo: '${tipo!"eliminacion"}'
        };

        function procesarSolicitud(accion) {
            fetch(window.location.pathname, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ accion })
            }).then(() => window.location.href="/admin/solicitudes");
        }

        // Funciones directas sin confirmación
        function aceptarSolicitud() {
            procesarSolicitud('ACEPTADA');
        }

        function rechazarSolicitud() {
            procesarSolicitud('RECHAZADA');
        }
    </script>
</#assign>

<#include "layout.ftl">

