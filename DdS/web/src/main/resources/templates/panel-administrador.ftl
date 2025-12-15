<#assign pageTitle = "Panel Administrador">
<#assign additionalCss = ["/css/panelAdministrador.css"]>

<#assign content>
    <div class="admin-container">

        <div class="admin-header">
            <div class="admin-welcome">
                <h1>Panel de Control</h1>
                <p>Bienvenido, administrador. Selecciona una acción para comenzar.</p>
            </div>
            <div class="admin-date">
                <span id="current-date"></span>
            </div>
        </div>

        <div class="dashboard-grid">

            <div class="admin-card">
                <div class="card-header">
                    <div class="icon-wrapper blue">
                        <i data-feather="layers"></i>
                    </div>
                    <h3>Colecciones</h3>
                </div>
                <div class="card-body">
                    <p>Gestiona las agrupaciones de hechos y mapas temáticos.</p>
                    <div class="action-list">
                        <a href="/admin/crear-coleccion/" class="action-item">
                            <i data-feather="plus-circle"></i>
                            <span>Crear Nueva Colección</span>
                        </a>
                        <a href="/colecciones?callback=/admin/panel" class="action-item">
                            <i data-feather="list"></i>
                            <span>Ver/Editar Colecciones</span>
                        </a>
                    </div>
                </div>
            </div>

            <div class="admin-card">
                <div class="card-header">
                    <div class="icon-wrapper green">
                        <i data-feather="map-pin"></i>
                    </div>
                    <h3>Hechos</h3>
                </div>
                <div class="card-body">
                    <p>Supervisa los reportes, etiqueta contenido y gestiona la calidad.</p>
                    <div class="action-list">
                        <a href="/hechos?callback=/admin/panel" class="action-item">
                            <i data-feather="tag"></i>
                            <span>Etiquetar y Gestionar Hechos</span>
                        </a>
                    </div>
                </div>
            </div>

            <div class="admin-card">
                <div class="card-header">
                    <div class="icon-wrapper orange">
                        <i data-feather="alert-circle"></i>
                    </div>
                    <h3>Solicitudes Pendientes</h3>
                </div>
                <div class="card-body">
                    <p>Revisa las peticiones de los usuarios sobre el contenido.</p>

                    <div class="action-list">
                    <#--
                        <a href="/admin/solicitudes/modificacion" class="action-item warning-hover">
                            <div class="action-text">
                                <span class="title">Modificaciones</span>
                                <#if pendientesModificacion??>
                                    <span class="badge badge-warning">${pendientesModificacion} pendientes</span>
                                </#if>
                            </div>
                            <i data-feather="chevron-right"></i>
                        </a> -->

                        <a href="/admin/solicitudes/eliminacion" class="action-item danger-hover">
                            <div class="action-text">
                                <span class="title">Eliminaciones</span>
                                <#if pendientesEliminacion??>
                                    <span class="badge badge-danger">${pendientesEliminacion} pendientes</span>
                                </#if>
                            </div>
                            <i data-feather="chevron-right"></i>
                        </a>
                    </div>
                </div>
            </div>

        </div>
    </div>

    <script>
        // Script simple para mostrar fecha
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        document.getElementById('current-date').textContent = new Date().toLocaleDateString('es-ES', options);
    </script>
</#assign>

<#include "layout.ftl">