<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Crear Colección</title>
    <link rel="stylesheet" href="/public/css/style.css">
</head>
<body>
<div class="container">
    <h1>Crear nueva colección</h1>

<form action="/crear-coleccion" method="post">
    <label for="titulo">Título:</label><br>
    <input type="text" id="titulo" name="titulo" required><br><br>

    <label for="descripcion">Descripción:</label><br>
    <textarea id="descripcion" name="descripcion" rows="4" required></textarea><br><br>

    <label for="algoritmo">Algoritmo de consenso:</label><br>
    <select id="algoritmo" name="algoritmo">
        <option value="ABSOLUTA">Absoluta</option>
        <option value="MAYORIASIMPLE">Mayoria simple</option>
        <option value="MULTIPLESMENCIONES">Multiples menciones</option>
    </select><br><br>

    <button type="submit">Crear Colección</button>
</form>


    <br>
    <a class="back-link" href="/">← Volver al inicio</a>
</div>
</body>
</html>
