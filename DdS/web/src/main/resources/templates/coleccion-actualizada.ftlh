<#assign pageTitle = "Colección actualizada">
<#assign content>
    <div class="container">
        <div class="header">
            <a href="/colecciones" class="header-link">← Volver a colecciones</a>
        </div>

        <h1>✅ Colección actualizada correctamente</h1>
        <div class="card" style="margin-top:25px;">
            <p><b>Título:</b> ${coleccion.titulo}</p>
            <p><b>Descripción:</b> ${coleccion.descripcion}</p>
            <p><b>Algoritmo de consenso:</b> ${coleccion.algoritmoDeConsenso}</p>

            <h3>Fuentes asociadas</h3>
            <ul>
                <#if coleccion.fuentes?? && coleccion.fuentes?size > 0>
                    <#list coleccion.fuentes as fuente>
                        <li>${fuente.tipoDeFuente}</li>
                    </#list>
                <#else>
                    <li>No hay fuentes asociadas</li>
                </#if>
            </ul>
        </div>

        <div style="margin-top:30px;">
            <a href="/editar-coleccion/${coleccion.handle}" class="action-button">✏️ Editar nuevamente</a>
            <a href="/colecciones" class="action-button">📚 Ver colecciones</a>
        </div>
    </div>
</#assign>
<#include "layout.ftl">
