
<#assign pageTitle = "Gestión de Solicitudes de Modificación">
<#assign additionalCSS>
    <link rel="stylesheet" href="/css/styleSolicitudes.css">
</#assign>

<#assign content>
    <div class="container">

        <div class="header">
            <a href="/admin/panel" class="header-link back-link">&larr; Volver al Panel</a>
        </div>

        <h1 class="main-title">✍️ Solicitudes de Modificación</h1>

        <div class="grid-metadata" style="margin-bottom: 30px;">
            <div class="card">
                <p class="card-subtitle">✍️ SOLICITUDES PENDIENTES</p>
                <p style="font-size: 1.5rem; font-weight: bold; color: #1d7858;">
                    ${solicitudesModificacion?size}
                </p>
            </div>
        </div>

        <hr style="border: none; height: 1px; background-color: var(--border-color); margin: 30px 0;">

        <#if solicitudesModificacion?size gt 0>
            <div class="list column gap-16">
                <#list solicitudesModificacion as solicitud>
                    <@solicitudModificacionCard solicitud=solicitud />
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <div class="empty-state-icon">✅</div>
                <h3>No hay solicitudes de modificación pendientes</h3>
                <p>Todas las solicitudes de modificación han sido procesadas</p>
            </div>
        </#if>
    </div>
</#assign>

<#macro solicitudModificacionCard solicitud>
    <#assign estado = solicitud.estado!obtenerTextoEstado(solicitud.estadoModificacion!"PENDIENTE")>
    <#assign hecho = solicitud.hechoModificado!"">
    <#assign urlDetalle = "/admin/solicitudes/modificacion/${solicitud.id}">

    <div class="solicitud-card" style="
        background: var(--bg-white);
        border: 1px solid var(--border-color);
        border-left: 5px solid; /* Color distintivo para Modificación */
        border-radius: var(--radius);
        padding: 20px;
        margin-bottom: 16px;
        box-shadow: var(--shadow);
        transition: var(--transition);
    ">
        <div class="solicitud-header">
            <div class="solicitud-info">
                <h3 class="solicitud-titulo">
                    ✍️ Solicitud de Modificación
                    <small style="color: var(--muted-color); font-weight: normal;">
                        #${solicitud.id?substring(0, 36)}
                    </small>
                </h3>
                <p class="solicitud-hecho">
                    <strong>Hecho Asociado ID:</strong> ${solicitud.hechoAsociado!""}
                </p>
                <p class="solicitud-usuario">
                    <strong>Usuario:</strong>
                    <#if solicitud.usuario?? && solicitud.usuario.username??>
                        ${solicitud.usuario.username}
                    <#else>
                        Anónimo
                    </#if>
                </p>
            </div>
        </div>

        <div class="solicitud-body" style="margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--border-color);">

            <#if hecho.camposModificados??>
                <div class="cambios" style="margin-bottom: 16px; background: var(--bg-light); padding: 10px; border-radius: 6px;">
                    <strong>📝 Cambios Propuestos:</strong>
                    <ul>
                        <#-- ITERAR sobre los campos del HechoModificado.
                            NOTA: Esto asume que HechoModificado expone los cambios de forma iterable (ej: List<Cambio> o Map)
                            o que tienes una estructura para mostrar la diferencia (ej: títuloNuevo, fechaNueva, etc.)
                            Si el objeto hechoModificado contiene las propiedades (título, fecha, etc.) estas deben compararse
                            con las propiedades originales del hecho. Aquí simularemos el listado de cambios.
                        -->
                        <#-- Ejemplo simulado, ajusta según la estructura real de tu HechoModificado en el service -->
                        <#-- Asumiendo que hechoModificado tiene una lista iterable de 'cambios' -->
                        <#list hecho.cambios![] as cambio>
                            <li>
                                <strong>${cambio.campo}:</strong> "${cambio.valorAnterior!""}"
                                &rarr;
                                <span style="font-weight: bold; color: green;">"${cambio.valorNuevo!""}"</span>
                            </li>
                        </#list>
                    </ul>
                    <#if hecho.cambios?? && hecho.cambios?size == 0>
                        <p>No se especificaron cambios detallados (verifique el detalle).</p>
                    </#if>
                </div>
            </#if>

            <div class="justificacion">
                <strong>Justificación:</strong>
                <p style="
                    margin: 8px 0 0 0;
                    padding: 12px;
                    background: var(--bg-light);
                    border-radius: 6px;
                    border-left: 4px solid;
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
                <a href="${urlDetalle}" class="btn btn-primary" style="
                    display: inline-block;
                    padding: 8px 16px;
                    background: #1d7858;
                    color: white;
                    border-radius: 6px;
                    text-decoration: none;
                    font-weight: 500;
                    transition: var(--transition);
                    border: 1px solid transparent;
                ">
                    Ver Detalles
                </a>
            </div>
        </div>
    </div>
</#macro>

<#-- Función de apoyo para estado (aunque el modelo de dominio usa un Enum, a veces el service lo simplifica a String) -->
<#function obtenerTextoEstado estado>
    <#if estado??>
        <#switch estado?upper_case>
            <#case "PENDIENTE"> <#return "Pendiente">
            <#case "ACEPTADA">  <#return "Aceptada">
            <#case "RECHAZADA"> <#return "Rechazada">
            <#default>          <#return estado>
        </#switch>
    <#else>
        <#return "Desconocido">
    </#if>
</#function>

<#include "layout.ftl">