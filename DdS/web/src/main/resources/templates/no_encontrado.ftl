<#assign content>

    <div class="empty-state" style="margin-top: 20px;">

        <div class="empty-state-icon" style="color: var(--warning-color);">
            <i class="fas fa-compass"></i>
        </div>

        <h1 style="color: var(--primary-dark);">
            Página no encontrada
        </h1>

        <p class="text-primary" style="max-width: 500px; margin: 0 auto 20px auto;">
            Parece que la dirección que buscas no existe o fue movida.
            Tal vez quieras revisar la URL o volver al mapa principal.
        </p>

        <div class="pager">
            <a href="/" class="btn btn-primary">
                <i class="fas fa-home"></i> Ir al Inicio
            </a>

            <button onclick="history.back()" class="btn btn-outline">
                Volver atrás
            </button>
        </div>

    </div>

</#assign>

<#include "layout.ftl">