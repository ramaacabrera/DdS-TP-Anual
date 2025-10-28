<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Colección creada</title>
    <link rel="stylesheet" href="/public/css/style.css">
</head>
<body>
<div class="container">
    <div class="header">
        <a href="/" class="header-link">← Volver al inicio</a>
    </div>

    <h1> Colección creada con éxito</h1>
    <p>La colección <strong>${coleccion.titulo}</strong> fue registrada correctamente.</p>

    <div class="card" style="margin-top: 25px;">
        <p><strong>Descripción:</strong> ${coleccion.descripcion}</p>
        <p><strong>Algoritmo de consenso:</strong> ${coleccion.algoritmoDeConsenso?string}</p>
    </div>

    <div class="card" style="margin-top: 25px;">
        <p><strong>Descripción:</strong> ${coleccion.descripcion}</p>
        <p><strong>Algoritmo:</strong> ${coleccion.algoritmoDeConsenso?string}</p>

        <h3>Fuentes:</h3>
        <ul>
            <#if coleccion.fuentes?? && coleccion.fuentes?size > 0>
                <#list coleccion.fuentes as f>
                    <li>${f.nombre}</li>
                </#list>
            <#else>
                <li>No se especificaron fuentes</li>
            </#if>
        </ul>

        <h3>Criterios:</h3>
        <ul>
            <#if coleccion.criteriosDePertenencia?? && coleccion.criteriosDePertenencia?size > 0>
                <#list coleccion.criteriosDePertenencia as c>
                    <li>${c?string}</li>
                </#list>
            <#else>
                <li>No se especificaron criterios</li>
            </#if>
        </ul>
    </div>


    <div style="margin-top: 30px;">
        <a href="/crear-coleccion" class="action-button"> Crear otra colección</a>
        <a href="/colecciones" class="action-button"> Ver todas las colecciones</a>
    </div>
</div>
</body>
</html>

