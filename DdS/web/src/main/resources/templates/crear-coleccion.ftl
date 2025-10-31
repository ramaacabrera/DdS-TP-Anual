<#assign pageTitle = "Crear Colección">
<#assign additionalCss = ["/css/styleCrearColeccion.css"]>
<#assign content>
    <div class="container">
        <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
            <a href="/" class="header-link back-link">&larr; Volver al inicio</a>
        </div>

        <h1 class="main-title">Crear nueva colección</h1>

        <#if resultado??>
            <h2 style="text-align: center">${resultado}</h2>
        </#if>

        <script>
            const URL_ADMIN = '${urlAdmin!"http://localhost:8081"}';
        </script>

        <form id="form-crear-coleccion" class="form-container" method="POST" action="/colecciones">
            <!-- Título -->
            <div class="form-group">
                <label for="titulo" class="form-label">Título *</label>
                <input type="text" id="titulo" name="titulo" class="form-input" required placeholder="Ej: Colección de reportes ambientales">
            </div>

            <!-- Descripción -->
            <div class="form-group">
                <label for="descripcion" class="form-label">Descripción *</label>
                <textarea id="descripcion" name="descripcion" class="form-textarea" rows="4" required placeholder="Describa brevemente la colección..."></textarea>
            </div>

            <!-- Algoritmo -->
            <div class="form-group">
                <label for="algoritmo" class="form-label">Algoritmo de consenso *</label>
                <select id="algoritmo" name="algoritmo" class="form-select" required>
                    <#list algoritmos as alg>
                        <option value="${alg}">${alg?capitalize}</option>
                    </#list>
                </select>
            </div>

            <!-- Criterios -->
            <div class="form-section">
                <h3 class="form-section-title">Criterios de pertenencia</h3>
                <div class="form-group">
                    <label for="tipoCriterio" class="form-label">Tipo de criterio</label>
                    <select id="tipoCriterio" name="criteriosDePertenencia" class="form-select" onchange="mostrarCampoCriterio()">
                        <option value="CriterioDeTexto">Texto</option>
                        <option value="CriterioTipoMultimedia">Tipo Multimedia</option>
                        <option value="CriterioEtiquetas">Etiquetas</option>
                        <option value="CriterioFecha">Fecha</option>
                        <option value="CriterioUbicacion">Ubicación</option>
                        <option value="CriterioContribuyente">Contribuyente</option>
                    </select>
                    <div id="campoCriterio"></div>
                </div>
            </div>

            <!-- Fuentes -->
            <div class="form-section">
                <h3 class="form-section-title">Fuentes</h3>
                <label for="fuentes" class="form-label">Seleccioná una o más fuentes:</label><br>
                <select id="fuentes" name="fuentes" multiple class="form-select" size="4">
                    <#list fuentes as fuente>
                        <option value="${fuente}">${fuente?capitalize}</option>
                    </#list>
                </select>
            </div>

            <!-- Listas iniciales -->
            <div class="form-section">
                <h3 class="form-section-title">Hechos incluidos en la colección</h3>
                <div id="lista-hechos" class="lista-vacia">
                    <p class="texto-placeholder">Aún no hay hechos asociados.</p>
                    <!-- Acá se cargan los hechos cuando se agreguen -->
                </div>
            </div>

            <div class="form-section">
                <h3 class="form-section-title">Hechos consensuados</h3>
                <div id="lista-consensuados" class="lista-vacia">
                    <p class="texto-placeholder">Aún no hay hechos consensuados.</p>
                    <!-- Acá se miuestran los hechos filtrados por el algoritmo de consenso -->
                </div>
            </div>

            <!-- Botones -->
            <div class="form-actions">
                <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
                <button type="submit" class="btn btn-primary">Crear Colección</button>
            </div>

            <!-- Mensajes -->
            <div id="mensaje-exito" class="mensaje mensaje-exito" style="display:none;">
                ✅ Colección creada exitosamente.
            </div>
            <div id="mensaje-error" class="mensaje mensaje-error" style="display:none;">
                ❌ Error al crear la colección. Intente nuevamente.
            </div>
        </form>
    </div>
</#assign>

<script>
    function mostrarCampoCriterio() {
        const tipo = document.getElementById("tipoCriterio").value;
        const campoDiv = document.getElementById("campoCriterio");

        // Limpiar contenido anterior
        campoDiv.innerHTML = "";

        switch (tipo) {
            case "CriterioDeTexto":
                campoDiv.innerHTML = `
                    <label class="form-label">Palabras clave:</label>
                    <div id="lista-palabras">
                        <input type="text" name="criterio.palabras[]" class="form-input palabra-input" placeholder="Ej: contaminación" required>
                    </div>
                    <button type="button" class="btn btn-secondary" onclick="agregarPalabra()">Agregar otra palabra</button>
                    <label for="tipoMultimedia" class="form-label">Tipo de texto:</label>
                    <select id="tipoMultimedia" name="criterio.tipoDeTexto" class="form-select" required>
                        <option value="titulo">Titulo</option>
                        <option value="descripcion">Descripcion</option>
                        <option value="categoria">Categoria</option>
                    </select>
                `;
                break;
            case "CriterioEtiquetas":
                campoDiv.innerHTML = `
                    <label class="form-label">Palabras clave:</label>
                    <div id="lista-palabras">
                        <input type="text" name="criterio.etiquetas[]" class="form-input palabra-input" placeholder="Ej: contaminación" required>
                    </div>
                    <button type="button" class="btn btn-secondary" onclick="agregarPalabra()">Agregar otra etiqueta</button>
                `;
                break;

            case "CriterioTipoMultimedia":
                campoDiv.innerHTML = `
                <label for="tipoMultimedia" class="form-label">Tipo de archivo:</label>
                <select id="tipoMultimedia" name="criterio.tipoMultimedia" class="form-select" required>
                    <option value="imagen">Imagen</option>
                    <option value="video">Video</option>
                    <option value="audio">Audio</option>
                </select>
            `;
                break;

            case "CriterioFecha":
                campoDiv.innerHTML = `
                <label for="fechaInicio" class="form-label">Desde:</label>
                <input type="date" id="fechaInicio" name="criterio.fechaInicio" class="form-input" required>
                <label for="fechaFin" class="form-label">Hasta:</label>
                <input type="date" id="fechaFin" name="criterio.fechaFin" class="form-input" required>
                <label for="tipoDeFecha" class="form-label">Tipo de fecha:</label>
                <select id="tipoDeFecha" name="criterio.tipoDeFecha" class="form-select" required>
                    <option value="fechaDelAcontecimiento">Fecha del acontecimiento</option>
                    <option value="fechaDeCarga">Fecha de carga</option>
                </select>
            `;
                break;

            case "CriterioUbicacion":
                campoDiv.innerHTML = `
                <label class="form-label">Ubicación</label>
            <div class="form-group">
                <input type="number" step="any" id="latitud" name="criterio.ubicacion.latitud" class="form-input" placeholder="Latitud" required>
                <input type="number" step="any" id="longitud" name="criterio.ubicacion.longitud" class="form-input" placeholder="Longitud" required>
            </div>
            `;
                break;

            case "CriterioContribuyente":
                campoDiv.innerHTML = `
                <label for="contribuyente" class="form-label">Nombre del contribuyente:</label>
                <input type="text" id="contribuyente" name="criterio.contribuyente" class="form-input" required>
            `;
                break;

            default:
                campoDiv.innerHTML = "";
        }
    }
</script>

<script>
    function agregarPalabra() {
        const contenedor = document.getElementById("lista-palabras");
        const nuevoInput = document.createElement("div");
        nuevoInput.classList.add("input-dinamico");
        nuevoInput.innerHTML = `
        <input type="text" name="criterio.palabras[]" class="form-input palabra-input" placeholder="Otra palabra" required>
        <button type="button" class="btn btn-danger btn-eliminar" onclick="eliminarPalabra(this)">✖</button>
    `;
        contenedor.appendChild(nuevoInput);
    }

    function eliminarPalabra(boton) {
        boton.parentElement.remove();
    }
</script>

<#include "layout.ftl">

