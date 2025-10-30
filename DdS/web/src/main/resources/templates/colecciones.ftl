<#assign pageTitle = "Colecciones">
<#assign content>
    <div class="container">
        <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
            <a href="/" class="header-link back-link">&larr; Volver al inicio</a>
        </div>

        <h1 class="main-title">Listado de Colecciones</h1>

        <div class="form-actions" style="margin-bottom: 20px;">
            <a href="/crear-coleccion" class="btn btn-primary">➕ Nueva Colección</a>
        </div>

        <#if colecciones?? && (colecciones?size > 0)>
            <div class="list column gap-16">
                <#list colecciones as c>
                    <div class="card">
                        <h3>${c.titulo!''}</h3>
                        <p>${c.descripcion!''}</p>
                        <small><b>Algoritmo:</b> ${c.algoritmoDeConsenso!''}</small>
                        <div style="margin-top:10px;">
                            <a href="/colecciones/${c.handle}" class="btn btn-secondary btn-sm">👁️ Ver Detalle</a>
                            <a href="/editar-coleccion/${c.handle}" class="btn btn-sm btn-outline">✏️ Editar</a>
                        </div>
                    </div>
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <h3>No hay colecciones creadas aún</h3>
                <p>Podés crear una nueva colección desde el botón de arriba.</p>
            </div>
        </#if>
    </div>
</#assign>

<#include "layout.ftl">
