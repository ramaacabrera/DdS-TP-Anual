<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Colección</title>
    <link rel="stylesheet" href="/public/css/style.css">
</head>
<body>
<div class="container">
    <h1>Editar Colección</h1>

    <form action="/editar-coleccion/${coleccion.handle}" method="post">

        <label for="titulo">Título</label><br>
        <input type="text" id="titulo" name="titulo" value="${coleccion.titulo}" required><br><br>

        <label for="descripcion">Descripción</label><br>
        <textarea id="descripcion" name="descripcion" rows="4" required>${coleccion.descripcion}</textarea><br><br>

        <label for="algoritmo">Algoritmo de consenso</label><br>
        <select id="algoritmo" name="algoritmo">
            <#list algoritmosDisponibles as alg>
                <option value="${alg}" <#if coleccion.algoritmoDeConsenso?string == alg>selected</#if>>
                    ${alg?replace("_", " ")}
                </option>
            </#list>
        </select><br><br>
        <h3>Fuentes asociadas</h3>
        <textarea id="fuentes" name="fuentes" rows="2">
<#if coleccion.fuentes??>
    ${coleccion.fuentes?join(", ")}
</#if>
</textarea><br><br>

        <h3>Criterios de pertenencia</h3>
        <textarea id="criterios" name="criteriosDePertenencia" rows="2">
<#if coleccion.criteriosDePertenencia??>
    ${coleccion.criteriosDePertenencia?join(", ")}
</#if>
</textarea><br><br>

        <label>Fuentes asociadas</label><br>
        <ul>
            <#if coleccion.fuentes?? && coleccion.fuentes?size > 0>
                <#list coleccion.fuentes as fuente>
                    <li>${fuente.nombre}</li>
                </#list>
            <#else>
                <li>No hay fuentes asociadas</li>
            </#if>
        </ul>

        <label>Criterios de pertenencia</label><br>
        <ul>
            <#if coleccion.criteriosDePertenencia?? && coleccion.criteriosDePertenencia?size > 0>
                <#list coleccion.criteriosDePertenencia as criterio>
                    <li>${criterio?string}</li>
                </#list>
            <#else>
                <li>No hay criterios definidos</li>
            </#if>
        </ul>

        <button type="submit">Guardar cambios</button>
    </form>

    <a href="/colecciones">← Volver</a>
</div>
</body>
</html>

