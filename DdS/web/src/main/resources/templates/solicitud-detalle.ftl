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
                <span class="estado-badge estado-${(solicitud.estadoSolicitudEliminacion!solicitud.estadoSolicitudModificacion!'PENDIENTE')?lower_case}">
                    ${(solicitud.estadoSolicitudEliminacion!solicitud.estadoSolicitudModificacion!'PENDIENTE')}
                </span>
            </div>
        </div>

        <!-- Información del Hecho -->
        <div class="card" style="margin-bottom: 20px;">
            <h3 class="card-subtitle">INFORMACIÓN DEL HECHO</h3>
            <div class="grid-metadata">
                <div class="info-item">
                    <strong>Título:</strong> ${hecho.titulo!""}
                </div>
                <div class="info-item">
                    <strong>Categoría:</strong> ${hecho.categoria!""}
                </div>
                <div class="info-item">
                    <strong>Fecha de carga:</strong>
                    <#if hecho.fechaDeCarga??>
                        ${hecho.fechaDeCarga?string("dd/MM/yyyy HH:mm")}
                    <#else>
                        Sin fecha
                    </#if>
                </div>
                <div class="info-item">
                    <strong>Estado:</strong> ${hecho.estadoHecho!""}
                </div>
            </div>
        </div>

        <!-- Información de la Solicitud -->
        <div class="grid-metadata">
            <div class="card">
                <p class="card-subtitle">SOLICITANTE</p>
                <p>
                    <#if solicitud.usuario??>
                        ${solicitud.usuario.nombre!""} ${solicitud.usuario.apellido!""}
                    <#else>
                        Anónimo
                    </#if>
                </p>
            </div>

            <div class="card">
                <p class="card-subtitle">FECHA DE SOLICITUD</p>
                <p>
                    <#if solicitud.fechaCreacion??>
                        ${solicitud.fechaCreacion?string("dd/MM/yyyy HH:mm")}
                    <#else>
                        Sin fecha
                    </#if>
                </p>
            </div>

            <div class="card">
                <p class="card-subtitle">ESTADO ACTUAL</p>
                <p>
                    <span class="estado-badge estado-${(solicitud.estadoSolicitudEliminacion!solicitud.estadoSolicitudModificacion!'PENDIENTE')?lower_case}">
                        ${(solicitud.estadoSolicitudEliminacion!solicitud.estadoSolicitudModificacion!'PENDIENTE')}
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
        <#if (solicitud.estadoSolicitudEliminacion!solicitud.estadoSolicitudModificacion!'PENDIENTE') == 'PENDIENTE'>
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
            id: '${solicitud.id_solicitud!"0"}', // Cambiado a id_solicitud
            tipo: '${tipo!"eliminacion"}' // 'eliminacion' o 'modificacion'
        };

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