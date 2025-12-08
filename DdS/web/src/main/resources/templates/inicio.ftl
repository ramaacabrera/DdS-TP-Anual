<#assign pageTitle = "MetaMapa">
<#assign additionalCss = ["/css/styleInicio.css"]>
<#assign content>
    <#import "fragments/components.ftl" as cmp>
    <div class="home-container">
        <!-- Hero Section -->
        <@cmp.heroSection
        title="Recopilación, Visibilización y Mapeo Colaborativo"
        subtitle="MetaMapa es una plataforma que permite registrar, visualizar y analizar información geográfica de manera colaborativa. Únete a nuestra comunidad y contribuye al mapeo colectivo."
        />

        <!-- Navegación Rápida con Cards -->
        <@cmp.quickNavCards/>

        <!-- Mapa de Hechos Registrados -->
        <@cmp.mapSection
        title="Mapa de Hechos Registrados"
        totalHechos=total
        displayedHechos=hechos?size
        />

        <script src="/js/app-home.js"></script>
        <script>
            const hechosData = [
                <#list hechos as hecho>
                <#if hecho.ubicacion?? && hecho.ubicacion.latitud?? && hecho.ubicacion.longitud??>
                {
                    id: "${hecho.hechoId}",
                    titulo: "${hecho.titulo?js_string}",
                    lat: ${hecho.ubicacion.latitud?string?replace(",", ".")},
                    lon: ${hecho.ubicacion.longitud?string?replace(",", ".")}
                },
                </#if>
                </#list>
            ];

            document.addEventListener('DOMContentLoaded', function() {
                if (typeof initMap === 'function') {
                    initMap(hechosData, 'map');
                } else {
                    console.error("initMap no está definido. Asegúrate de cargar app-home.js.");
                }
            });
        </script>
    </div>


</#assign>
<#include "layout.ftl">
