<#assign pageTitle = "Gestión de Solicitudes">
<#assign additionalCSS>
    <link rel="stylesheet" href="/css/styleSolicitudes.css">
</#assign>

<#assign content>
    <div class="container">

        <div class="header">
            <a href="/home" class="header-link back-link">&larr; Volver al Inicio</a>
        </div>

        <h1 class="main-title">Solicitudes de Eliminación</h1>

        <!-- Resumen de solicitudes -->
        <div class="grid-metadata" style="margin-bottom: 30px;">
            <div class="card">
                <p class="card-subtitle">🗑️ SOLICITUDES DE ELIMINACIÓN</p>
                <p style="font-size: 1.5rem; font-weight: bold; color: var(--primary-color);">
                    ${solicitudesEliminacion?size}
                </p>
            </div>
        </div>

        <!-- Línea divisoria gris -->
        <hr style="border: none; height: 1px; background-color: var(--border-color); margin: 30px 0;">

        <!-- Lista de solicitudes -->
        <#if solicitudesEliminacion?size gt 0>
            <div class="list column gap-16">
                <#list solicitudesEliminacion as solicitud>
                    <@solicitudCard solicitud=solicitud />
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <div class="empty-state-icon">✅</div>
                <h3>No hay solicitudes pendientes</h3>
                <p>Todas las solicitudes han sido procesadas</p>
            </div>
        </#if>
    </div>
</#assign>

<!-- MACRO para tarjeta de solicitud -->
<#macro solicitudCard solicitud>
    <#assign estado = solicitud.estadoSolicitudEliminacion!"PENDIENTE">

    <div class="solicitud-card" style="
        background: var(--bg-white);
        border: 1px solid var(--border-color);
        border-radius: var(--radius);
        padding: 20px;
        margin-bottom: 16px;
        box-shadow: var(--shadow);
        transition: var(--transition);
    ">
        <div class="solicitud-header">
            <div class="solicitud-info">
                <h3 class="solicitud-titulo">
                    🗑️ Solicitud de Eliminación
                    <small style="color: var(--muted-color); font-weight: normal;">
                        #${solicitud.id?substring(0, 36)}
                    </small>
                </h3>
                <p class="solicitud-hecho">
                    <strong>Hecho:</strong> ${solicitud.hechoTitulo!"Sin título"}
                </p>
                <p class="solicitud-usuario">
                    <strong>Usuario:</strong>
                    <#if solicitud.usuario??>
                        ${solicitud.usuario.username!""}
                    <#else>
                        Anónimo
                    </#if>
                </p>
            </div>
            <div class="solicitud-meta">
                <span class="estado-badge estado-${estado?lower_case}">
                    ${obtenerTextoEstado(estado)}
                </span>
            </div>
        </div>

        <div class="solicitud-body" style="margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--border-color);">
            <div class="justificacion">
                <strong>Justificación:</strong>
                <p style="
                    margin: 8px 0 0 0;
                    padding: 12px;
                    background: var(--bg-light);
                    border-radius: 6px;
                    border-left: 4px solid var(--primary-color);
                    line-height: 1.5;
                    white-space: pre-wrap;
                ">
                    ${solicitud.justificacion!"Sin justificación"}
                </p>
            </div>

            <div class="solicitud-actions" style="
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-top: 16px;
            ">
                <a href="/admin/solicitudes/eliminacion/${solicitud.id}" class="btn btn-primary" style="
                    display: inline-block;
                    padding: 8px 16px;
                    background: var(--primary-color);
                    color: white;
                    border-radius: 6px;
                    text-decoration: none;
                    font-weight: 500;
                    transition: var(--transition);
                    border: 1px solid transparent;
                ">
                    Ver Detalles
                </a>
                <#if estado == "PENDIENTE">
                    <span class="pending-badge" style="
                        color: #856404;
                        font-weight: 600;
                        font-size: 0.9rem;
                    ">
                        ⏳ Pendiente de revisión
                    </span>
                </#if>
            </div>
        </div>
    </div>
</#macro>

<#function obtenerTextoEstado estado>
    <#if estado??>
        <#switch estado>
            <#case "PENDIENTE">
                <#return "Pendiente">
            <#case "ACEPTADA">
                <#return "Aceptada">
            <#case "RECHAZADA">
                <#return "Rechazada">
            <#default>
                <#return estado>
        </#switch>
    <#else>
        <#return "Desconocido">
    </#if>
</#function>

<#include "layout.ftl">