<#assign pageTitle = "Gestión de Solicitudes de Eliminación">
<#assign additionalCss = ["/css/styleSolicitudes.css"]>

<#assign content>
    <div class="container">

        <div class="header">
            <a href="/admin/panel" class="header-link back-link">&larr; Volver al Panel</a>
        </div>

        <h1 class="main-title">Solicitudes de Eliminación</h1>

        <!-- Filtros por estado - Ahora se comunican con el backend -->
        <div class="filtros-estado" style="margin-bottom: 30px;">
            <div class="filtros-header" style="margin-bottom: 15px;">
                <h3 style="font-size: 1.1rem; color: var(--text-color); margin: 0 0 10px 0;">Filtrar por estado:</h3>
            </div>

            <div class="filtros-botones" style="display: flex; gap: 12px; flex-wrap: wrap;">
                <!-- Botón para TODAS -->
                <a href="/admin/solicitudes/eliminacion?page=1&size=${(size!10)?c}"
                   class="filtro-btn filtro-todas ${(!estadoFiltro?? || estadoFiltro == '')?then('filtro-btn-activo', '')}"
                   style="
                           display: inline-flex;
                           align-items: center;
                           gap: 8px;
                           padding: 10px 20px;
                           background-color: ${(!estadoFiltro?? || estadoFiltro == '')?then('var(--primary-color)', 'var(--bg-light)')};
                           color: ${(!estadoFiltro?? || estadoFiltro == '')?then('white', 'var(--text-color)')};
                           border: 1px solid ${(!estadoFiltro?? || estadoFiltro == '')?then('var(--primary-dark)', 'var(--border-color)')};
                           border-radius: 8px;
                           text-decoration: none;
                           font-weight: 600;
                           transition: all 0.2s;
                           cursor: pointer;
                           ">

                    <span>Todas</span>
                    <span class="badge-contador" style="
                            background-color: ${(!estadoFiltro?? || estadoFiltro == '')?then('white', 'var(--primary-color)')};
                            color: ${(!estadoFiltro?? || estadoFiltro == '')?then('var(--primary-color)', 'white')};
                            font-size: 0.8rem;
                            padding: 2px 8px;
                            border-radius: 12px;
                            margin-left: 4px;
                            ">${totalTodas!solicitudesEliminacion?size}</span>
                </a>

                <!-- Botón para PENDIENTES -->
                <a href="/admin/solicitudes/eliminacion?estado=PENDIENTE&page=1&size=${(size!10)?c}"
                   class="filtro-btn filtro-pendientes ${(estadoFiltro?? && estadoFiltro == 'PENDIENTE')?then('filtro-btn-activo', '')}"
                   style="
                           display: inline-flex;
                           align-items: center;
                           gap: 8px;
                           padding: 10px 20px;
                           background-color: ${(estadoFiltro?? && estadoFiltro == 'PENDIENTE')?then('#fff3cd', 'var(--bg-light)')};
                           color: ${(estadoFiltro?? && estadoFiltro == 'PENDIENTE')?then('#856404', 'var(--text-color)')};
                           border: 1px solid ${(estadoFiltro?? && estadoFiltro == 'PENDIENTE')?then('#ffeaa7', 'var(--border-color)')};
                           border-radius: 8px;
                           text-decoration: none;
                           font-weight: 600;
                           transition: all 0.2s;
                           cursor: pointer;
                           ">

                    <span>Pendientes</span>
                    <span class="badge-contador" style="
                            background-color: ${(estadoFiltro?? && estadoFiltro == 'PENDIENTE')?then('#856404', '#fff3cd')};
                            color: ${(estadoFiltro?? && estadoFiltro == 'PENDIENTE')?then('#fff3cd', '#856404')};
                            font-size: 0.8rem;
                            padding: 2px 8px;
                            border-radius: 12px;
                            margin-left: 4px;
                            ">${totalPendientes!0}</span>
                </a>

                <!-- Botón para ACEPTADAS -->
                <a href="/admin/solicitudes/eliminacion?estado=ACEPTADA&page=1&size=${(size!10)?c}"
                   class="filtro-btn filtro-aceptadas ${(estadoFiltro?? && estadoFiltro == 'ACEPTADA')?then('filtro-btn-activo', '')}"
                   style="
                           display: inline-flex;
                           align-items: center;
                           gap: 8px;
                           padding: 10px 20px;
                           background-color: ${(estadoFiltro?? && estadoFiltro == 'ACEPTADA')?then('#d1edff', 'var(--bg-light)')};
                           color: ${(estadoFiltro?? && estadoFiltro == 'ACEPTADA')?then('#004085', 'var(--text-color)')};
                           border: 1px solid ${(estadoFiltro?? && estadoFiltro == 'ACEPTADA')?then('#b3d7ff', 'var(--border-color)')};
                           border-radius: 8px;
                           text-decoration: none;
                           font-weight: 600;
                           transition: all 0.2s;
                           cursor: pointer;
                           ">

                    <span>Aceptadas</span>
                    <span class="badge-contador" style="
                            background-color: ${(estadoFiltro?? && estadoFiltro == 'ACEPTADA')?then('#004085', '#d1edff')};
                            color: ${(estadoFiltro?? && estadoFiltro == 'ACEPTADA')?then('#d1edff', '#004085')};
                            font-size: 0.8rem;
                            padding: 2px 8px;
                            border-radius: 12px;
                            margin-left: 4px;
                            ">${totalAceptadas!0}</span>
                </a>

                <!-- Botón para RECHAZADAS -->
                <a href="/admin/solicitudes/eliminacion?estado=RECHAZADA&page=1&size=${(size!10)?c}"
                   class="filtro-btn filtro-rechazadas ${(estadoFiltro?? && estadoFiltro == 'RECHAZADA')?then('filtro-btn-activo', '')}"
                   style="
                           display: inline-flex;
                           align-items: center;
                           gap: 8px;
                           padding: 10px 20px;
                           background-color: ${(estadoFiltro?? && estadoFiltro == 'RECHAZADA')?then('#f8d7da', 'var(--bg-light)')};
                           color: ${(estadoFiltro?? && estadoFiltro == 'RECHAZADA')?then('#721c24', 'var(--text-color)')};
                           border: 1px solid ${(estadoFiltro?? && estadoFiltro == 'RECHAZADA')?then('#f5c6cb', 'var(--border-color)')};
                           border-radius: 8px;
                           text-decoration: none;
                           font-weight: 600;
                           transition: all 0.2s;
                           cursor: pointer;
                           ">

                    <span>Rechazadas</span>
                    <span class="badge-contador" style="
                            background-color: ${(estadoFiltro?? && estadoFiltro == 'RECHAZADA')?then('#721c24', '#f8d7da')};
                            color: ${(estadoFiltro?? && estadoFiltro == 'RECHAZADA')?then('#f8d7da', '#721c24')};
                            font-size: 0.8rem;
                            padding: 2px 8px;
                            border-radius: 12px;
                            margin-left: 4px;
                            ">${totalRechazadas!0}</span>
                </a>
            </div>

            <!-- Información del filtro actual -->
            <#if estadoFiltro?? && estadoFiltro != "">
                <div class="filtro-info" style="
                    margin-top: 15px;
                    padding: 10px 15px;
                    background-color: var(--bg-light);
                    border-radius: 8px;
                    border-left: 4px solid var(--primary-color);
                ">
                    <p style="margin: 0; color: var(--text-color); font-weight: 500;">
                        Mostrando solicitudes con estado:
                        <span class="estado-badge estado-${estadoFiltro?lower_case}" style="margin-left: 8px;">
                            ${estadoFiltro}
                        </span>
                        <a href="/admin/solicitudes/eliminacion?page=1&size=${(size!10)?c}"
                           style="margin-left: 15px; color: var(--primary-color); text-decoration: none; font-size: 0.9rem;">
                            ✕ Limpiar filtro
                        </a>
                    </p>
                </div>
            </#if>
        </div>

        <!-- Resumen de solicitudes -->
        <div class="grid-metadata" style="margin-bottom: 30px;">
            <div class="card" style="
                background: var(--bg-white);
                border: 1px solid var(--border-color);
                border-radius: var(--radius);
                padding: 20px;
                box-shadow: var(--shadow);
            ">
                <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 15px;">
                    <div>
                        <p style="font-size: 1.5rem; font-weight: bold; color: var(--primary-color); margin: 0;">
                            ${totalSolicitudes!solicitudesEliminacion?size} solicitud(es)
                            <#if estadoFiltro?? && estadoFiltro != "">
                                <span style="font-size: 1rem; color: var(--text-light); display: block; margin-top: 5px;">
                                    Estado actual: ${estadoFiltro}
                                </span>
                            </#if>
                        </p>
                    </div>

                    <#if estadoFiltro?? && estadoFiltro != "">
                        <a href="/admin/solicitudes/eliminacion?page=1&size=${(size!10)?c}"
                           class="btn btn-cancel"
                           style="text-decoration: none; white-space: nowrap;">
                            Ver todas las solicitudes
                        </a>
                    </#if>
                </div>
            </div>
        </div>

        <!-- Línea divisoria gris -->
        <hr style="border: none; height: 1px; background-color: var(--border-color); margin: 30px 0;">

        <!-- Lista de solicitudes -->
        <div id="lista-solicitudes">
            <#if solicitudesEliminacion?size gt 0>
                <div class="list column gap-16">
                    <#list solicitudesEliminacion as solicitud>
                        <#if solicitud??>
                            <#assign estadoActual = (solicitud.estado)!'PENDIENTE'>
                            <div class="solicitud-item" data-estado="${estadoActual?html}">
                                <@solicitudCard solicitud=solicitud />
                            </div>
                        </#if>
                    </#list>
                </div>
            <#else>
                <div class="empty-state" style="
                    text-align: center;
                    padding: 60px 20px;
                    background: var(--bg-light);
                    border-radius: var(--radius);
                    border: 1px solid var(--border-color);
                ">
                    <div class="empty-state-icon" style="font-size: 3rem; margin-bottom: 20px;">
                        <#if estadoFiltro?? && estadoFiltro != "">
                            📋
                        <#else>
                            ✅
                        </#if>
                    </div>
                    <h3 style="color: var(--text-color); margin: 0 0 10px 0;">
                        <#if estadoFiltro?? && estadoFiltro != "">
                            No hay solicitudes ${estadoFiltro?lower_case}
                        <#else>
                            No hay solicitudes
                        </#if>
                    </h3>
                    <p style="color: var(--text-light); margin: 0;">
                        <#if estadoFiltro?? && estadoFiltro != "">
                            No se encontraron solicitudes con estado "${estadoFiltro}"
                        <#else>
                            No hay solicitudes de eliminación registradas
                        </#if>
                    </p>

                    <#if estadoFiltro?? && estadoFiltro != "">
                        <div style="margin-top: 20px;">
                            <a href="/admin/solicitudes/eliminacion?page=1&size=${(size!10)?c}"
                               class="btn btn-primary"
                               style="text-decoration: none;">
                                Ver todas las solicitudes
                            </a>
                        </div>
                    </#if>
                </div>
            </#if>
        </div>
    </div>

    <!-- Paginador -->
    <#if totalPages?? && (totalPages > 1)>
        <div class="page-btn" style="display: flex; justify-content: center; margin: 30px 0;">
            <#import "fragments/components.ftl" as cmp>
            <@cmp.pager
            page=(page!1)
            size=(size!10)
            totalPages=(totalPages!1)
            baseHref=(baseHref!"/admin/solicitudes/eliminacion")
            keep={'estado': (estadoFiltro!"")} />
        </div>
    </#if>
</#assign>

<!-- MACRO para tarjeta de solicitud -->
<#macro solicitudCard solicitud>
    <#assign estado = solicitud.estado!"PENDIENTE">

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
                <h3 class="solicitud-titulo" style="margin: 0 0 10px 0;">
                    🗑️ Solicitud de Eliminación
                    <small style="color: var(--text-light); font-weight: normal; font-size: 0.9rem;">
                        ID: ${solicitud.id}
                    </small>
                </h3>
                <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 10px;">
                    <span class="estado-badge estado-${estado?lower_case}">
                        ${estado}
                    </span>
                </div>
                <p class="solicitud-hecho" style="margin: 5px 0;">
                    <strong>Hecho:</strong> ${solicitud.hechoTitulo!"Sin título"}
                </p>
                <p class="solicitud-usuario" style="margin: 5px 0;">
                    <strong>Usuario:</strong>
                    <#if solicitud.usuario??>
                        ${solicitud.usuario.username!""}
                    <#else>
                        Anónimo
                    </#if>
                </p>
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
            </div>
        </div>
    </div>
</#macro>

<#include "layout.ftl">