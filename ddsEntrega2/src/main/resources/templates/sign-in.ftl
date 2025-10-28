<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="../public/sign-in.css">
     <title>Registrarse</title>

</head>

<body>



<div class="login-container">
    <h2 id="titulo">Registrarse</h2>

    <#-- Mostrar mensaje de error si existe -->
    <#if error??>
        <div class="error">${error}</div>
    </#if>

    <form method="post" action="/api/sign-in">
        <input type="text" name="nombre" placeholder="Nombre" required>
        <input type="text" name="apellido" placeholder="Apellido" required>
        <input type="number" name="edad" placeholder="Edad" min="18" required>
        <button type="submit">Registrar</button>
    </form>
</div>

</body>
</html>