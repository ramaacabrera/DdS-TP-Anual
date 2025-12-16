<#assign content>

    <div class="empty-state" style="margin-top: 20px;">

        <div class="empty-state-icon" style="color: var(--danger-color);">
            <i class="fas fa-bug"></i>
        </div>

        <h1 style="color: var(--primary-dark);">
            ¡Ups! Error interno
        </h1>

        <p class="text-primary" style="max-width: 600px; margin: 0 auto 20px auto;">
            Ocurrió un problema inesperado al procesar tu solicitud.
            El equipo técnico ya ha sido notificado.
        </p>


        <div class="pager">
            <a href="/" class="btn btn-primary">
                Volver al Inicio
            </a>

            <button onclick="history.back()" class="btn btn-outline">
                Volver atrás
            </button>
        </div>

    </div>

</#assign>

<#include "layout.ftl">