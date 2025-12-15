const API_URL = '/graphql';
let searchStartTime;
let currentEntity = 'hechos';

// Cambiar entre entidades
function switchEntity(entity) {
    currentEntity = entity;

    // Actualizar botones
    document.querySelectorAll('.selector-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');

    // Mostrar/ocultar filtros
    document.querySelectorAll('.entity-filters').forEach(filter => {
        filter.classList.remove('active');
    });

    if (entity === 'hechos') {
        document.getElementById('hechos-filtros').classList.add('active');
    } else if (entity === 'colecciones') {
        document.getElementById('colecciones-filtros').classList.add('active');
    } else if (entity === 'ambos') {
        document.getElementById('hechos-filtros').classList.add('active');
        document.getElementById('colecciones-filtros').classList.add('active');
    }

    buildQuery();
}

// Construir la query según la entidad seleccionada
function buildQuery() {
    let query;

    if (currentEntity === 'hechos') {
        query = buildHechosQuery();
    } else if (currentEntity === 'colecciones') {
        query = buildColeccionesQuery();
    } else if (currentEntity === 'ambos') {
        query = buildCombinedQuery();
    }

    // Mostrar la query generada
    const queryElement = document.getElementById('generatedQuery');
    queryElement.textContent = query;

    if (window.hljs) {
        hljs.highlightElement(queryElement);
    }

    return query;
}

// Construir query de hechos
function buildHechosQuery() {
    const filters = {};

    // Recoger valores de los filtros de hechos
    const titulo = document.getElementById('titulo').value.trim();
    const categoria = document.getElementById('categoria').value.trim();
    const ubicacion = document.getElementById('ubicacion').value.trim();
    const etiquetas = document.getElementById('etiquetas').value.trim();
    const fechaAcontecimientoDesde = document.getElementById('fechaAcontecimientoDesde').value;
    const fechaAcontecimientoHasta = document.getElementById('fechaAcontecimientoHasta').value;
    const fechaCargaDesde = document.getElementById('fechaCargaDesde').value;
    const fechaCargaHasta = document.getElementById('fechaCargaHasta').value;
    const contribuyente = document.getElementById('contribuyente').value.trim();
    const tipoFuente = document.getElementById('tipoDeFuente').value;
    const page = parseInt(document.getElementById('page').value) || 0;
    const size = parseInt(document.getElementById('size').value) || 10;

    // Agregar filtros si tienen valor
    if (titulo) filters.titulo = `"${titulo}"`;
    if (categoria) filters.categoria = `"${categoria}"`;
    if (ubicacion) filters.ubicacion = `"${ubicacion}"`;
    if (contribuyente) filters.contribuyente = `"${contribuyente}"`;
    if (tipoFuente) filters.tipoDeFuente = `"${tipoFuente}"`;
    if (fechaAcontecimientoDesde) filters.fechaAcontecimientoDesde = `"${fechaAcontecimientoDesde}"`;
    if (fechaAcontecimientoHasta) filters.fechaAcontecimientoHasta = `"${fechaAcontecimientoHasta}"`;
    if (fechaCargaDesde) filters.fechaCargaDesde = `"${fechaCargaDesde}"`;
    if (fechaCargaHasta) filters.fechaCargaHasta = `"${fechaCargaHasta}"`;
    if (etiquetas) {
        const etiquetasArray = etiquetas.split(',').map(e => `"${e.trim()}"`);
        filters.etiquetas = `[${etiquetasArray.join(', ')}]`;
    }

    // Determinar qué campos incluir en la respuesta
    const fields = [];
    if (document.getElementById('fieldTitulo').checked) fields.push('titulo');
    if (document.getElementById('fieldDescripcion').checked) fields.push('descripcion');
    if (document.getElementById('fieldCategoria').checked) fields.push('categoria');
    if (document.getElementById('fieldFechaAcontecimiento').checked) fields.push('fechaDeAcontecimiento');
    if (document.getElementById('fieldFechaCarga').checked) fields.push('fechaDeCarga');
    if (document.getElementById('fieldEstadoHecho').checked) fields.push('estadoHecho');

    // Campos con sub-objetos
    const ubicacionFields = [];
    if (document.getElementById('fieldUbicacion').checked) {
        ubicacionFields.push('latitud', 'longitud', 'descripcion');
    }

    const fuenteFields = [];
    if (document.getElementById('fieldFuente').checked) {
        fuenteFields.push('tipoDeFuente');
    }

    const etiquetasFields = [];
    if (document.getElementById('fieldEtiquetas').checked) {
        etiquetasFields.push('nombre');
    }

    const contribuyenteFields = [];
    if (document.getElementById('fieldContribuyente').checked) {
        contribuyenteFields.push('username');
    }

    // Construir la parte del filtro
    let filterString = '';
    if (Object.keys(filters).length > 0) {
        filterString = `filtro: { ${Object.entries(filters).map(([key, value]) => `${key}: ${value}`).join(', ')} }`;
    }

    // Construir la query completa
    let query = `{
  hechos(${filterString ? filterString + ', ' : ''}page: { page: ${page}, size: ${size} }) {
    content {
      id`;

    // Agregar campos seleccionados
    if (fields.length > 0) {
        query += `\n      ${fields.join('\n      ')}`;
    }

    // Agregar sub-objetos
    if (ubicacionFields.length > 0) {
        query += `\n      ubicacion {
        ${ubicacionFields.join('\n        ')}
      }`;
    }

    if (fuenteFields.length > 0) {
        query += `\n      fuente {
        ${fuenteFields.join('\n        ')}
      }`;
    }

    if (etiquetasFields.length > 0) {
        query += `\n      etiquetas {
        ${etiquetasFields.join('\n        ')}
      }`;
    }

    if (contribuyenteFields.length > 0) {
        query += `\n      contribuyente {
        ${contribuyenteFields.join('\n        ')}
      }`;
    }

    query += `\n    }
    page
    size
    totalPages
    totalElements
  }
}`;

    return query;
}

// Construir query de colecciones
function buildColeccionesQuery() {
    const filters = {};

    // Recoger valores de los filtros de colecciones
    const titulo = document.getElementById('coleccionTitulo').value.trim();
    const descripcion = document.getElementById('coleccionDescripcion').value.trim();
    const algoritmo = document.getElementById('algoritmoConsenso').value;
    const minHechos = document.getElementById('minHechos').value;
    const page = parseInt(document.getElementById('page').value) || 0;
    const size = parseInt(document.getElementById('size').value) || 10;

    // Agregar filtros si tienen valor
    if (titulo) filters.titulo = `"${titulo}"`;
    if (descripcion) filters.descripcion = `"${descripcion}"`;
    if (algoritmo) filters.algoritmoDeConsenso = `"${algoritmo}"`;
    if (minHechos) filters.minHechos = minHechos;

    // Determinar qué campos incluir
    const fields = [];
    if (document.getElementById('coleccionFieldTitulo').checked) fields.push('titulo');
    if (document.getElementById('coleccionFieldDescripcion').checked) fields.push('descripcion');
    if (document.getElementById('coleccionFieldAlgoritmo').checked) fields.push('algoritmoDeConsenso');

    // Campos con sub-objetos
    const hechosFields = [];
    if (document.getElementById('coleccionFieldHechos').checked) {
        // Campos básicos siempre
        hechosFields.push('id', 'titulo');

        // Campos según selección
        if (document.getElementById('hechoFieldCategoria').checked) hechosFields.push('categoria');
        if (document.getElementById('hechoFieldDescripcion').checked) hechosFields.push('descripcion');
        if (document.getElementById('hechoFieldFecha').checked) {
            hechosFields.push('fechaDeAcontecimiento');
            hechosFields.push('fechaDeCarga');
        }
        if (document.getElementById('hechoFieldUbicacion').checked) {
            hechosFields.push('ubicacion { descripcion }');
        }
        if (document.getElementById('hechoFieldEtiquetas').checked) {
            hechosFields.push('etiquetas { nombre }');
        }
    }

    const hechosConsensuadosFields = [];
    if (document.getElementById('coleccionFieldHechosConsensuados').checked) {
        // Mismos campos que para hechos normales
        hechosConsensuadosFields.push('id', 'titulo');
        if (document.getElementById('hechoFieldCategoria').checked) hechosConsensuadosFields.push('categoria');
        if (document.getElementById('hechoFieldDescripcion').checked) hechosConsensuadosFields.push('descripcion');
        if (document.getElementById('hechoFieldFecha').checked) {
            hechosConsensuadosFields.push('fechaDeAcontecimiento');
            hechosConsensuadosFields.push('fechaDeCarga');
        }
        if (document.getElementById('hechoFieldUbicacion').checked) {
            hechosConsensuadosFields.push('ubicacion { descripcion }');
        }
        if (document.getElementById('hechoFieldEtiquetas').checked) {
            hechosConsensuadosFields.push('etiquetas { nombre }');
        }
    }

    const fuentesFields = [];
    if (document.getElementById('coleccionFieldFuentes').checked) {
        fuentesFields.push('id', 'tipoDeFuente');
    }

    // Construir la parte del filtro
    let filterString = '';
    if (Object.keys(filters).length > 0) {
        filterString = `filtro: { ${Object.entries(filters).map(([key, value]) => `${key}: ${value}`).join(', ')} }`;
    }

    // Construir la query completa
    let query = `{
  colecciones(${filterString ? filterString + ', ' : ''}page: { page: ${page}, size: ${size} }) {
    content {
      id`;

    // Agregar campos seleccionados
    if (fields.length > 0) {
        query += `\n      ${fields.join('\n      ')}`;
    }

    // Agregar sub-objetos
    if (hechosFields.length > 0) {
        query += `\n      hechos {
        ${hechosFields.join('\n        ')}
      }`;
    }

    if (hechosConsensuadosFields.length > 0) {
        query += `\n      hechosConsensuados {
        ${hechosConsensuadosFields.join('\n        ')}
      }`;
    }

    if (fuentesFields.length > 0) {
        query += `\n      fuentes {
        ${fuentesFields.join('\n        ')}
      }`;
    }

    query += `\n    }
    page
    size
    totalPages
    totalElements
  }
}`;

    return query;
}

// Construir query combinada
function buildCombinedQuery() {
    // Obtener la query de hechos y colecciones
    const hechosQuery = buildHechosQuery().replace(/^\s*\{|\}\s*$/g, '').trim();
    const coleccionesQuery = buildColeccionesQuery().replace(/^\s*\{|\}\s*$/g, '').trim();

    // Combinar
    return `{
${hechosQuery}

${coleccionesQuery}
}`;
}

// Función de búsqueda principal
async function search() {
    const query = buildQuery();
    const resultOutput = document.getElementById('resultOutput');
    const statusDot = document.getElementById('statusDot');
    const statusText = document.getElementById('statusText');
    const responseTime = document.getElementById('responseTime');
    const resultCount = document.getElementById('resultCount');
    const responseStats = document.getElementById('responseStats');
    const statsText = document.getElementById('statsText');

    // Actualizar UI a estado de carga
    searchStartTime = Date.now();
    statusDot.className = 'status-dot loading';
    statusText.textContent = 'Buscando...';
    resultOutput.innerHTML = '<p class="empty-state"><i class="fas fa-spinner fa-spin fa-2x"></i><br>Realizando búsqueda...</p>';
    responseStats.classList.remove('show');

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                query: query,
                variables: {}
            })
        });

        const endTime = Date.now();
        const executionTime = endTime - searchStartTime;

        const data = await response.json();

        // Actualizar estado
        statusDot.className = 'status-dot';
        statusText.textContent = 'Completado';
        responseTime.textContent = `${executionTime}ms`;

        // Mostrar resultados
        if (data.errors) {
            resultOutput.innerHTML = `<div class="error-message">
                <h3><i class="fas fa-exclamation-triangle"></i> Error en la consulta</h3>
                <pre>${JSON.stringify(data.errors, null, 2)}</pre>
            </div>`;
            resultCount.textContent = 'Error';
            statusDot.className = 'status-dot error';
        } else if (data.data) {
            displayResults(data.data, currentEntity);
        }

    } catch (error) {
        const endTime = Date.now();
        statusDot.className = 'status-dot error';
        statusText.textContent = 'Error';
        responseTime.textContent = `${endTime - searchStartTime}ms`;
        resultCount.textContent = 'Error de conexión';
        resultOutput.innerHTML = `<div class="error-message">
            <h3><i class="fas fa-exclamation-triangle"></i> Error de conexión</h3>
            <p>${error.message}</p>
            <p>Verifica que el servidor esté corriendo en el puerto correcto.</p>
        </div>`;
    }
}

// Mostrar resultados según el tipo de entidad
function displayResults(data, entityType) {
    const resultOutput = document.getElementById('resultOutput');
    const responseStats = document.getElementById('responseStats');
    const statsText = document.getElementById('statsText');
    const resultCount = document.getElementById('resultCount');

    if (entityType === 'hechos' && data.hechos) {
        const hechos = data.hechos;
        const content = hechos.content;

        // Mostrar estadísticas
        statsText.innerHTML = `
            <strong>${hechos.totalElements}</strong> hechos totales |
            Página <strong>${hechos.page + 1}</strong> de <strong>${hechos.totalPages}</strong> |
            Mostrando <strong>${content.length}</strong> resultados
        `;
        responseStats.classList.add('show');

        // Formatear resultados de hechos
        if (content.length === 0) {
            resultOutput.innerHTML = `<div class="empty-state">
                <i class="fas fa-inbox fa-3x"></i>
                <h3>No se encontraron hechos</h3>
                <p>Intenta con otros criterios de búsqueda</p>
            </div>`;
        } else {
            let html = '<div class="results-grid">';
            content.forEach(hecho => {
                html += formatHechoResult(hecho);
            });
            html += '</div>';
            resultOutput.innerHTML = html;
        }

        resultCount.textContent = `${content.length} de ${hechos.totalElements} hechos`;

    } else if (entityType === 'colecciones' && data.colecciones) {
        const colecciones = data.colecciones;
        const content = colecciones.content;

        // Mostrar estadísticas
        statsText.innerHTML = `
            <strong>${colecciones.totalElements}</strong> colecciones totales |
            Página <strong>${colecciones.page + 1}</strong> de <strong>${colecciones.totalPages}</strong> |
            Mostrando <strong>${content.length}</strong> resultados
        `;
        responseStats.classList.add('show');

        // Formatear resultados de colecciones
        if (content.length === 0) {
            resultOutput.innerHTML = `<div class="empty-state">
                <i class="fas fa-inbox fa-3x"></i>
                <h3>No se encontraron colecciones</h3>
                <p>Intenta con otros criterios de búsqueda</p>
            </div>`;
        } else {
            let html = '<div class="results-grid">';
            content.forEach(coleccion => {
                html += formatColeccionResult(coleccion);
            });
            html += '</div>';
            resultOutput.innerHTML = html;
        }

        resultCount.textContent = `${content.length} de ${colecciones.totalElements} colecciones`;

    } else if (entityType === 'ambos') {
        // Mostrar resultados combinados
        let html = '<div class="combined-results">';

        if (data.hechos && data.hechos.content.length > 0) {
            html += `<h3><i class="fas fa-list"></i> Hechos (${data.hechos.content.length})</h3>`;
            html += '<div class="results-grid">';
            data.hechos.content.forEach(hecho => {
                html += formatHechoResult(hecho);
            });
            html += '</div>';
        }

        if (data.colecciones && data.colecciones.content.length > 0) {
            html += `<h3><i class="fas fa-folder"></i> Colecciones (${data.colecciones.content.length})</h3>`;
            html += '<div class="results-grid">';
            data.colecciones.content.forEach(coleccion => {
                html += formatColeccionResult(coleccion);
            });
            html += '</div>';
        }

        if ((!data.hechos || data.hechos.content.length === 0) &&
            (!data.colecciones || data.colecciones.content.length === 0)) {
            html = `<div class="empty-state">
                <i class="fas fa-inbox fa-3x"></i>
                <h3>No se encontraron resultados</h3>
                <p>Intenta con otros criterios de búsqueda</p>
            </div>`;
        }

        html += '</div>';
        resultOutput.innerHTML = html;

        // Actualizar contadores
        const totalHechos = data.hechos ? data.hechos.content.length : 0;
        const totalColecciones = data.colecciones ? data.colecciones.content.length : 0;
        resultCount.textContent = `${totalHechos + totalColecciones} resultados`;
        responseStats.classList.add('show');
    }
}

// Formatear resultado de un hecho
function formatHechoResult(hecho) {
    let html = `<div class="hecho-card">
        <h3>${hecho.titulo || 'Sin título'}</h3>
        <div class="metadata">`;

    if (hecho.id) html += `<div><strong>ID:</strong> ${hecho.id}</div>`;
    if (document.getElementById('fieldTitulo').checked && hecho.titulo) {
        html += `<div><strong>Título:</strong> ${hecho.titulo}</div>`;
    }
    if (document.getElementById('fieldDescripcion').checked && hecho.descripcion) {
        const descCorta = hecho.descripcion.substring(0, 100);
        const tieneMas = hecho.descripcion.length > 100 ? '...' : '';
        html += `<div><strong>Descripción:</strong> ${descCorta}${tieneMas}</div>`;
    }
    if (document.getElementById('fieldCategoria').checked && hecho.categoria) {
        html += `<div><strong>Categoría:</strong> ${hecho.categoria}</div>`;
    }
    if (document.getElementById('fieldFechaAcontecimiento').checked && hecho.fechaDeAcontecimiento) {
        const fechaAcon = new Date(hecho.fechaDeAcontecimiento).toLocaleDateString('es-AR');
        html += `<div><strong>Fecha Acontecimiento:</strong> ${fechaAcon}</div>`;
    }
    if (document.getElementById('fieldFechaCarga').checked && hecho.fechaDeCarga) {
        const fechaCarga = new Date(hecho.fechaDeCarga).toLocaleDateString('es-AR');
        html += `<div><strong>Fecha Carga:</strong> ${fechaCarga}</div>`;
    }
    if (document.getElementById('fieldEstadoHecho').checked && hecho.estadoHecho) {
        html += `<div><strong>Estado:</strong> ${hecho.estadoHecho}</div>`;
    }
    if (document.getElementById('fieldUbicacion').checked && hecho.ubicacion && hecho.ubicacion.descripcion) {
        html += `<div><strong>Ubicación:</strong> ${hecho.ubicacion.descripcion}</div>`;
    }
    if (document.getElementById('fieldFuente').checked && hecho.fuente && hecho.fuente.tipoDeFuente) {
        html += `<div><strong>Tipo de Fuente:</strong> ${hecho.fuente.tipoDeFuente}</div>`;
    }
    if (document.getElementById('fieldEtiquetas').checked && hecho.etiquetas && hecho.etiquetas.length > 0) {
        const tags = hecho.etiquetas.map(e => e.nombre).join(', ');
        html += `<div><strong>Etiquetas:</strong> ${tags}</div>`;
    }
    if (document.getElementById('fieldContribuyente').checked && hecho.contribuyente && hecho.contribuyente.username) {
        html += `<div><strong>Contribuyente:</strong> ${hecho.contribuyente.username}</div>`;
    }

    html += `</div></div>`;
    return html;
}

// Formatear resultado de una colección
function formatColeccionResult(coleccion) {
    let html = `<div class="coleccion-card">
        <h3>${coleccion.titulo || 'Sin título'}</h3>
        <div class="metadata">`;

    // Campos básicos
    if (coleccion.id) html += `<div><strong>ID:</strong> ${coleccion.id}</div>`;
    if (document.getElementById('coleccionFieldTitulo').checked && coleccion.titulo) {
        html += `<div><strong>Título:</strong> ${coleccion.titulo}</div>`;
    }
    if (document.getElementById('coleccionFieldDescripcion').checked && coleccion.descripcion) {
        const descCorta = coleccion.descripcion.substring(0, 100);
        const tieneMas = coleccion.descripcion.length > 100 ? '...' : '';
        html += `<div><strong>Descripción:</strong> ${descCorta}${tieneMas}</div>`;
    }
    if (document.getElementById('coleccionFieldAlgoritmo').checked && coleccion.algoritmoDeConsenso) {
        html += `<div><strong>Algoritmo:</strong> ${coleccion.algoritmoDeConsenso}</div>`;
    }

    // Mostrar hechos con formato mejorado
    if (document.getElementById('coleccionFieldHechos').checked && coleccion.hechos && coleccion.hechos.length > 0) {
        html += `<div class="hechos-list">
            <strong>Hechos (${coleccion.hechos.length}):</strong>
            <div class="hechos-grid">`;

        coleccion.hechos.forEach(hecho => {
            html += `<div class="hecho-mini-card">
                <div class="hecho-mini-titulo">${hecho.titulo || 'Sin título'}</div>`;

            if (document.getElementById('hechoFieldCategoria').checked && hecho.categoria) {
                html += `<div class="hecho-mini-campo"><span class="hecho-mini-label">Categoría:</span> ${hecho.categoria}</div>`;
            }
            if (document.getElementById('hechoFieldDescripcion').checked && hecho.descripcion) {
                const descCorta = hecho.descripcion.substring(0, 60);
                const tieneMas = hecho.descripcion.length > 60 ? '...' : '';
                html += `<div class="hecho-mini-campo"><span class="hecho-mini-label">Descripción:</span> ${descCorta}${tieneMas}</div>`;
            }
            if (document.getElementById('hechoFieldFecha').checked && hecho.fechaDeAcontecimiento) {
                const fecha = new Date(hecho.fechaDeAcontecimiento).toLocaleDateString('es-AR');
                html += `<div class="hecho-mini-campo"><span class="hecho-mini-label">Fecha:</span> ${fecha}</div>`;
            }
            if (document.getElementById('hechoFieldUbicacion').checked && hecho.ubicacion && hecho.ubicacion.descripcion) {
                html += `<div class="hecho-mini-campo"><span class="hecho-mini-label">Ubicación:</span> ${hecho.ubicacion.descripcion}</div>`;
            }
            if (document.getElementById('hechoFieldEtiquetas').checked && hecho.etiquetas && hecho.etiquetas.length > 0) {
                const tags = hecho.etiquetas.map(e => e.nombre).slice(0, 3).join(', ');
                const masTags = hecho.etiquetas.length > 3 ? '...' : '';
                html += `<div class="hecho-mini-campo"><span class="hecho-mini-label">Etiquetas:</span> ${tags}${masTags}</div>`;
            }

            html += `</div>`;
        });

        html += `</div></div>`;
    }

    // Mostrar hechos consensuados con mismo formato
    if (document.getElementById('coleccionFieldFuentes').checked && coleccion.fuentes && coleccion.fuentes.length > 0) {
        // Agrupar fuentes por tipo y contar
        const fuentesPorTipo = {};
        coleccion.fuentes.forEach(fuente => {
            const tipo = fuente.tipoDeFuente || 'Sin tipo';
            if (!fuentesPorTipo[tipo]) {
                fuentesPorTipo[tipo] = 0;
            }
            fuentesPorTipo[tipo]++;
        });

        html += `<div class="fuentes-list">
        <strong>Fuentes (${coleccion.fuentes.length}):</strong>
        <div class="fuentes-tags">`;

        // Mostrar cada tipo con su cantidad
        Object.entries(fuentesPorTipo).forEach(([tipo, cantidad]) => {
            html += `<span class="fuente-tag">
            ${tipo} 
            <span class="fuente-cantidad">${cantidad}</span>
        </span>`;
        });

        html += `</div></div>`;
    }
    

    html += `</div></div>`;
    return html;
}

// Limpiar todos los filtros
function clearFilters() {
    // Limpiar filtros de hechos
    document.getElementById('titulo').value = '';
    document.getElementById('categoria').value = '';
    document.getElementById('ubicacion').value = '';
    document.getElementById('etiquetas').value = '';
    document.getElementById('fechaAcontecimientoDesde').value = '';
    document.getElementById('fechaAcontecimientoHasta').value = '';
    document.getElementById('fechaCargaDesde').value = '';
    document.getElementById('fechaCargaHasta').value = '';
    document.getElementById('contribuyente').value = '';
    document.getElementById('tipoDeFuente').value = '';

    // Limpiar filtros de colecciones
    document.getElementById('coleccionTitulo').value = '';
    document.getElementById('coleccionDescripcion').value = '';
    document.getElementById('algoritmoConsenso').value = '';
    document.getElementById('minHechos').value = '';

    // Paginación común
    document.getElementById('page').value = '0';
    document.getElementById('size').value = '10';

    // Resetear checkboxes de hechos (todos marcados excepto ID)
    document.querySelectorAll('#hechos-filtros .checkbox-item input[type="checkbox"]').forEach(cb => {
        if (cb.id !== 'fieldId') cb.checked = true;
    });

    // Resetear checkboxes de colecciones
    document.querySelectorAll('#colecciones-filtros .checkbox-item input[type="checkbox"]').forEach(cb => {
        if (cb.id !== 'coleccionFieldId') {
            // Campos básicos marcados por defecto
            if (cb.id.includes('Titulo') || cb.id.includes('Descripcion') || cb.id.includes('Algoritmo')) {
                cb.checked = true;
            }
            // Hechos y Hechos Consensuados desmarcados (porque hacen la query pesada)
            else if (cb.id.includes('Hechos') || cb.id.includes('Fuentes')) {
                cb.checked = false;
            }
        }
    });

    // Resetear checkboxes de campos de hechos (dentro de colecciones)
    const hechosCamposOpciones = document.getElementById('hechos-campos-opciones');
    if (hechosCamposOpciones) {
        // Ocultar la sección inicialmente
        hechosCamposOpciones.style.display = 'none';

        // Resetear los checkboxes internos
        document.querySelectorAll('#hechos-campos-opciones input[type="checkbox"]').forEach(cb => {
            if (cb.id === 'hechoFieldTitulo' || cb.id === 'hechoFieldCategoria') {
                cb.checked = true; // Título y categoría marcados por defecto
            } else {
                cb.checked = false; // El resto desmarcados
            }
        });
    }

    // Si estamos en modo "ambos", resetear también la visibilidad de las secciones
    if (currentEntity === 'ambos') {
        document.getElementById('hechos-filtros').classList.add('active');
        document.getElementById('colecciones-filtros').classList.add('active');
    }

    // Limpiar resultados
    document.getElementById('resultOutput').innerHTML = `
        <p class="empty-state">
            <i class="fas fa-search fa-2x"></i>
            Selecciona el tipo de consulta y haz clic en "Buscar"
        </p>
    `;

    document.getElementById('generatedQuery').textContent = '// La query aparecerá aquí cuando realices una búsqueda';
    document.getElementById('statusText').textContent = 'Listo';
    document.getElementById('responseTime').textContent = '-';
    document.getElementById('resultCount').textContent = '0 resultados';
    document.getElementById('responseStats').classList.remove('show');
    document.getElementById('statusDot').className = 'status-dot';

    // Regenerar la query básica
    buildQuery();
}

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Ejecutar búsqueda al presionar Enter en cualquier campo
    document.querySelectorAll('input, select').forEach(element => {
        element.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                search();
            }
        });
    });

    // Generar query automáticamente al cambiar filtros
    document.querySelectorAll('input, select, .checkbox-item input').forEach(element => {
        element.addEventListener('change', buildQuery);
        element.addEventListener('input', buildQuery);
    });

    // Controlar visibilidad de opciones de campos de hechos para colecciones
    const checkboxHechos = document.getElementById('coleccionFieldHechos');
    const checkboxHechosConsensuados = document.getElementById('coleccionFieldHechosConsensuados');
    const hechosCamposOpciones = document.getElementById('hechos-campos-opciones');

    if (checkboxHechos && checkboxHechosConsensuados && hechosCamposOpciones) {
        function toggleHechosCampos() {
            if (checkboxHechos.checked || checkboxHechosConsensuados.checked) {
                hechosCamposOpciones.style.display = 'block';
            } else {
                hechosCamposOpciones.style.display = 'none';
            }
            buildQuery(); // Regenerar la query cuando cambie
        }

        checkboxHechos.addEventListener('change', toggleHechosCampos);
        checkboxHechosConsensuados.addEventListener('change', toggleHechosCampos);

        // También para los checkboxes dentro de hechos-campos-opciones
        document.querySelectorAll('#hechos-campos-opciones input[type="checkbox"]').forEach(cb => {
            cb.addEventListener('change', buildQuery);
            cb.addEventListener('input', buildQuery);
        });

        // Inicializar estado
        toggleHechosCampos();
    }

    // Inicializar con una query básica
    buildQuery();
});