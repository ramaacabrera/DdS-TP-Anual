<#-- Macros reutilizables: hero, mediaGrid, tagsList -->
<#-- Usar <#import "fragments/components.ftl" as cmp> y luego <@cmp.hero hecho=hecho /> -->

<#macro hero hecho>
    <div class="hero">
        <#-- lógica: si hay multimedia, usá la primera imagen; si no, fallback -->
        <#if hecho.getContenidoMultimedia()?? && (hecho.getContenidoMultimedia()?size > 0)>
            <#assign primerMedia = hecho.getContenidoMultimedia()[0]>
            <#if (primerMedia.getTipoContenido()?upper_case == "IMAGEN") && primerMedia.contenido?has_content>
                <img src="${primerMedia.contenido?html}" alt="Imagen principal del hecho" style="width:100%; height:100%; object-fit:cover;">
            <#else>
                <div class="hero-default"> <img src="/img/noimg-default.png"alt="Sin imagen principal"></div>
            </#if>
        <#else>
            <div class="hero-default"> <img src="/img/noimg-default.png"alt="Sin imagen principal"></div>
        </#if>
    </div>
</#macro>
<#macro actionButtons hecho>
  <#-- obtiene id del hecho de forma segura -->
  <div class="action-buttons-group" data-hecho-id="${hecho.hecho_id?html}">
    <!-- Compartir: icono "share" (flecha saliendo de caja) -->
    <button class="icon-btn" data-action="share" aria-label="Compartir hecho" title="Compartir">
      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <path d="M4 12v7a1 1 0 001 1h14a1 1 0 001-1v-7" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M16 6l-4-4-4 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M12 2v13" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>

    <!-- Editar: lápiz claro -->
    <button class="icon-btn" data-action="edit" aria-label="Solicitar modificación" title="Solicitar modificación">
      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <path d="M3 21l3-1 11-11 1-3-3 1L4 20z" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M14 6l4 4" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>

    <!-- Eliminar: tacho de basura  -->
    <button class="icon-btn danger" data-action="delete" aria-label="Solicitar eliminación" title="Solicitar eliminación">
      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <path d="M3 6h18" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M8 6v14a1 1 0 001 1h6a1 1 0 001-1V6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M10 11v6M14 11v6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>
  </div>
</#macro>

<#macro mediaGrid hecho>
    <div class="media-grid">
        <#if hecho.getContenidoMultimedia()?? && (hecho.getContenidoMultimedia()?size > 0)>
            <#list hecho.getContenidoMultimedia() as media>
                <div class="media-item">
                    <#if media.getTipoContenido()?upper_case == "IMAGEN">
                        <img src="${media.contenido?html}" alt="Media" style="width:100%; height:100%; object-fit:cover;">
                    <#elseif media.getTipoContenido()?upper_case == "AUDIO">
                        <div style="padding:10px; font-size:0.9rem; color:var(--muted-color);">
                            🎧 <a href="${media.contenido?html}" target="_blank" rel="noopener">Audio</a>
                        </div>
                    <#elseif media.getTipoContenido()?upper_case == "VIDEO">
                        <div style="padding:10px; font-size:0.9rem; color:var(--muted-color);">
                            📹 <a href="${media.contenido?html}" target="_blank" rel="noopener">Video</a>
                        </div>
                    <#else>
                        <div style="padding:10px; font-size:0.9rem; color:var(--muted-color);">
                            📎 <a href="${media.contenido?html}" target="_blank" rel="noopener">Archivo</a>
                        </div>
                    </#if>
                </div>
            </#list>
        <#else>
            <div style="grid-column:1 / -1; color:#999;">No hay contenido multimedia adjunto.</div>
        </#if>
    </div>
</#macro>

<#macro tagsList etiquetas>
    <div class="tag-list">
        <#if etiquetas?? && (etiquetas?size > 0)>
            <#list etiquetas as etiqueta>
                <span class="tag">${etiqueta.nombre?html}</span>
            </#list>
        <#else>
            <p class="card-subtitle" style="color:#999;">Sin etiquetas.</p>
        </#if>
    </div>
</#macro>