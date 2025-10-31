<#assign pageTitle = "Registrarse" />
<#assign additionalCss = ["/css/styleLogin.css"]>

<#assign content>

    <main>
        <div class="login-container">
            <h2 id="titulo">Registrarse</h2>

            <#-- Mostrar mensaje de error si existe -->
            <#if error??>
                <div class="error">${error}</div>
            </#if>

            <form method="post" action="${baseAPIUrl}/api/sign-in" onsubmit="return validarPassword()">
                <input type="text" name="nombre" placeholder="Nombre" required>
                <input type="text" name="apellido" placeholder="Apellido" required>
                <input type="number" name="edad" placeholder="Edad" min="18" required>
                <input type="text" name="email" placeholder="Email" required>
                <input type="text" name="usuario" placeholder="Usuario" required minlength="3" maxlength="255">
                <input type="password" id="password" name="password" placeholder="Contraseña" required
                       minlength="8" maxlength="20">
                <input type="password" id="confirmPassword" name="confirmPassword"
                       placeholder="Confirmar Contraseña" required minlength="8" maxlength="20">
                <p id="error" style="color: red; display: none; text-align: center">
                    Las contraseñas no coinciden.
                </p>
                <button type="submit">Registrarme</button>
            </form>
        </div>
    </main>

</#assign>

<script>
    function validarPassword() {
        const pass = document.getElementById("password").value;
        const confirm = document.getElementById("confirmPassword").value;
        const error = document.getElementById("error");

        if (pass !== confirm) {
            error.style.display = "block";
            return false;
        } else {
            error.style.display = "none";
            return true;
        }
    }
</script>

<#include "layout.ftl">