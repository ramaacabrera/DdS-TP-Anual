<#assign pageTitle = "Editar Hecho - ${hecho.titulo}">
<#assign content>

<div class="header" style="margin-bottom:20px;">
    <a href="/hechos/${hecho.id}" class="header-link">&larr; Volver</a>
</div>

<h1>Editar Hecho</h1>

<form method="post" action="/hechos/${hecho.id}/editar" class="form">

    <label>Título</label>
    <input type="text" name="titulo" value="${hecho.titulo?html}" required>

    <label>Descripción</label>
    <textarea name="descripcion" rows="4">${hecho.descripcion?html}</textarea>

    <label>Categoría</label>
    <input type="text" name="categoria" value="${hecho.categoria!}">

    <label>Estado del Hecho</label>
    <input type="text" name="estadoHecho" value="${hecho.estadoHecho!}">

    <label>Fecha de Acontecimiento</label>
    <input type="datetime-local" name="fechaDeAcontecimiento"
           value="${hecho.fechaDeAcontecimiento?string("yyyy-MM-dd'T'HH:mm")}">

    <label>Fecha de Carga</label>
    <input type="datetime-local" name="fechaDeCarga"
           value="${hecho.fechaDeCarga?string("yyyy-MM-dd'T'HH:mm")}">

    <h3>Ubicación</h3>

    <label>Latitud</label>
    <input type="text" name="latitud" value="${hecho.ubicacion.latitud?string}">

    <label>Longitud</label>
    <input type="text" name="longitud" value="${hecho.ubicacion.longitud?string}">

    <label>ID Contribuyente</label>
    <input type="text" name="idContribuyente" value="${hecho.contribuyente.id!}">

    <label>ID Fuente</label>
    <input type="text" name="idFuente" value="${hecho.fuente.idFuente!}">

    <label>Etiquetas (separadas por coma)</label>
    <input type="text" name="etiquetas"
           value="${hecho.etiquetas?join(', ')}">

    <label>Contenido Multimedia (URLs separadas por coma)</label>
    <input type="text" name="multimedia"
           value="${hecho.contenidoMultimedia?join(', ')}">

    <button type="submit" class="btn-primary">Guardar Cambios</button>
</form>

</#assign>
<#include"layout.ftl">