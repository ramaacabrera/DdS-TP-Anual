<#assign pageTitle = "Crear Colección">
<#assign additionalCss = ["/css/styleCrearColeccion.css","/css/styleCrearHechoSolEliminacion.css"]>
<#assign content>
    <div class="container">
        <div class="header" style="border-bottom:1px solid var(--border-color); padding-bottom:15px; margin-bottom:25px;">
            <a href="/admin/panel" class="header-link back-link">&larr; Volver al panel</a>
        </div>

        <h1 class="main-title">Crear nueva colección</h1>

        <#if resultado??>
            <h2 style="text-align: center">${resultado}</h2>
        </#if>

        <script>
            const URL_ADMIN = '${urlAdmin}';
        </script>

        <form id="form-crear-coleccion" class="form-container" method="POST" action="/colecciones">
            <div class="form-group">
                <label for="titulo" class="form-label">Título *</label>
                <input type="text" id="titulo" name="titulo" class="form-input" required placeholder="Ej: Colección de reportes ambientales">
            </div>

            <div class="form-group">
                <label for="descripcion" class="form-label">Descripción *</label>
                <textarea id="descripcion" name="descripcion" class="form-textarea" rows="4" required placeholder="Describa brevemente la colección..."></textarea>
            </div>

            <div class="form-group">
                <label for="algoritmo" class="form-label">Algoritmo de consenso *</label>
                <select id="algoritmo" name="algoritmo" class="form-select" required>
                    <#list algoritmos as alg>
                        <option value="${alg}">${alg?capitalize}</option>
                    </#list>
                </select>
            </div>

            <div class="form-section">
                <h3 class="form-section-title">Criterios de pertenencia</h3>

                <div id="criterios-agregados" class="criterios-container">
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
                                            <input type="checkbox" name="fuentes" value="${f.fuenteId}" class="form-checkbox">
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

            <div class="form-section">
                <h3 class="form-section-title">Hechos incluidos en la colección</h3>
                <div id="lista-hechos" class="lista-vacia">
                    <p class="texto-placeholder">Aún no hay hechos asociados.</p>
                </div>
            </div>

            <div class="form-section">
                <h3 class="form-section-title">Hechos consensuados</h3>
                <div id="lista-consensuados" class="lista-vacia">
                    <p class="texto-placeholder">Aún no hay hechos consensuados.</p>
                </div>
            </div>

            <div class="form-actions">
                <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
                <button type="submit" class="btn btn-primary">Crear Colección</button>
            </div>

            <div id="mensaje-exito" class="mensaje mensaje-exito" style="display:none;">
                ✅ Colección creada exitosamente.
            </div>
            <div id="mensaje-error" class="mensaje mensaje-error" style="display:none;">
                ❌ Error al crear la colección. Intente nuevamente.
            </div>
        </form>
        <div id="modal-exito" class="modal-overlay">
            <div class="modal-content">
                <div class="modal-icon">✅</div>
                <h2 class="modal-title">¡Colección creada con éxito!</h2>
                <p class="modal-message">La colección ha sido creada exitosamente. Serás redirigido en 3 segundos...</p>
                <button id="btn-redirigir" class="modal-button">Ir a Colecciones</button>
            </div>
        </div>
    </div>
</#assign>

<script src="/js/crear-coleccion.js"></script>

<#include "layout.ftl">