<#assign pageTitle = "Gestión de Solicitudes">
<#assign additionalCSS>
    <link rel="stylesheet" href="/css/styleSolicitudes.css">
</#assign>

<#assign content>
    <div class="container">


        <div class="header">
            <a href="/home" class="header-link back-link">&larr; Volver al Inicio</a>
        </div>

        <h1 class="main-title">Solicitudes Pendientes</h1>

        <!-- Resumen de solicitudes -->
        <div class="grid-metadata" style="margin-bottom: 30px;">
            <div class="card">
                <p class="card-subtitle">🗑️ SOLICITUDES DE ELIMINACIÓN</p>
                <p style="font-size: 1.5rem; font-weight: bold; color: var(--primary-color);">
                    ${solicitudesEliminacion?size}
                </p>
            </div>
            <div class="card">
                <p class="card-subtitle">✏️ SOLICITUDES DE MODIFICACIÓN</p>
                <p style="font-size: 1.5rem; font-weight: bold; color: var(--primary-color);">
                    ${solicitudesModificacion?size}
                </p>
            </div>
        </div>

        <!-- Tabs para diferentes tipos de solicitudes -->
        <div class="tabs-nav">
            <button class="tab-btn active" onclick="showTab('eliminacion', this)">Eliminación (${solicitudesEliminacion?size})</button>
            <button class="tab-btn" onclick="showTab('modificacion', this)">Modificación (${solicitudesModificacion?size})</button>
        </div>

        <!-- Solicitudes de Eliminación -->
        <div id="eliminacion" class="tab-content active">
            <#if solicitudesEliminacion?size gt 0>
                <div class="list column gap-16">
                    <#list solicitudesEliminacion as solicitud>
                        <@solicitudCard
                        solicitud=solicitud
                        tipo="eliminacion"
                        estado=solicitud.estadoSolicitudEliminacion! />
                    </#list>
                </div>
            <#else>
                <div class="empty-state">
                    <div class="empty-state-icon">✅</div>
                    <h3>No hay solicitudes de eliminación pendientes</h3>
                    <p>Todas las solicitudes han sido procesadas</p>
                </div>
            </#if>
        </div>

        <!-- Solicitudes de Modificación -->
        <div id="modificacion" class="tab-content" style="display:none;">
            <#if solicitudesModificacion?size gt 0>
                <div class="list column gap-16">
                    <#list solicitudesModificacion as solicitud>
                        <@solicitudCard
                        solicitud=solicitud
                        tipo="modificacion"
                        estado=solicitud.estadoSolicitudModificacion! />
                    </#list>
                </div>
            <#else>
                <div class="empty-state">
                    <div class="empty-state-icon">✅</div>
                    <h3>No hay solicitudes de modificación pendientes</h3>
                    <p>Todas las solicitudes han sido procesadas</p>
                </div>
            </#if>
        </div>
    </div>
</#assign>

<#-- Macro para tarjeta de solicitud -->
<#macro solicitudCard solicitud tipo estado>
    <div class="solicitud-card">
        <div class="solicitud-header">
            <div class="solicitud-info">
                <h3 class="solicitud-titulo">
                    <#if tipo == "eliminacion">
                        🗑️ Solicitud de Eliminación
                    <#else>
                        ✏️ Solicitud de Modificación
                    </#if>
                    <small style="color: var(--muted-color); font-weight: normal;">
                        #${solicitud.id?substring(0, 8)}
                    </small>
                </h3>
                <p class="solicitud-hecho">
                    <strong>Hecho:</strong> ${solicitud.hechoAsociado.titulo!""}
                </p>
                <p class="solicitud-usuario">
                    <strong>Usuario:</strong>
                    <#if solicitud.usuario??>
                        ${solicitud.usuario.nombre!""} ${solicitud.usuario.apellido!""}
                    <#else>
                        Anónimo
                    </#if>
                </p>
            </div>
            <div class="solicitud-meta">
                <span class="estado-badge estado-${estado?lower_case}">
                    ${obtenerTextoEstado(estado)}
                </span>
                <span class="fecha">
                    <#if solicitud.fechaCreacion??>
                        ${solicitud.fechaCreacion?string("dd/MM/yyyy HH:mm")}
                    <#else>
                        Sin fecha
                    </#if>
                </span>
            </div>
        </div>

        <div class="solicitud-body">
            <div class="justificacion">
                <strong>Justificación:</strong>
                <p>${solicitud.justificacion!"Sin justificación"}</p>
            </div>

            <div class="solicitud-actions">
                <a href="/admin/solicitudes/${tipo}/${solicitud.id}" class="btn btn-primary">
                    Ver Detalles
                </a>
                <#if estado == "PENDIENTE">
                    <span class="pending-badge">⏳ Pendiente de revisión</span>
                </#if>
            </div>
        </div>
    </div>
</#macro>

<#function obtenerTextoEstado estado>
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
</#function>

<script>
    function showTab(tabName, element) {
        // Ocultar todos los tabs
        document.querySelectorAll('.tab-content').forEach(tab => {
            tab.style.display = 'none';
        });

        // Remover clase active de todos los botones
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.remove('active');
        });

        // Mostrar tab seleccionado y activar botón
        document.getElementById(tabName).style.display = 'block';
        element.classList.add('active');
    }
</script>

<#include "layout.ftl">