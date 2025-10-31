<#assign pageTitle = "Iniciar Sesión" />
<#assign additionalCss = ["/css/styleLogin.css"]>

<#assign content>

    <main>
        <div class="login-container">
            <h2 id="titulo">Iniciar Sesión</h2>

            <#-- Mostrar mensaje de error si existe -->
            <#if error??>
                <div class="error">${error}</div>
            </#if>

            <form method="post" action="/login">
                <input type="text" name="usuario" placeholder="Usuario" required>
                <input type="password" name="password" placeholder="Contraseña" required>
                <input type="hidden" name="redirectUrl" value="${ultimaUrl!}">
                <button type="submit">Entrar</button>
            </form>
            <#if authUrl??>
                <div style="text-align: center">
                    <a href="${authUrl}">Iniciar sesión con Keycloak</a>
                </div>
            </#if>
            <div class="link">
                <a href ="/sign-in">¿No tenés cuenta? Registrate</a>
            </div>

        </div>
    </main>

</#assign>

<#include "layout.ftl">