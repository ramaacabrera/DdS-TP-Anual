<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Colección Actualizada</title>
    <link rel="stylesheet" href="/public/css/style.css">
</head>
<body>
<div class="container">
    <h1>Colección actualizada correctamente</h1>
    <p><b>Título:</b> ${coleccion.titulo}</p>
    <p><b>Descripción:</b> ${coleccion.descripcion}</p>
    <p><b>Algoritmo de consenso:</b> ${coleccion.algoritmoDeConsenso}</p>

    <h3>Fuentes asociadas:</h3>
    <ul>
        <#list coleccion.fuentes as fuente>
            <li>${fuente.nombre}</li>
        </#list>
    </ul>

    <a href="/colecciones">Volver</a>
</div>
</body>
</html>
