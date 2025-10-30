<div class="site-content">

    <div class="hero-home" style="height: 450px; background-color: #343a40; color: white; display: flex; align-items: center; justify-content: center; margin-bottom: 30px; border-radius: 8px;">
        <h2 style="margin:0;">IMAGEN PRINCIPAL DE BIENVENIDA</h2>
    </div>

    <div class="card" style="margin-bottom: 30px;">
        <p class="card-subtitle">📍 Mapa de Hechos Registrados</p>
        <div id="map" style="height: 500px; border-radius: 4px; margin-top: 10px;"></div>
    </div>

    <div class="content-list">
        <#import "fragments/components.ftl" as cmp>
        <@cmp.filters filters=filters baseHref=baseHref filterValues=filterValues />

        <div class="hechos-grid">
            <#-- LISTA DE HECHOS -->
            <#list hechos as hecho>
                <div class="hecho-card">
                    <h3><a href="/hechos/${hecho.hecho_id}">${hecho.titulo?html}</a></h3>
                    <p>${hecho.descripcion?truncate(100)?html}</p>
                    <p class="card-subtitle">Fuente: ${hecho.fuente.tipoDeFuente}</p>
                </div>
            </#list>
            <#if hechos?size == 0><p>No se encontraron hechos que coincidan con los filtros.</p></#if>
        </div>

        <@cmp.pagination totalPages=totalPages page=page baseHref=baseHref filterValues=filterValues />
    </div>

</div>

<script src="/public/js/app-home.js"></script> <#-- Usaremos un archivo JS dedicado -->
<script>
    // Inyección de datos geográficos para el JS
    const hechosData = [
    <#list hechos as hecho>
        <#if hecho.ubicacion?? && hecho.ubicacion.latitud?? && hecho.ubicacion.longitud??>
        {
            id: "${hecho.hecho_id}",
            titulo: "${hecho.titulo?js_string}",
            lat: ${hecho.ubicacion.latitud},
            lon: ${hecho.ubicacion.longitud},
            desc: "${hecho.ubicacion.descripcion?js_string}"
        },
        </#if>
    </#list>
    ];

    // Llamar a la función de inicialización del mapa (definida en app-home.js)
    document.addEventListener('DOMContentLoaded', function() {
        if (typeof initMap === 'function') {
            initMap(hechosData, 'map');
        } else {
            console.error("initMap no está definido. Asegúrate de cargar app-home.js.");
        }
    });
</script>

</#assign>
<#include "layout.ftl">