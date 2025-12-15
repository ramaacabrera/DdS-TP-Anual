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
            let COLECCION_ID = '${COLECCION_ID}';
            const USERNAME = '${username!""}';
            const ACCESS_TOKEN = '${accessToken!""}';
            const FUENTES_SELECCIONADAS_IDS = '${idSeleccionados}';
        </script>

        <form id="form-editar-coleccion" class="form-container">
            <div class="form-group">
                <label for="titulo" class="form-label">Título *</label>
                <input type="text" id="titulo" name="titulo" class="form-input" value="${coleccion.titulo!''}" required>
            </div>

            <div class="form-group">
                <label for="descripcion" class="form-label">Descripción *</label>
                <textarea id="descripcion" name="descripcion" class="form-textarea" rows="4" required>${coleccion.descripcion!''}</textarea>
            </div>

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

            <div class="form-section">
                <h3 class="form-section-title">Criterios de pertenencia</h3>

                <div id="criterios-agregados" class="criterios-container">
                    <#if coleccion.criteriosDePertenencia?? && (coleccion.criteriosDePertenencia?size > 0)>
                        <#list coleccion.criteriosDePertenencia as criterio>
                            <#assign criterioId = "criterio_existente_" + criterio_index>
                            <#assign tipoCriterio = criterio.tipoCriterio!"Desconocido">

                            <#assign cardClass = "">
                            <#assign iconClass = "">
                            <#assign tituloLimpio = tipoCriterio?replace("Criterio", "")>

                            <#if tipoCriterio == "CriterioDeTexto">
                                <#assign cardClass = "type-texto">
                                <#assign iconClass = "fas fa-font">
                            <#elseif tipoCriterio == "CriterioEtiquetas">
                                <#assign cardClass = "type-etiqueta">
                                <#assign iconClass = "fas fa-tags"> <#-- 🏷️ -->
                            <#elseif tipoCriterio == "CriterioTipoMultimedia">
                                <#assign cardClass = "type-multimedia">
                                <#assign iconClass = "fas fa-photo-video"> <#-- 📷 -->
                            <#elseif tipoCriterio == "CriterioFecha">
                                <#assign cardClass = "type-fecha">
                                <#assign iconClass = "far fa-calendar-alt"> <#-- 📅 -->
                            <#elseif tipoCriterio == "CriterioUbicacion">
                                <#assign cardClass = "type-ubicacion">
                                <#assign iconClass = "fas fa-map-marker-alt"> <#-- 📍 -->
                            <#elseif tipoCriterio == "CriterioContribuyente">
                                <#assign cardClass = "type-contribuyente">
                                <#assign iconClass = "fas fa-user"> <#-- 👤 -->
                            </#if>

                            <div class="criterio-card ${cardClass}" id="${criterioId}">
                                <div class="criterio-header">
                                    <div class="criterio-info">
                                        <span class="criterio-icon"><i class="${iconClass}"></i></span>
                                        <span class="criterio-titulo">${tituloLimpio}</span>
                                    </div>
                                    <button type="button" class="btn-eliminar-criterio">
                                        <i class="fas fa-trash"></i> Eliminar
                                    </button>
                                </div>

                                <div class="criterio-body">
                                    <#if tipoCriterio == "CriterioDeTexto">
                                        <div class="dato-fila">
                                            <span class="dato-label">Tipo:</span>
                                            <span>${(criterio.tipoDeTexto)!''}</span>
                                        </div>
                                        <div class="dato-fila" style="align-items: flex-start;">
                                            <span class="dato-label">Palabras:</span>
                                            <div class="badge-container">
                                                <#if criterio.palabras??>
                                                    <#list criterio.palabras as palabra>
                                                        <span class="badge-item">${palabra}</span>
                                                        <input type="hidden" name="criterios[${criterioId}].palabras" value="${palabra}">
                                                    </#list>
                                                <#else>
                                                    <span class="text-muted">-</span>
                                                </#if>
                                            </div>
                                        </div>
                                        <input type="hidden" name="criterios[${criterioId}].tipoDeTexto" value="${(criterio.tipoDeTexto)!''}">

                                    <#elseif tipoCriterio == "CriterioEtiquetas">
                                        <div class="dato-fila" style="align-items: flex-start;">
                                            <span class="dato-label">Etiquetas:</span>
                                            <div class="badge-container">
                                                <#if criterio.etiquetas??>
                                                    <#list criterio.etiquetas as etiqueta>
                                                        <span class="badge-item">#${etiqueta.nombre}</span>
                                                        <input type="hidden" name="criterios[${criterioId}].etiquetas" value="${etiqueta.nombre}">
                                                    </#list>
                                                <#else>
                                                    <span class="text-muted">-</span>
                                                </#if>
                                            </div>
                                        </div>

                                    <#elseif tipoCriterio == "CriterioTipoMultimedia">
                                        <div class="dato-fila">
                                            <span class="dato-label">Formato:</span>
                                            <strong>${(criterio.tipoContenidoMultimedia)!''}</strong>
                                        </div>
                                        <input type="hidden" name="criterios[${criterioId}].tipoContenidoMultimedia" value="${(criterio.tipoContenidoMultimedia)!''}">

                                    <#elseif tipoCriterio == "CriterioFecha">
                                        <#assign fechaInicioStr = ''>
                                        <#if criterio.fechaInicio??>
                                            <#assign fechaInicioDate = criterio.fechaInicio?long?number_to_datetime>
                                            <#assign fechaInicioStr = fechaInicioDate?string('yyyy-MM-dd')>
                                        </#if>
                                        <#assign fechaFinStr = ''>
                                        <#if criterio.fechaFin??>
                                            <#assign fechaFinDate = criterio.fechaFin?long?number_to_datetime>
                                            <#assign fechaFinStr = fechaFinDate?string('yyyy-MM-dd')>
                                        </#if>

                                        <div class="dato-fila">
                                            <span class="dato-label">Rango:</span>
                                            <span>${fechaInicioStr} <strong>al</strong> ${fechaFinStr}</span>
                                        </div>
                                        <div class="dato-fila">
                                            <span class="dato-label">Aplica a:</span>
                                            <span>${(criterio.tipoFecha == 'fechaDeCarga')?then('Fecha de Carga', 'Fecha del Hecho')}</span>
                                        </div>
                                        <input type="hidden" name="criterios[${criterioId}].fechaInicio" value="${fechaInicioStr}">
                                        <input type="hidden" name="criterios[${criterioId}].fechaFin" value="${fechaFinStr}">
                                        <input type="hidden" name="criterios[${criterioId}].tipoFecha" value="${(criterio.tipoFecha)!''}">

                                    <#elseif tipoCriterio == "CriterioUbicacion">
                                        <div class="dato-fila">
                                            <span class="dato-label">Lugar:</span>
                                            <strong>${(criterio.descripcion)!''}</strong>
                                        </div>
                                        <input type="hidden" name="criterios[${criterioId}].descripcion" value="${(criterio.descripcion)!''}">

                                    <#elseif tipoCriterio == "CriterioContribuyente">
                                        <div class="dato-fila">
                                            <span class="dato-label">Usuario:</span>
                                            <span>@${(criterio.nombreContribuyente)!''}</span>
                                        </div>
                                        <input type="hidden" name="criterios[${criterioId}].nombreContribuyente" value="${(criterio.nombreContribuyente)!''}">
                                    </#if>

                                    <input type="hidden" name="criterios[${criterioId}].@type" value="${tipoCriterio}">
                                </div>
                            </div>
                        </#list>
                    </#if>
                </div>

                <div class="nuevo-criterio-card">
                    <h4 class="form-subtitle"><i class="fas fa-plus-circle"></i> Agregar Criterio de Pertenencia</h4>
                    <div class="criterio-controls">
                        <div class="form-group" style="text-align: left;">
                            <label for="tipoCriterio" class="form-label">Seleccione el tipo de criterio:</label>
                            <select id="tipoCriterio" class="form-select">
                                <option value="">-- Seleccionar --</option>
                                <option value="CriterioDeTexto">Texto (Título/Descripción)</option>
                                <option value="CriterioTipoMultimedia">Tipo de Multimedia</option>
                                <option value="CriterioEtiquetas">Etiquetas</option>
                                <option value="CriterioFecha">Rango de Fechas</option>
                                <option value="CriterioUbicacion">Ubicación Geográfica</option>
                                <option value="CriterioContribuyente">Contribuyente</option>
                            </select>
                        </div>

                        <div id="campoCriterio" class="campo-criterio-dinamico" style="text-align: left;"></div>

                        <button type="button" id="btn-agregar-criterio" class="btn btn-primary" style="width: 100%;" disabled>
                            + Insertar Criterio
                        </button>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h3 class="form-section-title">Fuentes</h3>
                <p class="form-help">Seleccioná las fuentes que compondrán esta colección:</p>

                <#-- 1. Separamos las fuentes en grupos usando variables de FreeMarker -->
                <#assign estaticas = []>
                <#assign dinamicas = []>
                <#assign proxies = []>
                <#assign otras = []>

                <#if listaFuentes??>
                    <#list listaFuentes as f>
                        <#if f.tipoDeFuente??>
                            <#if f.tipoDeFuente == "ESTATICA">
                                <#assign estaticas = estaticas + [f]>
                            <#elseif f.tipoDeFuente == "DINAMICA">
                                <#assign dinamicas = dinamicas + [f]>
                            <#elseif f.tipoDeFuente == "PROXY">
                                <#assign proxies = proxies + [f]>
                            <#else>
                                <#assign otras = otras + [f]>
                            </#if>
                        </#if>
                    </#list>
                </#if>

                <#-- 2. Contenedor Grid para las columnas -->
                <div class="fuentes-grid-container">

                    <#-- Macro para renderizar cada columna y evitar repetir código HTML -->
                    <#macro renderGrupoFuentes titulo icono lista claseColor>
                        <div class="fuente-grupo-card">
                            <div class="fuente-grupo-header ${claseColor}">
                                <span class="grupo-icono">${icono}</span>
                                <span class="grupo-titulo">${titulo}</span>
                                <span class="badge-count">${lista?size}</span>
                            </div>
                            <div class="fuente-lista-scroll">
                                <#if lista?size == 0>
                                    <div class="fuente-vacia">No hay fuentes disponibles.</div>
                                <#else>
                                    <#list lista as f>
                                        <label class="fuente-item">
                                            <#-- IMPORTANTE: name="fuentes" para que el backend lo reciba igual que antes -->
                                            <input type="checkbox"
                                                   name="fuentes"
                                                   value="${f.fuenteId}"
                                                   class="form-checkbox"
                                            >
                                            <span class="fuente-descriptor">${f.descriptor}</span>
                                        </label>
                                    </#list>
                                </#if>
                            </div>
                        </div>
                    </#macro>

                    <#-- 3. Renderizamos los grupos -->
                    <@renderGrupoFuentes titulo="Dinámicas" icono="⚡" lista=dinamicas claseColor="header-dinamica" />
                    <@renderGrupoFuentes titulo="Estáticas" icono="📄" lista=estaticas claseColor="header-estatica" />
                    <@renderGrupoFuentes titulo="Proxy / Externas" icono="🔗" lista=proxies claseColor="header-proxy" />

                    <#-- Opcional: Si hubiera tipos desconocidos -->
                    <#if otras?size != 0>
                        <@renderGrupoFuentes titulo="Otras" icono="❓" lista=otras claseColor="header-otras" />
                    </#if>
                </div>
            </div>

            <div class="form-actions">
                <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
                <button type="submit" class="btn btn-primary">Guardar cambios</button>
            </div>

            <div id="mensaje-exito" class="mensaje mensaje-exito" style="display:none;">
                ✅ Colección actualizada correctamente.
            </div>
            <div id="mensaje-error" class="mensaje mensaje-error" style="display:none;">
                ❌ Error al actualizar la colección. Intente nuevamente.
            </div>
        </form>
    </div>
    <script src="/js/editar-coleccion.js" defer></script>
</#assign>

<#include "layout.ftl">