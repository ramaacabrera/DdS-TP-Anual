<#assign pageTitle = "Gestión de Solicitudes">
<#assign additionalCSS>
    <link rel="stylesheet" href="/css/styleSolicitudes.css">
</#assign>

<#assign content>
    <div class="solicitudes-container">
        <!-- Header -->
        <div class="solicitudes-header">
            <a href="/admin" class="back-button">
                <span class="back-icon">&larr;</span>
                Volver al Panel de Administración
            </a>
            <h1 class="solicitudes-title">Solicitudes Pendientes</h1>
        </div>

        <!-- Estadísticas -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">🗑️</div>
                <div class="stat-content">
                    <span class="stat-number">${solicitudesEliminacion?size}</span>
                    <span class="stat-label">Solicitudes de Eliminación</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">✏️</div>
                <div class="stat-content">
                    <span class="stat-number">${solicitudesModificacion?size}</span>
                    <span class="stat-label">Solicitudes de Modificación</span>
                </div>
            </div>
        </div>

        <!-- Tabs -->
        <div class="tabs-container">
            <div class="tabs-header">
                <button class="tab-button active" data-tab="eliminacion">
                    <span class="tab-icon">🗑️</span>
                    Eliminación
                    <span class="tab-badge">${solicitudesEliminacion?size}</span>
                </button>
                <button class="tab-button" data-tab="modificacion">
                    <span class="tab-icon">✏️</span>
                    Modificación
                    <span class="tab-badge">${solicitudesModificacion?size}</span>
                </button>
            </div>

            <!-- Contenido de Tabs -->
            <div class="tab-content active" id="tab-eliminacion">
                <#if solicitudesEliminacion?size gt 0>
                    <div class="solicitudes-grid">
                        <#list solicitudesEliminacion as solicitud>
                            <@solicitudCard
                            solicitud=solicitud
                            tipo="eliminacion"
                            estado=solicitud.estadoSolicitudEliminacion!
                            />
                        </#list>
                    </div>
                <#else>
                    <@emptyState
                    icon="✅"
                    title="No hay solicitudes de eliminación"
                    message="Todas las solicitudes han sido procesadas"
                    />
                </#if>
            </div>

            <div class="tab-content" id="tab-modificacion">
                <#if solicitudesModificacion?size gt 0>
                    <div class="solicitudes-grid">
                        <#list solicitudesModificacion as solicitud>
                            <@solicitudCard
                            solicitud=solicitud
                            tipo="modificacion"
                            estado=solicitud.estadoSolicitudModificacion!
                            />
                        </#list>
                    </div>
                <#else>
                    <@emptyState
                    icon="✅"
                    title="No hay solicitudes de modificación"
                    message="Todas las solicitudes han sido procesadas"
                    />
                </#if>
            </div>
        </div>
    </div>
</#assign>

<#-- Macro para Estado Vacío -->
<#macro emptyState icon title message>
    <div class="empty-state">
        <div class="empty-state-icon">${icon}</div>
        <h3 class="empty-state-title">${title}</h3>
        <p class="empty-state-message">${message}</p>
    </div>
</#macro>

<#-- Macro para Tarjeta de Solicitud Mejorada -->
<#macro solicitudCard solicitud tipo estado>
    <div class="solicitud-card" data-id="${solicitud.id}" data-type="${tipo}">
        <div class="solicitud-card-header">
            <div class="solicitud-type-badge">
                <#if tipo == "eliminacion">
                    <span class="type-icon">🗑️</span>
                    Eliminación
                <#else>
                    <span class="type-icon">✏️</span>
                    Modificación
                </#if>
            </div>
            <div class="solicitud-meta">
                <span class="solicitud-id">#${solicitud.id?substring(0, 8)}</span>
                <span class="estado-badge estado-${estado?lower_case}">
                    ${obtenerTextoEstado(estado)}
                </span>
            </div>
        </div>

        <div class="solicitud-card-body">
            <h3 class="hecho-title">${solicitud.hechoAsociado.titulo!""}</h3>

            <div class="solicitud-info-grid">
                <div class="info-item">
                    <span class="info-label">👤 Usuario:</span>
                    <span class="info-value">
                        <#if solicitud.usuario??>
                            ${solicitud.usuario.nombre!""} ${solicitud.usuario.apellido!""}
                        <#else>
                            Anónimo
                        </#if>
                    </span>
                </div>
                <div class="info-item">
                    <span class="info-label">📅 Fecha:</span>
                    <span class="info-value">
                        <#if solicitud.fechaCreacion??>
                            ${solicitud.fechaCreacion?string("dd/MM/yyyy HH:mm")}
                        <#else>
                            Sin fecha
                        </#if>
                    </span>
                </div>
            </div>

            <div class="justificacion-section">
                <h4 class="justificacion-title">Justificación</h4>
                <p class="justificacion-text">${solicitud.justificacion!"Sin justificación proporcionada"}</p>
            </div>
        </div>

        <div class="solicitud-card-footer">
            <a href="/admin/solicitudes/${tipo}/${solicitud.id}" class="action-button primary">
                <span class="button-icon">👁️</span>
                Ver Detalles
            </a>
            <#if estado == "PENDIENTE">
                <span class="pending-indicator">
                    <span class="indicator-dot"></span>
                    Pendiente de revisión
                </span>
            </#if>
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