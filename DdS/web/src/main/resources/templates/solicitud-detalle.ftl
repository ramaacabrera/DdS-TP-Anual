<script src="/js/solicitud-detalle.js" defer></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<#assign pageTitle = "Detalle de Solicitud">
<#assign additionalCss = ["/css/styleSolicitudes.css"]>

<#assign content>
    <div class="container">
        <div class="header">
            <a href="/admin/solicitudes/eliminacion" class="header-link back-link">&larr; Volver a Solicitudes</a>
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
        <div class="info-card" style="margin-bottom: 20px;">
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
            <div class="info-card small">
                <p class="card-subtitle">SOLICITANTE</p>
                <p>
                    <#if solicitud.usuarioId??>
                        ${solicitud.usuarioId.username}
                    <#else>
                        Anónimo
                    </#if>
                </p>
            </div>

            <div class="info-card small">
                <p class="card-subtitle">ESTADO ACTUAL</p>
                <p>
                    <span class="estado-badge estado-${(solicitud.estado!solicitud.estado!'PENDIENTE')?lower_case}">
                        ${(solicitud.estado!solicitud.estado!'PENDIENTE')}
                    </span>
                </p>
            </div>
        </div>

        <!-- Cambios si es modificacion -->
        <#if solicitud.cambios??>
            <div class="info-card full-width">
                <div class="card-header-with-icon">
                    <h3 class="card-subtitle">CAMBIOS PROPUESTOS (COMPARATIVA)</h3>
                    <small style="color: var(--text-light);">Puedes editar el valor de la derecha antes de aceptar.</small>
                </div>

                <div class="cambios-container">
                    <#list solicitud.cambios as cambio>
                        <div class="cambio-row" data-campo="${cambio.campo}">
                            <div class="cambio-header">
                                <span class="campo-nombre">${cambio.campo?upper_case}</span>
                            </div>

                            <div class="comparacion-body">
                                <div class="valor-box anterior">
                                    <span class="badge-mini original">Valor Actual</span>
                                    <div class="valor-content">
                                        ${cambio.anterior!"<em>(Vacío)</em>"}
                                    </div>
                                </div>

                                <div class="flecha-indicadora">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
                                </div>

                                <div class="valor-box nuevo">
                                    <span class="badge-mini propuesto">Valor Nuevo (Editable)</span>

                                    <#if cambio.campo == "descripcion">
                                        <textarea class="form-input input-modificado" rows="4">${cambio.nuevo!''}</textarea>
                                    <#elseif cambio.campo == "fechaDeAcontecimiento">
                                        <input type="text" class="form-input input-modificado" value="${cambio.nuevo!''}">
                                    <#else>
                                        <input type="text" class="form-input input-modificado" value="${cambio.nuevo!''}">
                                    </#if>
                                </div>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>
        </#if>
        <!-- Justificación -->
        <div class="info-card full-width">
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

    </script>
</#assign>

<#include "layout.ftl">