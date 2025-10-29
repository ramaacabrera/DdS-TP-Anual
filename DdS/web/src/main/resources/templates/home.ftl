<#assign pageTitle = "Hechos" />
<#assign content>
    <div class="container">

        <section class="box mb-20">
            <h2 class="box-title">Búsqueda y filtros</h2>
            <form method="get" action="${baseHref!'/api/hechos'}" class="filters-form">
                <#import "fragments/components.ftl" as cmp>
                <@cmp.filterGroup filters=filters values=filterValues />
            </form>
        </section>

        <#if total??>
            <div class="muted mb-10">
                Mostrando ${(fromIndex!0)+1}-${(toIndex!0)} de ${total} hechos
            </div>
        </#if>

        <#if hechos?? && (hechos?size > 0)>
            <div class="list column gap-16">
                <#import "fragments/components.ftl" as cmp>
                <#list hechos as h>
                    <@cmp.hechoCard
                    id=h.hecho_id
                    titulo=h.titulo
                    resumen=h.resumen!""
                    fecha=h.fecha!""
                    categoria=h.categoria!""
                    ubicacion=h.ubicacion!""
                    etiquetas=h.etiquetas![]
                    verHref="/api/hechos/${h.hecho_id}"
                    editarHref="/editar-hecho/${h.hecho_id}" />
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <p>No se encontraron hechos para los filtros actuales.</p>
            </div>
        </#if>

        <#import "fragments/components.ftl" as cmp>
        <@cmp.pager
        page=(page!1)
        size=(size!10)
        totalPages=(totalPages!1)
        baseHref=(baseHref!"/api/hechos")
        keep=filterValues />

    </div>
</#assign>
<#include "layout.ftl">
