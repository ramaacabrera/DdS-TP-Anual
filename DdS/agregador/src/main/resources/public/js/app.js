const API_URL = '/graphql';
let searchStartTime;

// Construye la query GraphQL basada en los filtros del formulario
function buildQuery() {
    const filters = {};

    // Recoger valores de los filtros
    const titulo = document.getElementById('titulo').value.trim();
    const categoria = document.getElementById('categoria').value.trim();
    const ubicacion = document.getElementById('ubicacion').value.trim();
    const etiquetas = document.getElementById('etiquetas').value.trim();
    const fechaAcontecimientoDesde = document.getElementById('fechaAcontecimientoDesde').value;
    const fechaAcontecimientoHasta = document.getElementById('fechaAcontecimientoHasta').value;
    const fechaCargaDesde = document.getElementById('fechaCargaDesde').value;
    const fechaCargaHasta = document.getElementById('fechaCargaHasta').value;
    const contribuyente = document.getElementById('contribuyente').value.trim();
    const tipoDeFuente = document.getElementById('tipoDeFuente').value;
    const page = parseInt(document.getElementById('page').value) || 0;
    const size = parseInt(document.getElementById('size').value) || 10;

    // Agregar filtros si tienen valor
    if (titulo) filters.titulo = `"${titulo}"`;
    if (categoria) filters.categoria = `"${categoria}"`;
    if (ubicacion) filters.ubicacion = `"${ubicacion}"`;
    if (contribuyente) filters.contribuyente = `"${contribuyente}"`;
    if (tipoDeFuente) filters.tipoDeFuente = `"${tipoDeFuente}"`;
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
    if (document.getElementById('fieldFechaAcontecimiento').checked) {
        fields.push('fechaDeAcontecimiento');
    }
    if (document.getElementById('fieldFechaCarga').checked) {
        fields.push('fechaDeCarga');
    }
    if (document.getElementById('fieldEstadoHecho').checked) {
        fields.push('estadoHecho');
    }

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

    // Mostrar la query generada
    const queryElement = document.getElementById('generatedQuery');
    queryElement.textContent = query;

    // Resaltar sintaxis
    if (window.hljs) {
        hljs.highlightElement(queryElement);
    }

    return query;
}

// Ejecuta la búsqueda de hechos
// Ejecuta la búsqueda de hechos
async function searchHechos() {
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
    resultOutput.innerHTML = '<p class="empty-state"><i class="fas fa-spinner fa-spin fa-2x"></i><br>Buscando hechos...</p>';
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

        // DEBUG: Ver qué datos llegan del servidor
        console.log("Respuesta completa:", data);
        if (data.data && data.data.hechos && data.data.hechos.content.length > 0) {
            console.log("Primer hecho recibido:", data.data.hechos.content[0]);
            console.log("Campos disponibles:", Object.keys(data.data.hechos.content[0]));
        }

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
        } else if (data.data && data.data.hechos) {
            const hechos = data.data.hechos;
            const content = hechos.content;

            // Mostrar estadísticas
            statsText.innerHTML = `
                <strong>${hechos.totalElements}</strong> hechos totales |
                Página <strong>${hechos.page + 1}</strong> de <strong>${hechos.totalPages}</strong> |
                Mostrando <strong>${content.length}</strong> resultados
            `;
            responseStats.classList.add('show');

            // Formatear resultados
            if (content.length === 0) {
                resultOutput.innerHTML = `
                    <div class="empty-state">
                        <i class="fas fa-inbox fa-3x"></i>
                        <h3>No se encontraron hechos</h3>
                        <p>Intenta con otros criterios de búsqueda</p>
                    </div>
                `;
            } else {
                let html = '<div class="results-grid">';
                content.forEach(hecho => {
                    html += `
                        <div class="hecho-card">
                            <h3>${hecho.titulo || 'Sin título'}</h3>
                            <div class="metadata">
                    `;

                    // ID (siempre visible)
                    if (hecho.id) html += `<div><strong>ID:</strong> ${hecho.id}</div>`;

                    // Título (según checkbox)
                    if (document.getElementById('fieldTitulo').checked && hecho.titulo) {
                        html += `<div><strong>Título:</strong> ${hecho.titulo}</div>`;
                    }

                    // Descripción (según checkbox)
                    if (document.getElementById('fieldDescripcion').checked && hecho.descripcion) {
                        const descCorta = hecho.descripcion.substring(0, 100);
                        const tieneMas = hecho.descripcion.length > 100 ? '...' : '';
                        html += `<div><strong>Descripción:</strong> ${descCorta}${tieneMas}</div>`;
                    }

                    // Categoría (según checkbox)
                    if (document.getElementById('fieldCategoria').checked && hecho.categoria) {
                        html += `<div><strong>Categoría:</strong> ${hecho.categoria}</div>`;
                    }

                    // Fecha Acontecimiento (según checkbox)
                    if (document.getElementById('fieldFechaAcontecimiento').checked && hecho.fechaDeAcontecimiento) {
                        const fechaAcon = new Date(hecho.fechaDeAcontecimiento).toLocaleDateString('es-AR');
                        html += `<div><strong>Fecha Acontecimiento:</strong> ${fechaAcon}</div>`;
                    }

                    // Fecha Carga (según checkbox)
                    if (document.getElementById('fieldFechaCarga').checked && hecho.fechaDeCarga) {
                        const fechaCarga = new Date(hecho.fechaDeCarga).toLocaleDateString('es-AR');
                        html += `<div><strong>Fecha Carga:</strong> ${fechaCarga}</div>`;
                    }

                    // Estado Hecho (según checkbox)
                    if (document.getElementById('fieldEstadoHecho').checked && hecho.estadoHecho) {
                        html += `<div><strong>Estado:</strong> ${hecho.estadoHecho}</div>`;
                    }

                    // Ubicación (según checkbox)
                    if (document.getElementById('fieldUbicacion').checked && hecho.ubicacion) {
                        if (hecho.ubicacion.descripcion) {
                            html += `<div><strong>Ubicación:</strong> ${hecho.ubicacion.descripcion}</div>`;
                        }
                        if (hecho.ubicacion.latitud && hecho.ubicacion.longitud) {
                            html += `<div><strong>Coordenadas:</strong> ${hecho.ubicacion.latitud.toFixed(6)}, ${hecho.ubicacion.longitud.toFixed(6)}</div>`;
                        }
                    }

                    // Fuente (según checkbox)
                    if (document.getElementById('fieldFuente').checked && hecho.fuente) {
                        if (hecho.fuente.tipoDeFuente) {
                            html += `<div><strong>Tipo de Fuente:</strong> ${hecho.fuente.tipoDeFuente}</div>`;
                        }
                        if (hecho.fuente.nombre) {
                            html += `<div><strong>Fuente:</strong> ${hecho.fuente.nombre}</div>`;
                        }
                    }

                    // Etiquetas (según checkbox)
                    if (document.getElementById('fieldEtiquetas').checked && hecho.etiquetas && hecho.etiquetas.length > 0) {
                        const tags = hecho.etiquetas.map(e => e.nombre).join(', ');
                        html += `<div><strong>Etiquetas:</strong> ${tags}</div>`;
                    }

                    // Contribuyente (según checkbox)
                    if (document.getElementById('fieldContribuyente').checked && hecho.contribuyente) {
                        if (hecho.contribuyente.username) {
                            html += `<div><strong>Contribuyente:</strong> ${hecho.contribuyente.username}</div>`;
                        }
                        if (hecho.contribuyente.id) {
                            html += `<div><strong>ID Contribuyente:</strong> ${hecho.contribuyente.id}</div>`;
                        }
                    }

                    html += `
                            </div>
                        </div>
                    `;
                });
                html += '</div>';
                resultOutput.innerHTML = html;
            }

            resultCount.textContent = `${content.length} de ${hechos.totalElements} hechos`;
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

// Limpia todos los filtros
function clearFilters() {
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
    document.getElementById('page').value = '0';
    document.getElementById('size').value = '10';

    // Resetear checkboxes
    document.querySelectorAll('.checkbox-item input[type="checkbox"]').forEach(cb => {
        if (cb.id !== 'fieldId') cb.checked = false;
    });
    /*document.getElementById('fieldTitulo').checked = true;
    document.getElementById('fieldCategoria').checked = true;
    document.getElementById('fieldUbicacion').checked = true;
    document.getElementById('fieldEtiquetas').checked = true;*/

    // Limpiar resultados
    document.getElementById('resultOutput').innerHTML = `
        <p class="empty-state">
            <i class="fas fa-search fa-2x"></i>
            Ingresa los criterios de búsqueda y haz clic en "Buscar Hechos"
        </p>
    `;

    document.getElementById('generatedQuery').textContent = '// La query aparecerá aquí cuando realices una búsqueda';
    document.getElementById('statusText').textContent = 'Listo';
    document.getElementById('responseTime').textContent = '-';
    document.getElementById('resultCount').textContent = '0 resultados';
    document.getElementById('responseStats').classList.remove('show');
    statusDot.className = 'status-dot';
}

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Ejecutar búsqueda al presionar Enter en cualquier campo
    document.querySelectorAll('input, select').forEach(element => {
        element.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchHechos();
            }
        });
    });

    // Generar query automáticamente al cambiar filtros
    document.querySelectorAll('#titulo, #categoria, #ubicacion, #etiquetas, #fechaAcontecimientoDesde, #fechaAcontecimientoHasta, #fechaCargaDesde, #fechaCargaHasta, #contribuyente, #tipoDeFuente, #page, #size, .checkbox-item input')
        .forEach(element => {
            element.addEventListener('change', buildQuery);
            element.addEventListener('input', buildQuery);
        });

    // Inicializar con una query básica
    buildQuery();
});