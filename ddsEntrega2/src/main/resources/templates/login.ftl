<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="../public/login.css">
     <title>Iniciar Sesión</title>

</head>

<body>


<div class="login-container">
    <h2 id="titulo">Iniciar Sesión</h2>

    <#-- Mostrar mensaje de error si existe -->
    <#if error??>
        <div class="error">${error}</div>
    </#if>

    <form method="post" action="/api/login">
        <input type="text" name="usuario" placeholder="Usuario" required>
        <input type="password" name="password" placeholder="Contraseña" required>
        <button type="submit">Entrar</button>
    </form>
    <div class="link">
    <a href ="/api/sign-in">¿No tenés cuenta? Registrate</a>
    </div>
</div>

</body>
</html>