<#assign pageTitle = "Editar Colección">
<#assign additionalCss = ["/css/styleCrearColeccion.css"]>
<#assign content>
    <div class="container">
        <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
            <a href="/colecciones" class="header-link back-link">&larr; Volver a colecciones</a>
        </div>

        <h1 class="main-title">Editar Colección</h1>

        <script>
            const URL_ADMIN = '${URL_ADMIN!"http://localhost:8081"}';
            const COLECCION_ID = '${COLECCION_ID}';
        </script>

        <form id="form-editar-coleccion" class="form-container" method="POST" action="/colecciones/${COLECCION_ID}">
            <!-- Título -->
            <div class="form-group">
                <label for="titulo" class="form-label">Título *</label>
                <input type="text" id="titulo" name="titulo" class="form-input" value="${coleccion.titulo!''}" required>
            </div>

            <!-- Descripción -->
            <div class="form-group">
                <label for="descripcion" class="form-label">Descripción *</label>
                <textarea id="descripcion" name="descripcion" class="form-textarea" rows="4" required>${coleccion.descripcion!''}</textarea>
            </div>

            <!-- Algoritmo -->
            <div class="form-group">
                <label for="algoritmo" class="form-label">Algoritmo de consenso *</label>
                <select id="algoritmo" name="algoritmo" class="form-select" required>
                    <#list algoritmos as alg>
                        <option value="${alg}" <#if coleccion.algoritmoDeConsenso?string == alg>selected</#if>>
                            ${alg?capitalize}
                        </option>
                    </#list>
                </select>
            </div>

            <!-- Criterios -->
            <div class="form-section">
                <h3 class="form-section-title">Criterios de pertenencia</h3>

                <!-- Contenedor para los criterios existentes (pre-cargados) -->
                <div id="criterios-agregados" class="criterios-container">
                    <#if coleccion.criteriosDePertenencia?? && (coleccion.criteriosDePertenencia?size > 0)>
                        <#list coleccion.criteriosDePertenencia as criterio>
                            <#assign criterioId = "criterio_" + criterio_index>
                            <#assign tipo = "">
                            <div class="criterio-card" id="${criterioId}">
                                <div class="criterio-header">
                                    <span class="criterio-tipo">${(criterio.tipoCriterio!)?replace('Criterio', '')}</span>
                                    <button type="button" class="btn-eliminar-criterio">Eliminar</button>
                                </div>

                                <#if criterio.tipoDeTexto??>
                                <#assign tipo = "CriterioDeTexto">
                                    <div><strong>Palabras:</strong> ${(criterio.palabras?join(', '))!''}</div>
                                    <div><strong>Tipo de texto:</strong> ${(criterio.tipoDeTexto)!''}</div>
                                    <#if criterio.palabras??>
                                        <#list criterio.palabras as palabra>
                                            <input type="hidden" name="criterios[${criterioId}].palabras" value="${palabra}">
                                        </#list>
                                    </#if>
                                    <input type="hidden" name="criterios[${criterioId}].tipoDeTexto" value="${(criterio.tipoDeTexto)!''}">

                                <#elseif criterio.etiquetas??>
                                <#assign tipo = "CriterioEtiquetas">
                                    <div><strong>Etiquetas:</strong>
                                        <#if criterio.etiquetas??>
                                            ${criterio.etiquetas?map(e -> e.nombre)?join(', ')}
                                        </#if>
                                    </div>
                                    <#if criterio.etiquetas??>
                                        <#list criterio.etiquetas as etiqueta>
                                            <input type="hidden" name="criterios[${criterioId}].etiquetas" value="${etiqueta.nombre}">
                                        </#list>
                                    </#if>

                                <#elseif criterio.tipoContenidoMultimedia??>
                                <#assign tipo = "CriterioTipoMultimedia">
                                    <div><strong>Tipo:</strong> ${(criterio.tipoContenidoMultimedia)!''}</div>
                                    <input type="hidden" name="criterios[${criterioId}].tipoContenidoMultimedia" value="${(criterio.tipoContenidoMultimedia)!''}">

                                <#elseif criterio.tipoFecha??>
                                <#assign tipo = "CriterioFecha">
                                    <#-- Fecha de inicio -->
                                    <#if criterio.fechaInicio??>
                                        <#-- Convertir a timestamp (long) y luego a datetime -->
                                        <#assign fechaInicioTimestamp = criterio.fechaInicio?long>
                                        <#assign fechaInicioDate = fechaInicioTimestamp?number_to_datetime>
                                        <#assign fechaInicioStr = fechaInicioDate?string('yyyy-MM-dd')>
                                    <#else>
                                        <#assign fechaInicioStr = ''>
                                    </#if>

                                    <#-- Fecha de fin -->
                                    <#if criterio.fechaFin??>
                                        <#assign fechaFinTimestamp = criterio.fechaFin?long>
                                        <#assign fechaFinDate = fechaFinTimestamp?number_to_datetime>
                                        <#assign fechaFinStr = fechaFinDate?string('yyyy-MM-dd')>
                                    <#else>
                                        <#assign fechaFinStr = ''>
                                    </#if>

                                    <div><strong>Desde:</strong> ${fechaInicioStr}</div>
                                    <div><strong>Hasta:</strong> ${fechaFinStr}</div>
                                    <div><strong>Tipo de fecha:</strong> ${(criterio.tipoFecha)!''}</div>
                                    <input type="hidden" name="criterios[${criterioId}].fechaInicio" value="${fechaInicioStr}">
                                    <input type="hidden" name="criterios[${criterioId}].fechaFin" value="${fechaFinStr}">
                                    <input type="hidden" name="criterios[${criterioId}].tipoFecha" value="${(criterio.tipoFecha)!''}">

                                <#elseif criterio.ubicacion??>
                                <#assign tipo = "CriterioUbicacion">
                                    <#if criterio.ubicacion??>
                                        <div><strong>Latitud:</strong> ${(criterio.ubicacion.latitud)!''}</div>
                                        <div><strong>Longitud:</strong> ${(criterio.ubicacion.longitud)!''}</div>
                                        <input type="hidden" name="criterios[${criterioId}].ubicacion.latitud" value="${(criterio.ubicacion.latitud)!''}">
                                        <input type="hidden" name="criterios[${criterioId}].ubicacion.longitud" value="${(criterio.ubicacion.longitud)!''}">
                                    </#if>

                                <#elseif criterio.nombreContribuyente??>
                                <#assign tipo = "CriterioContribuyente">
                                    <div><strong>Contribuyente:</strong> ${(criterio.nombreContribuyente)!''}</div>
                                    <input type="hidden" name="criterios[${criterioId}].nombreContribuyente" value="${(criterio.nombreContribuyente)!''}">
                                </#if>

                                <input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">
                            </div>
                        </#list>
                    </#if>
                </div>

                <!-- Formulario para agregar nuevo criterio (igual que en creación) -->
                <div class="nuevo-criterio-card">
                    <h4 class="form-subtitle">Agregar nuevo criterio</h4>
                    <div class="form-group">
                        <label for="tipoCriterio" class="form-label">Tipo de criterio</label>
                        <select id="tipoCriterio" class="form-select">
                            <option value="">Seleccione un tipo</option>
                            <option value="CriterioDeTexto">Texto</option>
                            <option value="CriterioTipoMultimedia">Tipo Multimedia</option>
                            <option value="CriterioEtiquetas">Etiquetas</option>
                            <option value="CriterioFecha">Fecha</option>
                            <option value="CriterioUbicacion">Ubicación</option>
                            <option value="CriterioContribuyente">Contribuyente</option>
                        </select>
                        <div id="campoCriterio" class="campo-criterio-dinamico"></div>
                        <button type="button" id="btn-agregar-criterio" class="btn btn-secondary" style="margin-top: 10px;" disabled>
                            + Agregar criterio
                        </button>
                    </div>
                </div>
            </div>

            <!-- Fuentes -->
            <div class="form-section">
                <h3 class="form-section-title">Fuentes asociadas</h3>
                <label for="fuentes" class="form-label">Seleccioná una o más fuentes:</label><br>
                <select id="fuentes" name="fuentes" multiple class="form-select" size="4">
                    <#list fuentes as fuente>
                        <option value="${fuente}"
                                <#if coleccion.fuentes?has_content && coleccion.fuentes?map(f -> f.tipoDeFuente)?seq_contains(fuente)>selected</#if>>
                            ${fuente?capitalize}
                        </option>
                    </#list>
                </select>
            </div>

            <!-- Hechos asociados (solo lectura) -->
            <div class="form-section">
                <h3 class="form-section-title">Hechos asociados</h3>
                <#if coleccion.hechos?? && (coleccion.hechos?size > 0)>
                    <div id="lista-hechos" class="lista-hechos">
                        <#list coleccion.hechos as hecho>
                            <div class="hecho-card">
                                <strong>${(hecho.titulo)!''}</strong>
                                <p>${(hecho.descripcion)!?truncate(100, '...')}</p>
                            </div>
                        </#list>
                    </div>
                <#else>
                    <div id="lista-hechos" class="lista-vacia">
                        <p class="texto-placeholder">No hay hechos asociados aún.</p>
                    </div>
                </#if>
            </div>

            <!-- Hechos consensuados (solo lectura) -->
            <div class="form-section">
                <h3 class="form-section-title">Hechos consensuados</h3>
                <#if coleccion.hechosConsensuados?? && (coleccion.hechosConsensuados?size > 0)>
                    <div id="lista-consensuados" class="lista-hechos">
                        <#list coleccion.hechosConsensuados as hecho>
                            <div class="hecho-card">
                                <strong>${(hecho.titulo)!''}</strong>
                                <p>${(hecho.descripcion)!?truncate(100, '...')}</p>
                            </div>
                        </#list>
                    </div>
                <#else>
                    <div id="lista-consensuados" class="lista-vacia">
                        <p class="texto-placeholder">No hay hechos consensuados aún.</p>
                    </div>
                </#if>
            </div>

            <!-- Botones -->
            <div class="form-actions">
                <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
                <button type="submit" class="btn btn-primary">Guardar cambios</button>
            </div>

            <!-- Mensajes -->
            <div id="mensaje-exito" class="mensaje mensaje-exito" style="display:none;">
                ✅ Colección actualizada correctamente.
            </div>
            <div id="mensaje-error" class="mensaje mensaje-error" style="display:none;">
                ❌ Error al actualizar la colección. Intente nuevamente.
            </div>
        </form>
    </div>

    <script>
        var URL_ADMIN_EDITAR = '${URL_ADMIN!"http://localhost:8081"}';
        var COLECCION_ID_EDITAR = '${COLECCION_ID}';
        var USERNAME = '${username!""}';
        var ACCESS_TOKEN = '${access_token!""}';
    </script>
    <script src="/js/editar-coleccion.js" defer></script>
</#assign>

<#include "layout.ftl">