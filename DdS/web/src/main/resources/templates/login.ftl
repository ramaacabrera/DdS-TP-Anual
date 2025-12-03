<#assign pageTitle = "Iniciar Sesión" />
<#assign additionalCss = ["/css/styleLogin.css"]>

<#assign content>

    <main>
        <div class="login-container">
            <h2 id="titulo">Iniciar Sesión</h2>

            <p id="mensajeError" style="color: red; display: none; text-align: center"></p>
            <p id="mensajeExito" style="color: green; display: none; text-align: center"></p>

            <form id="loginForm">
                <input type="text" id="usuario" name="usuario" placeholder="Usuario" required>
                <input type="password" id="password" name="password" placeholder="Contraseña" required>

                <input type="hidden" id="redirectUrl" value="${ultimaUrl!}">

                <button type="button" onclick="enviarLogin()">Entrar</button>
            </form>

            <div class="link">
                <a href="/sign-in">¿No tenés cuenta? Registrate</a>
            </div>
        </div>
    </main>

</#assign>

<script>
    async function enviarLogin() {
        const usuario = document.getElementById("usuario").value.trim();
        const password = document.getElementById("password").value.trim();
        const redirectUrl = document.getElementById("redirectUrl").value;

        // Validación simple
        if (!usuario || !password) {
            mostrarError("Debes completar usuario y contraseña");
            return;
        }

        // Ocultar mensajes previos
        document.getElementById('mensajeError').style.display = 'none';
        document.getElementById('mensajeExito').style.display = 'none';

        const formData = new FormData();
        formData.append('usuario', usuario);
        formData.append('password', password);
        if (redirectUrl) {
            formData.append('redirectUrl', redirectUrl);
        }

        try {
            const response = await fetch('/login', {  // Usar /login, no ${baseAPIUrl}/login
                method: 'POST',
                body: new URLSearchParams(formData)  // Esto envía como application/x-www-form-urlencoded
            });

            if (response.redirected) {
                // Si hay redirección, seguirla
                window.location.href = response.url;
            } else if (response.ok) {
                const resultado = await response.json();
                // Manejar respuesta JSON si es el caso
                mostrarExito("Inicio de sesión exitoso");
                setTimeout(() => {
                    window.location.href = '/home';
                }, 1000);
            } else {
                const errorData = await response.json();
                mostrarError(errorData.error || "Credenciales incorrectas");
            }

        } catch (e) {
            console.error(e);
            mostrarError("Error de conexión con el servidor");
        }
    }

    function mostrarError(mensaje) {
        const errorEl = document.getElementById('mensajeError');
        errorEl.textContent = mensaje;
        errorEl.style.display = 'block';
    }

    function mostrarExito(mensaje) {
        const exitoEl = document.getElementById('mensajeExito');
        exitoEl.textContent = mensaje;
        exitoEl.style.display = 'block';
    }

    // Permitir Enter
    document.getElementById('loginForm').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            enviarLogin();
        }
    });
</script>

<#include "layout.ftl">