<#assign pageTitle = "Editar Colección">
<#assign content>
    <div class="container">
        <div class="header">
            <a href="/colecciones" class="header-link">← Volver</a>
        </div>

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
            <textarea id="fuentes" name="fuentes" rows="2" placeholder="Ej: ESTATICA, DINAMICA, PROXY">
<#if coleccion.fuentes??>
    ${coleccion.fuentes?join(", ")}
</#if>
</textarea><br><br>

            <h3>Criterios de pertenencia</h3>
            <textarea id="criterios" name="criteriosDePertenencia" rows="2" placeholder="Ej: CriterioDeTexto: palabra">
<#if coleccion.criteriosDePertenencia??>
    ${coleccion.criteriosDePertenencia?join(", ")}
</#if>
</textarea><br><br>

            <button type="submit" class="btn btn-primary">Guardar cambios</button>
        </form>

        <div class="card" style="margin-top:25px;">
            <h3>Vista previa actual</h3>
            <p><b>Título:</b> ${coleccion.titulo}</p>
            <p><b>Descripción:</b> ${coleccion.descripcion}</p>
            <p><b>Algoritmo:</b> ${coleccion.algoritmoDeConsenso}</p>
        </div>
    </div>
</#assign>
<#include "layout.ftl">

