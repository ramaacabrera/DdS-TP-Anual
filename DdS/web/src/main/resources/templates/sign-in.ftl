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

            <form id="registroForm">
                <input type="text" name="nombre" id="nombre" placeholder="Nombre" required>
                <input type="text" name="apellido" id="apellido" placeholder="Apellido" required>
                <input type="number" name="edad" id="edad" placeholder="Edad" min="18" required>
                <input type="email" name="email" id="email" placeholder="Email" required>
                <input type="text" name="usuario" id="usuario" placeholder="Usuario" required minlength="3" maxlength="255">
                <input type="password" id="password" name="password" placeholder="Contraseña" required
                       minlength="8" maxlength="20">
                <input type="password" id="confirmPassword" name="confirmPassword"
                       placeholder="Confirmar Contraseña" required minlength="8" maxlength="20">
                <p id="error" style="color: red; display: none; text-align: center">
                    Las contraseñas no coinciden.
                </p>
                <p id="mensajeExito" style="color: green; display: none; text-align: center">
                    ¡Registro exitoso! Redirigiendo...
                </p>
                <p id="mensajeError" style="color: red; display: none; text-align: center"></p>
                <button type="button" onclick="enviarRegistro()">Registrarme</button>
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

    async function enviarRegistro() {
        // Validar contraseñas
        if (!validarPassword()) {
            return;
        }

        // Obtener valores del formulario
        const datos = {
            nombre: document.getElementById('nombre').value.trim(),
            apellido: document.getElementById('apellido').value.trim(),
            edad: parseInt(document.getElementById('edad').value),
            email: document.getElementById('email').value.trim(),
            usuario: document.getElementById('usuario').value.trim(),
            password: document.getElementById('password').value,
            confirmPassword: document.getElementById('confirmPassword').value
        };

        // Validaciones adicionales
        if (!datos.nombre || !datos.apellido || !datos.email || !datos.usuario || !datos.password) {
            mostrarError("Todos los campos son requeridos");
            return;
        }

        if (datos.edad < 18) {
            mostrarError("Debes tener al menos 18 años");
            return;
        }

        if (datos.usuario.length < 3) {
            mostrarError("El usuario debe tener al menos 3 caracteres");
            return;
        }

        if (datos.password.length < 8) {
            mostrarError("La contraseña debe tener al menos 8 caracteres");
            return;
        }

        // Ocultar mensajes anteriores
        document.getElementById('mensajeError').style.display = 'none';
        document.getElementById('mensajeExito').style.display = 'none';

        try {
            // Enviar como JSON
            const response = await fetch('${baseAPIUrl}/api/sign-in', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(datos)
            });

            const resultado = await response.json();

            if (response.ok) {
                // Mostrar mensaje de éxito
                document.getElementById('mensajeExito').style.display = 'block';
                document.getElementById('mensajeExito').textContent = resultado.mensaje || '¡Registro exitoso!';

                // Redirigir después de 2 segundos
                setTimeout(() => {
                    window.location.href = '${baseAPIUrl}/home';
                }, 2000);
            } else {
                // Mostrar mensaje de error del servidor
                mostrarError(resultado.error || 'Error en el registro');
            }
        } catch (error) {
            console.error('Error:', error);
            mostrarError('Error de conexión con el servidor');
        }
    }

    function mostrarError(mensaje) {
        const errorElement = document.getElementById('mensajeError');
        errorElement.textContent = mensaje;
        errorElement.style.display = 'block';
    }

    // Permitir enviar con Enter en cualquier campo
    document.getElementById('registroForm').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            enviarRegistro();
        }
    });
</script>

<#include "layout.ftl">