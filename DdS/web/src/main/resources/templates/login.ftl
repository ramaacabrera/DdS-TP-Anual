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
                <button type="submit">Entrar</button>
            </form>
            <div class="link">
                <a href ="/sign-in">¿No tenés cuenta? Registrate</a>
            </div>
        </div>
    </main>

</#assign>

<#include "layout.ftl">