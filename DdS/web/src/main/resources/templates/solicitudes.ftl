<#assign pageTitle = "Solicitudes de Eliminación">
<#assign additionalCSS>
    <link rel="stylesheet" href="/css/styleSolicitudes.css">
</#assign>

<#assign pageTitle = "Gestión de Solicitudes">
<#assign additionalCSS>
    <link rel="stylesheet" href="/css/styleSolicitudes.css">
</#assign>

<#assign content>
    <div class="container">

        <div class="header">
            <a href="/home" class="header-link back-link">← Volver al Inicio</a>
        </div>

        <h1 class="main-title">Solicitudes de Eliminación</h1>

        <!-- Resumen -->
        <div class="grid-metadata" style="margin-bottom: 30px;">
            <div class="card">
                <p class="card-subtitle">Solicitudes de eliminación</p>
                <p class="card-counter">
                    ${solicitudesEliminacion?size}
                </p>
            </div>
        </div>

        <!-- Lista -->
        <#if solicitudesEliminacion?size gt 0>
            <div class="list column gap-16">
                <#list solicitudesEliminacion as solicitud>
                    <@solicitudCard solicitud=solicitud />
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>No hay solicitudes pendientes</h3>
                <p>Todas las solicitudes han sido procesadas.</p>
            </div>
        </#if>

    </div>
</#assign>

<!-- CARD MACRO -->
<#macro solicitudCard solicitud>
    <#assign estado = solicitud.estadoSolicitudEliminacion!"PENDIENTE">

    <a class="solicitud-card-link"
       href="/admin/solicitudes/eliminacion/${solicitud.id}"
       style="text-decoration: none; color: inherit;">

        <div class="solicitud-card" data-hecho-id="${solicitud.hechoId!""}">

            <div class="solicitud-header">
                <div class="solicitud-info">
                    <h3 class="solicitud-titulo">
                        Solicitud de Eliminación
                        <small class="codigo">#${solicitud.id?substring(0,8)}</small>
                    </h3>

                    <p class="solicitud-hecho">
                        <strong>Hecho:</strong>
                        <span class="hecho-titulo">${solicitud.hechoTitulo!"Sin título"}</span>
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

            <div class="solicitud-body">
                <div class="justificacion">
                    <strong>Justificación:</strong>
                    <p>${solicitud.justificacion!"Sin justificación"}</p>
                </div>

                <div class="solicitud-actions">
                    <span class="btn btn-primary">Ver detalles</span>

                    <#if estado == "PENDIENTE">
                        <span class="pending-badge">Pendiente de revisión</span>
                    </#if>
                </div>
            </div>

        </div>
    </a>
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