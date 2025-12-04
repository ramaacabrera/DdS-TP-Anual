// /js/editar-coleccion.js

// Variables globales
var contadorCriteriosEditar = 0;

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM cargado - Inicializando eventos de edición');

    // Obtener valores de variables globales
    if (window.URL_ADMIN_EDITAR) {
        URL_ADMIN_EDITAR = window.URL_ADMIN_EDITAR;
    }
    if (window.COLECCION_ID_EDITAR) {
        COLECCION_ID_EDITAR = window.COLECCION_ID_EDITAR;
    }

    console.log('URL_ADMIN:', URL_ADMIN_EDITAR);
    console.log('COLECCION_ID:', COLECCION_ID_EDITAR);

    // Hacer criterios existentes editables
    hacerCriteriosEditables();

    // Contar criterios existentes
    const criteriosExistentes = document.querySelectorAll('.criterio-card');
    contadorCriteriosEditar = criteriosExistentes.length;
    console.log('Criterios existentes:', contadorCriteriosEditar);

    // Inicializar eventos
    inicializarEventosEditar();
});

function hacerCriteriosEditables() {
    console.log('Haciendo criterios existentes editables...');

    const criteriosCards = document.querySelectorAll('.criterio-card');

    criteriosCards.forEach((card, index) => {
        const criterioId = card.id || `criterio_existente_${index}`;
        if (!card.id) card.id = criterioId;

        // Verificar el tipo de criterio
        const tipoElement = card.querySelector('.criterio-tipo');
        if (!tipoElement) return;

        const tipoTexto = tipoElement.textContent.trim();

        if (tipoTexto.includes('Texto')) {
            hacerCriterioTextoEditable(card, criterioId);
        } else if (tipoTexto.includes('Etiquetas')) {
            hacerCriterioEtiquetasEditable(card, criterioId);
        } else if (tipoTexto.includes('Fecha')) {
            hacerCriterioFechaEditable(card, criterioId);
        } else if (tipoTexto.includes('Ubicación')) {
            hacerCriterioUbicacionEditable(card, criterioId);
        } else if (tipoTexto.includes('Contribuyente')) {
            hacerCriterioContribuyenteEditable(card, criterioId);
        } else if (tipoTexto.includes('Multimedia')) {
            hacerCriterioMultimediaEditable(card, criterioId);
        }
    });
}

function hacerCriterioTextoEditable(card, criterioId) {
    console.log('Haciendo criterio de texto editable:', criterioId);

    // Buscar todos los divs en el card
    const divs = card.querySelectorAll('div');
    let palabrasElement = null;
    let tipoTextoElement = null;

    // Buscar elementos por contenido
    divs.forEach(div => {
        const text = div.textContent || '';
        if (text.includes('Palabras:')) {
            palabrasElement = div;
        } else if (text.includes('Tipo de texto:')) {
            tipoTextoElement = div;
        }
    });

    if (!palabrasElement || !tipoTextoElement) return;

    // Extraer valores actuales
    const palabrasTexto = palabrasElement.textContent.replace('Palabras:', '').trim();
    const tipoTextoValor = tipoTextoElement.textContent.replace('Tipo de texto:', '').trim();
    const palabrasArray = palabrasTexto ? palabrasTexto.split(', ').filter(p => p) : [];

    // Reemplazar con campos editables
    palabrasElement.innerHTML = `
        <strong>Palabras clave:</strong>
        <div id="palabras-edit-${criterioId}" class="palabras-container-editable" style="margin-top: 5px;">
            ${palabrasArray.map((palabra, idx) => `
                <div class="input-palabra-group-editable" style="display: flex; align-items: center; margin-bottom: 5px;">
                    <input type="text" class="form-input palabra-input-editable"
                           value="${palabra.replace(/"/g, '&quot;')}"
                           style="flex: 1; margin-right: 5px; padding: 5px;">
                    <button type="button" class="btn btn-danger btn-eliminar-palabra"
                            style="padding: 2px 8px; ${palabrasArray.length === 1 ? 'display:none;' : ''}">✖</button>
                </div>
            `).join('')}
        </div>
        <button type="button" class="btn btn-outline-secondary btn-agregar-palabra"
                data-criterio="${criterioId}"
                style="margin-top: 5px; padding: 3px 10px; font-size: 0.9em;">
            + Agregar otra palabra
        </button>
    `;

    // Reemplazar tipo de texto con select editable
    const tipoTextoOptions = ['TITULO', 'DESCRIPCION', 'CATEGORIA', 'BUSQUEDA'];
    tipoTextoElement.innerHTML = `
        <strong>Tipo de texto:</strong>
        <select class="form-select tipo-texto-editable" style="margin-top: 5px; padding: 5px;">
            ${tipoTextoOptions.map(option => `
                <option value="${option}" ${option === tipoTextoValor ? 'selected' : ''}>
                    ${option}
                </option>
            `).join('')}
        </select>
    `;
}

function hacerCriterioEtiquetasEditable(card, criterioId) {
    console.log('Haciendo criterio de etiquetas editable:', criterioId);

    const divs = card.querySelectorAll('div');
    let etiquetasElement = null;

    divs.forEach(div => {
        const text = div.textContent || '';
        if (text.includes('Etiquetas:')) {
            etiquetasElement = div;
        }
    });

    if (!etiquetasElement) return;

    // Extraer valores actuales
    const etiquetasTexto = etiquetasElement.textContent.replace('Etiquetas:', '').trim();
    const etiquetasArray = etiquetasTexto ? etiquetasTexto.split(', ').filter(e => e) : [];

    // Reemplazar con campos editables
    etiquetasElement.innerHTML = `
        <strong>Etiquetas:</strong>
        <div id="etiquetas-edit-${criterioId}" class="etiquetas-container-editable" style="margin-top: 5px;">
            ${etiquetasArray.map((etiqueta, idx) => `
                <div class="input-etiqueta-group-editable" style="display: flex; align-items: center; margin-bottom: 5px;">
                    <input type="text" class="form-input etiqueta-input-editable"
                           value="${etiqueta.replace(/"/g, '&quot;')}"
                           style="flex: 1; margin-right: 5px; padding: 5px;">
                    <button type="button" class="btn btn-danger btn-eliminar-etiqueta"
                            style="padding: 2px 8px; ${etiquetasArray.length === 1 ? 'display:none;' : ''}">✖</button>
                </div>
            `).join('')}
        </div>
        <button type="button" class="btn btn-outline-secondary btn-agregar-etiqueta"
                data-criterio="${criterioId}"
                style="margin-top: 5px; padding: 3px 10px; font-size: 0.9em;">
            + Agregar otra etiqueta
        </button>
    `;
}

// Funciones para otros tipos de criterios (simplificadas)
function hacerCriterioFechaEditable(card, criterioId) {
    console.log('Haciendo criterio de fecha editable:', criterioId);

    const divs = card.querySelectorAll('div');
    let desdeElement = null, hastaElement = null, tipoFechaElement = null;

    divs.forEach(div => {
        const text = div.textContent || '';
        if (text.includes('Desde:')) desdeElement = div;
        else if (text.includes('Hasta:')) hastaElement = div;
        else if (text.includes('Tipo de fecha:')) tipoFechaElement = div;
    });

    if (desdeElement) {
        const desdeValor = desdeElement.textContent.replace('Desde:', '').trim();
        desdeElement.innerHTML = `
            <strong>Desde:</strong>
            <input type="date" class="form-input fecha-desde-editable"
                   value="${desdeValor}"
                   style="margin-top: 5px; padding: 5px;">
        `;
    }

    if (hastaElement) {
        const hastaValor = hastaElement.textContent.replace('Hasta:', '').trim();
        hastaElement.innerHTML = `
            <strong>Hasta:</strong>
            <input type="date" class="form-input fecha-hasta-editable"
                   value="${hastaValor}"
                   style="margin-top: 5px; padding: 5px;">
        `;
    }

    if (tipoFechaElement) {
        const tipoFechaValor = tipoFechaElement.textContent.replace('Tipo de fecha:', '').trim();
        const tipoFechaOptions = ['fechaDeCarga', 'fechaDeAcontecimiento'];
        tipoFechaElement.innerHTML = `
            <strong>Tipo de fecha:</strong>
            <select class="form-select tipo-fecha-editable" style="margin-top: 5px; padding: 5px;">
                ${tipoFechaOptions.map(option => `
                    <option value="${option}" ${option === tipoFechaValor ? 'selected' : ''}>
                        ${option === 'fechaDeCarga' ? 'Fecha de carga' : 'Fecha del acontecimiento'}
                    </option>
                `).join('')}
            </select>
        `;
    }
}

function hacerCriterioUbicacionEditable(card, criterioId) {
    console.log('Haciendo criterio de ubicación editable:', criterioId);

    const divs = card.querySelectorAll('div');
    let latitudElement = null, longitudElement = null;

    divs.forEach(div => {
        const text = div.textContent || '';
        if (text.includes('Latitud:')) latitudElement = div;
        else if (text.includes('Longitud:')) longitudElement = div;
    });

    if (latitudElement) {
        const latitudValor = latitudElement.textContent.replace('Latitud:', '').trim();
        latitudElement.innerHTML = `
            <strong>Latitud:</strong>
            <input type="number" step="any" class="form-input latitud-editable"
                   value="${latitudValor}"
                   style="margin-top: 5px; padding: 5px;">
        `;
    }

    if (longitudElement) {
        const longitudValor = longitudElement.textContent.replace('Longitud:', '').trim();
        longitudElement.innerHTML = `
            <strong>Longitud:</strong>
            <input type="number" step="any" class="form-input longitud-editable"
                   value="${longitudValor}"
                   style="margin-top: 5px; padding: 5px;">
        `;
    }
}

function hacerCriterioContribuyenteEditable(card, criterioId) {
    console.log('Haciendo criterio de contribuyente editable:', criterioId);

    const divs = card.querySelectorAll('div');
    let contribuyenteElement = null;

    divs.forEach(div => {
        const text = div.textContent || '';
        if (text.includes('Contribuyente:')) {
            contribuyenteElement = div;
        }
    });

    if (contribuyenteElement) {
        const contribuyenteValor = contribuyenteElement.textContent.replace('Contribuyente:', '').trim();
        contribuyenteElement.innerHTML = `
            <strong>Contribuyente:</strong>
            <input type="text" class="form-input contribuyente-editable"
                   value="${contribuyenteValor.replace(/"/g, '&quot;')}"
                   style="margin-top: 5px; padding: 5px;">
        `;
    }
}

function hacerCriterioMultimediaEditable(card, criterioId) {
    console.log('Haciendo criterio de multimedia editable:', criterioId);

    const divs = card.querySelectorAll('div');
    let tipoElement = null;

    divs.forEach(div => {
        const text = div.textContent || '';
        if (text.includes('Tipo:')) {
            tipoElement = div;
        }
    });

    if (tipoElement) {
        const tipoValor = tipoElement.textContent.replace('Tipo:', '').trim();
        const tipoMultimediaOptions = ['IMAGEN', 'VIDEO', 'AUDIO', 'DOCUMENTO'];
        tipoElement.innerHTML = `
            <strong>Tipo:</strong>
            <select class="form-select tipo-multimedia-editable" style="margin-top: 5px; padding: 5px;">
                ${tipoMultimediaOptions.map(option => `
                    <option value="${option}" ${option === tipoValor ? 'selected' : ''}>
                        ${option}
                    </option>
                `).join('')}
            </select>
        `;
    }
}

function inicializarEventosEditar() {
    console.log('Inicializando eventos de edición...');

    // 1. Evento para cambiar tipo de criterio (NUEVOS criterios)
    const tipoCriterioSelect = document.getElementById('tipoCriterio');
    if (tipoCriterioSelect) {
        console.log('✓ Enlazando evento change a tipoCriterio');
        tipoCriterioSelect.addEventListener('change', mostrarCampoCriterioEditar);
    } else {
        console.warn('⚠ No se encontró elemento con id tipoCriterio');
    }

    // 2. Evento para agregar criterio (NUEVOS criterios)
    const btnAgregarCriterio = document.getElementById('btn-agregar-criterio');
    if (btnAgregarCriterio) {
        console.log('✓ Enlazando evento click a btn-agregar-criterio');
        btnAgregarCriterio.addEventListener('click', agregarCriterioEditar);
    } else {
        console.warn('⚠ No se encontró elemento con id btn-agregar-criterio');
    }

    // 3. Delegación de eventos para botones dinámicos
    document.addEventListener('click', function(e) {
        const target = e.target;

        // A. Eliminar criterios completos (EXISTENTES y NUEVOS)
        if (target.classList.contains('btn-eliminar-criterio')) {
            console.log('✗ Botón eliminar criterio clickeado');
            const criterioCard = target.closest('.criterio-card');
            if (criterioCard) {
                if (confirm('¿Está seguro de eliminar este criterio?')) {
                    criterioCard.remove();
                    console.log('✓ Criterio eliminado');
                }
            }
        }

        // B. Eliminar palabras en criterios EXISTENTES editables
        else if (target.classList.contains('btn-eliminar-palabra')) {
            console.log('✗ Botón eliminar palabra clickeado');
            eliminarPalabraEditable(target);
        }

        // C. Agregar palabras en criterios EXISTENTES editables
        else if (target.classList.contains('btn-agregar-palabra')) {
            console.log('+ Botón agregar palabra clickeado');
            const criterioId = target.getAttribute('data-criterio');
            agregarPalabraEditable(criterioId);
        }

        // D. Eliminar etiquetas en criterios EXISTENTES editables
        else if (target.classList.contains('btn-eliminar-etiqueta')) {
            console.log('✗ Botón eliminar etiqueta clickeado');
            eliminarEtiquetaEditable(target);
        }

        // E. Agregar etiquetas en criterios EXISTENTES editables
        else if (target.classList.contains('btn-agregar-etiqueta')) {
            console.log('+ Botón agregar etiqueta clickeado');
            const criterioId = target.getAttribute('data-criterio');
            agregarEtiquetaEditable(criterioId);
        }

        // F. Agregar palabras en NUEVOS criterios (temporales)
        else if (target.textContent && target.textContent.includes('Agregar otra palabra')) {
            console.log('+ Agregando otra palabra (nuevo criterio)');
            agregarInputTemporalEditar('palabras');
        }

        // G. Agregar etiquetas en NUEVOS criterios (temporales)
        else if (target.textContent && target.textContent.includes('Agregar otra etiqueta')) {
            console.log('+ Agregando otra etiqueta (nuevo criterio)');
            agregarInputTemporalEditar('etiquetas');
        }

        // H. Eliminar inputs temporales en NUEVOS criterios
        else if (target.classList.contains('btn-eliminar')) {
            console.log('✗ Botón eliminar input temporal clickeado');
            eliminarInputTemporalEditar(target);
        }
    });

    // 4. Evento para el formulario principal
    const form = document.getElementById("form-editar-coleccion");
    if (form) {
        console.log('✓ Enlazando evento submit al formulario');
        form.addEventListener("submit", manejarEnvioFormularioEditar);
    }

    // 5. Evento para cancelar
    const btnCancelar = document.getElementById("btn-cancelar");
    if (btnCancelar) {
        btnCancelar.addEventListener("click", function() {
            if (confirm("¿Seguro que deseas cancelar? Los cambios no guardados se perderán.")) {
                window.location.href = `/colecciones/${COLECCION_ID_EDITAR}`;
            }
        });
    }

    console.log('✓ Todos los eventos inicializados');
}

// FUNCIÓN PARA MOSTRAR CAMPOS DE NUEVOS CRITERIOS (igual que creación)
function mostrarCampoCriterioEditar() {
    console.log('Cambiando tipo de criterio (nuevo):', this.value);
    const tipo = this.value;
    const campoDiv = document.getElementById("campoCriterio");
    const btnAgregar = document.getElementById("btn-agregar-criterio");

    if (!campoDiv) {
        console.error('❌ No se encontró elemento campoCriterio');
        return;
    }

    campoDiv.innerHTML = "";

    if (btnAgregar) {
        btnAgregar.disabled = !tipo;
        console.log('✓ Botón agregar:', btnAgregar.disabled ? 'deshabilitado' : 'habilitado');
    }

    if (!tipo) return;

    // EXACTAMENTE IGUAL QUE EN TU CÓDIGO DE CREACIÓN
    switch (tipo) {
        case "CriterioDeTexto":
            campoDiv.innerHTML = `
                <div class="form-group">
                    <label class="form-label">Palabras clave:</label>
                    <div id="lista-palabras-temporal" class="palabras-container">
                        <div class="input-palabra-group">
                            <input type="text" class="form-input palabra-input-temporal" placeholder="Ej: contaminación" required>
                            <button type="button" class="btn btn-danger btn-eliminar" style="display:none;">✖</button>
                        </div>
                    </div>
                    <button type="button" class="btn btn-outline-secondary" style="margin-top: 10px;">+ Agregar otra palabra</button>
                </div>
                <div class="form-group">
                    <label for="tipoTexto" class="form-label" style="margin-top: 15px;">Tipo de texto:</label>
                    <select id="tipoTexto" class="form-select" required>
                        <option value="TITULO">Título</option>
                        <option value="DESCRIPCION">Descripción</option>
                        <option value="CATEGORIA">Categoría</option>
                        <option value="BUSQUEDA">Búsqueda</option>
                    </select>
                </div>
            `;
            console.log('✓ Campos de CriterioDeTexto renderizados');
            break;

        case "CriterioEtiquetas":
            campoDiv.innerHTML = `
                <div class="form-group">
                    <label class="form-label">Etiquetas:</label>
                    <div id="lista-etiquetas-temporal" class="palabras-container">
                        <div class="input-palabra-group">
                            <input type="text" class="form-input palabra-input-temporal" placeholder="Ej: contaminación" required>
                            <button type="button" class="btn btn-danger btn-eliminar" style="display:none;">✖</button>
                        </div>
                    </div>
                    <button type="button" class="btn btn-outline-secondary" style="margin-top: 10px;">+ Agregar otra etiqueta</button>
                </div>
            `;
            console.log('✓ Campos de CriterioEtiquetas renderizados');
            break;

        case "CriterioTipoMultimedia":
            campoDiv.innerHTML = `
                <div class="form-group">
                    <label for="tipoMultimedia" class="form-label">Tipo de archivo:</label>
                    <select id="tipoMultimedia" class="form-select" required>
                        <option value="IMAGEN">Imagen</option>
                        <option value="VIDEO">Video</option>
                        <option value="AUDIO">Audio</option>
                        <option value="DOCUMENTO">Documento</option>
                    </select>
                </div>
            `;
            console.log('✓ Campos de CriterioTipoMultimedia renderizados');
            break;

        case "CriterioFecha":
            campoDiv.innerHTML = `
                <div class="form-group">
                    <label for="fechaInicio" class="form-label">Desde:</label>
                    <input type="date" id="fechaInicio" class="form-input" required>
                </div>
                <div class="form-group">
                    <label for="fechaFin" class="form-label">Hasta:</label>
                    <input type="date" id="fechaFin" class="form-input" required>
                </div>
                <div class="form-group">
                    <label for="tipoDeFecha" class="form-label">Tipo de fecha:</label>
                    <select id="tipoDeFecha" class="form-select" required>
                        <option value="fechaDeAcontecimiento">Fecha del acontecimiento</option>
                        <option value="fechaDeCarga">Fecha de carga</option>
                    </select>
                </div>
            `;
            console.log('✓ Campos de CriterioFecha renderizados');
            break;

        case "CriterioUbicacion":
            campoDiv.innerHTML = `
                <div class="form-group">
                    <label class="form-label">Ubicación</label>
                    <input type="number" step="any" id="latitud" class="form-input" placeholder="Latitud" required style="margin-bottom: 10px;">
                    <input type="number" step="any" id="longitud" class="form-input" placeholder="Longitud" required>
                </div>
            `;
            console.log('✓ Campos de CriterioUbicacion renderizados');
            break;

        case "CriterioContribuyente":
            campoDiv.innerHTML = `
                <div class="form-group">
                    <label for="contribuyente" class="form-label">Nombre del contribuyente:</label>
                    <input type="text" id="contribuyente" class="form-input" required>
                </div>
            `;
            console.log('✓ Campos de CriterioContribuyente renderizados');
            break;
    }
}

// FUNCIONES PARA CAMPOS EDITABLES EN CRITERIOS EXISTENTES
function agregarPalabraEditable(criterioId) {
    const container = document.getElementById(`palabras-edit-${criterioId}`);
    if (!container) {
        console.error('❌ No se encontró contenedor de palabras:', `palabras-edit-${criterioId}`);
        return;
    }

    const inputsExistentes = container.querySelectorAll('.input-palabra-group-editable');

    if (inputsExistentes.length === 1) {
        const primerBoton = inputsExistentes[0].querySelector('.btn-eliminar-palabra');
        if (primerBoton) primerBoton.style.display = 'inline-block';
    }

    const nuevoInput = document.createElement("div");
    nuevoInput.classList.add("input-palabra-group-editable");
    nuevoInput.style.cssText = "display: flex; align-items: center; margin-bottom: 5px;";
    nuevoInput.innerHTML = `
        <input type="text" class="form-input palabra-input-editable"
               value="" placeholder="Nueva palabra"
               style="flex: 1; margin-right: 5px; padding: 5px;">
        <button type="button" class="btn btn-danger btn-eliminar-palabra"
                style="padding: 2px 8px;">✖</button>
    `;
    container.appendChild(nuevoInput);
    console.log('✓ Nueva palabra agregada a criterio:', criterioId);
}

function eliminarPalabraEditable(boton) {
    const grupoInput = boton.parentElement;
    const container = grupoInput.parentElement;
    const gruposRestantes = container.querySelectorAll('.input-palabra-group-editable');

    if (gruposRestantes.length === 2) {
        const primerGrupo = gruposRestantes[0];
        const primerBoton = primerGrupo.querySelector('.btn-eliminar-palabra');
        if (primerBoton) primerBoton.style.display = 'none';
    }

    grupoInput.remove();
    console.log('✓ Palabra eliminada');
}

function agregarEtiquetaEditable(criterioId) {
    const container = document.getElementById(`etiquetas-edit-${criterioId}`);
    if (!container) {
        console.error('❌ No se encontró contenedor de etiquetas:', `etiquetas-edit-${criterioId}`);
        return;
    }

    const inputsExistentes = container.querySelectorAll('.input-etiqueta-group-editable');

    if (inputsExistentes.length === 1) {
        const primerBoton = inputsExistentes[0].querySelector('.btn-eliminar-etiqueta');
        if (primerBoton) primerBoton.style.display = 'inline-block';
    }

    const nuevoInput = document.createElement("div");
    nuevoInput.classList.add("input-etiqueta-group-editable");
    nuevoInput.style.cssText = "display: flex; align-items: center; margin-bottom: 5px;";
    nuevoInput.innerHTML = `
        <input type="text" class="form-input etiqueta-input-editable"
               value="" placeholder="Nueva etiqueta"
               style="flex: 1; margin-right: 5px; padding: 5px;">
        <button type="button" class="btn btn-danger btn-eliminar-etiqueta"
                style="padding: 2px 8px;">✖</button>
    `;
    container.appendChild(nuevoInput);
    console.log('✓ Nueva etiqueta agregada a criterio:', criterioId);
}

function eliminarEtiquetaEditable(boton) {
    const grupoInput = boton.parentElement;
    const container = grupoInput.parentElement;
    const gruposRestantes = container.querySelectorAll('.input-etiqueta-group-editable');

    if (gruposRestantes.length === 2) {
        const primerGrupo = gruposRestantes[0];
        const primerBoton = primerGrupo.querySelector('.btn-eliminar-etiqueta');
        if (primerBoton) primerBoton.style.display = 'none';
    }

    grupoInput.remove();
    console.log('✓ Etiqueta eliminada');
}

// FUNCIONES PARA NUEVOS CRITERIOS (igual que creación)
function agregarInputTemporalEditar(tipoInput) {
    console.log('Agregando input temporal:', tipoInput);
    const contenedorId = tipoInput === 'palabras' ? 'lista-palabras-temporal' : 'lista-etiquetas-temporal';
    const placeholder = tipoInput === 'palabras' ? 'Otra palabra clave' : 'Otra etiqueta';

    const contenedor = document.getElementById(contenedorId);
    if (!contenedor) {
        console.error('❌ No se encontró contenedor:', contenedorId);
        return;
    }

    const inputsExistentes = contenedor.querySelectorAll('.input-palabra-group');

    if (inputsExistentes.length === 1) {
        const primerBoton = inputsExistentes[0].querySelector('.btn-eliminar');
        if (primerBoton) primerBoton.style.display = 'inline-block';
    }

    const nuevoInput = document.createElement("div");
    nuevoInput.classList.add("input-palabra-group");
    nuevoInput.innerHTML = `
        <input type="text" class="form-input palabra-input-temporal" placeholder="${placeholder}" required>
        <button type="button" class="btn btn-danger btn-eliminar">✖</button>
    `;
    contenedor.appendChild(nuevoInput);
    console.log('✓ Input temporal agregado:', tipoInput);
}

function eliminarInputTemporalEditar(boton) {
    console.log('Eliminando input temporal');
    const grupoInput = boton.parentElement;
    const contenedor = grupoInput.parentElement;
    const gruposRestantes = contenedor.querySelectorAll('.input-palabra-group');

    if (gruposRestantes.length === 2) {
        const primerGrupo = gruposRestantes[0];
        const primerBoton = primerGrupo.querySelector('.btn-eliminar');
        if (primerBoton) primerBoton.style.display = 'none';
    }

    grupoInput.remove();
    console.log('✓ Input temporal eliminado');
}

// Función para agregar un nuevo criterio (similar a creación)
function agregarCriterioEditar() {
    console.log('Agregando nuevo criterio');
    const tipo = document.getElementById("tipoCriterio").value;
    if (!tipo) {
        alert('Debe seleccionar un tipo de criterio');
        return;
    }

    const criterioId = "criterio_nuevo_" + contadorCriteriosEditar;
    contadorCriteriosEditar++;
    const criteriosContainer = document.getElementById("criterios-agregados");

    if (!criteriosContainer) {
        console.error('❌ No se encontró contenedor de criterios');
        return;
    }

    let criterioHTML = '';
    let camposHTML = '';

    // EXACTAMENTE IGUAL QUE EN TU CÓDIGO DE CREACIÓN
    switch (tipo) {
        case "CriterioDeTexto":
            const palabrasInputs = document.querySelectorAll('#lista-palabras-temporal .palabra-input-temporal');
            const palabras = Array.from(palabrasInputs).map(input => input.value).filter(val => val.trim() !== '');
            const tipoTexto = document.getElementById('tipoTexto').value;

            if (palabras.length === 0) {
                alert('Debe ingresar al menos una palabra clave');
                return;
            }

            camposHTML = palabras.map(palabra =>
                `<input type="hidden" name="criterios[${criterioId}].palabras" value="${palabra}">`
            ).join('');
            camposHTML += `<input type="hidden" name="criterios[${criterioId}].tipoDeTexto" value="${tipoTexto}">`;

            criterioHTML = `
                <div class="criterio-card" id="${criterioId}">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Texto</span>
                        <button type="button" class="btn-eliminar-criterio">Eliminar</button>
                    </div>
                    <div><strong>Palabras:</strong> ${palabras.join(', ')}</div>
                    <div><strong>Tipo de texto:</strong> ${tipoTexto}</div>
                    ${camposHTML}
                    <input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">
                </div>
            `;
            break;

        case "CriterioEtiquetas":
            const etiquetasInputs = document.querySelectorAll('#lista-etiquetas-temporal .palabra-input-temporal');
            const etiquetas = Array.from(etiquetasInputs).map(input => input.value).filter(val => val.trim() !== '');

            if (etiquetas.length === 0) {
                alert('Debe ingresar al menos una etiqueta');
                return;
            }

            camposHTML = etiquetas.map(etiqueta =>
                `<input type="hidden" name="criterios[${criterioId}].etiquetas" value="${etiqueta}">`
            ).join('');

            criterioHTML = `
                <div class="criterio-card" id="${criterioId}">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Etiquetas</span>
                        <button type="button" class="btn-eliminar-criterio">Eliminar</button>
                    </div>
                    <div><strong>Etiquetas:</strong> ${etiquetas.join(', ')}</div>
                    ${camposHTML}
                    <input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">
                </div>
            `;
            break;

        case "CriterioTipoMultimedia":
            const tipoMultimedia = document.getElementById('tipoMultimedia').value;
            camposHTML = `<input type="hidden" name="criterios[${criterioId}].tipoContenidoMultimedia" value="${tipoMultimedia}">`;

            criterioHTML = `
                <div class="criterio-card" id="${criterioId}">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Tipo Multimedia</span>
                        <button type="button" class="btn-eliminar-criterio">Eliminar</button>
                    </div>
                    <div><strong>Tipo:</strong> ${tipoMultimedia}</div>
                    ${camposHTML}
                    <input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">
                </div>
            `;
            break;

        case "CriterioFecha":
            const fechaInicio = document.getElementById('fechaInicio').value;
            const fechaFin = document.getElementById('fechaFin').value;
            const tipoDeFecha = document.getElementById('tipoDeFecha').value;

            if (!fechaInicio || !fechaFin) {
                alert('Debe ingresar fecha de inicio y fin');
                return;
            }

            camposHTML = `<input type="hidden" name="criterios[${criterioId}].fechaInicio" value="${fechaInicio}">`;
            camposHTML += `<input type="hidden" name="criterios[${criterioId}].fechaFin" value="${fechaFin}">`;
            camposHTML += `<input type="hidden" name="criterios[${criterioId}].tipoFecha" value="${tipoDeFecha}">`;

            criterioHTML = `
                <div class="criterio-card" id="${criterioId}">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Fecha</span>
                        <button type="button" class="btn-eliminar-criterio">Eliminar</button>
                    </div>
                    <div><strong>Desde:</strong> ${fechaInicio}</div>
                    <div><strong>Hasta:</strong> ${fechaFin}</div>
                    <div><strong>Tipo de fecha:</strong> ${tipoDeFecha}</div>
                    ${camposHTML}
                    <input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">
                </div>
            `;
            break;

        case "CriterioUbicacion":
            const latitud = document.getElementById('latitud').value;
            const longitud = document.getElementById('longitud').value;

            if (!latitud || !longitud) {
                alert('Debe ingresar latitud y longitud');
                return;
            }

            camposHTML = `<input type="hidden" name="criterios[${criterioId}].ubicacion.latitud" value="${latitud}">`;
            camposHTML += `<input type="hidden" name="criterios[${criterioId}].ubicacion.longitud" value="${longitud}">`;

            criterioHTML = `
                <div class="criterio-card" id="${criterioId}">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Ubicación</span>
                        <button type="button" class="btn-eliminar-criterio">Eliminar</button>
                    </div>
                    <div><strong>Latitud:</strong> ${latitud}</div>
                    <div><strong>Longitud:</strong> ${longitud}</div>
                    ${camposHTML}
                    <input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">
                </div>
            `;
            break;

        case "CriterioContribuyente":
            const contribuyente = document.getElementById('contribuyente').value;

            if (!contribuyente || contribuyente.trim() === '') {
                alert('Debe ingresar un nombre de contribuyente');
                return;
            }

            camposHTML = `<input type="hidden" name="criterios[${criterioId}].nombreContribuyente" value="${contribuyente}">`;

            criterioHTML = `
                <div class="criterio-card" id="${criterioId}">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Contribuyente</span>
                        <button type="button" class="btn-eliminar-criterio">Eliminar</button>
                    </div>
                    <div><strong>Contribuyente:</strong> ${contribuyente}</div>
                    ${camposHTML}
                    <input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">
                </div>
            `;
            break;
    }

    criteriosContainer.insertAdjacentHTML('beforeend', criterioHTML);
    console.log('✓ Criterio agregado con ID:', criterioId);

    // Limpiar el formulario temporal
    document.getElementById("tipoCriterio").value = "";
    document.getElementById("campoCriterio").innerHTML = "";
    document.getElementById("btn-agregar-criterio").disabled = true;
}

// Función para manejar el envío del formulario
async function manejarEnvioFormularioEditar(e) {
    e.preventDefault();
    console.log('Enviando formulario de edición');

    // Recolectar datos del formulario
    const data = {
        titulo: document.getElementById("titulo").value,
        descripcion: document.getElementById("descripcion").value,
        algoritmoDeConsenso: document.getElementById("algoritmo").value,
        fuentes: Array.from(document.getElementById("fuentes").selectedOptions).map(o => o.value),
        criteriosDePertenencia: []
    };

    // Recolectar criterios (existentes editados y nuevos)
    const criteriosCards = document.querySelectorAll('.criterio-card');
    console.log('Total criterios a enviar:', criteriosCards.length);

    criteriosCards.forEach((card, index) => {
        const criterio = {};
        const criterioId = card.id || `criterio_${index}`;

        // Determinar tipo de criterio
        const tipoElement = card.querySelector('.criterio-tipo');
        if (!tipoElement) return;

        const tipoTexto = tipoElement.textContent.trim();

        if (tipoTexto.includes('Texto')) {
            // Criterio de texto editable
            const palabrasInputs = card.querySelectorAll('.palabra-input-editable');
            const palabras = Array.from(palabrasInputs).map(input => input.value.trim()).filter(val => val);
            const tipoSelect = card.querySelector('.tipo-texto-editable');

            if (palabras.length > 0) {
                criterio['@type'] = 'CriterioDeTexto';
                criterio.palabras = palabras;
                if (tipoSelect) criterio.tipoDeTexto = tipoSelect.value;
            }

        } else if (tipoTexto.includes('Etiquetas')) {
            // Criterio de etiquetas editable
            const etiquetasInputs = card.querySelectorAll('.etiqueta-input-editable');
            const etiquetas = Array.from(etiquetasInputs).map(input => input.value.trim()).filter(val => val);

            if (etiquetas.length > 0) {
                criterio['@type'] = 'CriterioEtiquetas';
                criterio.etiquetas = etiquetas.map(nombre => ({ nombre }));
            }

        } else if (tipoTexto.includes('Fecha')) {
            // Criterio de fecha editable
            const fechaInicio = card.querySelector('.fecha-desde-editable')?.value;
            const fechaFin = card.querySelector('.fecha-hasta-editable')?.value;
            const tipoFecha = card.querySelector('.tipo-fecha-editable')?.value;

            if (fechaInicio && fechaFin) {
                criterio['@type'] = 'CriterioFecha';
                criterio.fechaInicio = new Date(fechaInicio).getTime();
                criterio.fechaFin = new Date(fechaFin).getTime();
                if (tipoFecha) criterio.tipoFecha = tipoFecha;
            }

        } else if (tipoTexto.includes('Ubicación')) {
            // Criterio de ubicación editable
            const latitud = card.querySelector('.latitud-editable')?.value;
            const longitud = card.querySelector('.longitud-editable')?.value;

            if (latitud && longitud) {
                criterio['@type'] = 'CriterioUbicacion';
                criterio.ubicacion = {
                    latitud: parseFloat(latitud),
                    longitud: parseFloat(longitud)
                };
            }

        } else if (tipoTexto.includes('Contribuyente')) {
            // Criterio de contribuyente editable
            const contribuyente = card.querySelector('.contribuyente-editable')?.value;

            if (contribuyente && contribuyente.trim()) {
                criterio['@type'] = 'CriterioContribuyente';
                criterio.nombreContribuyente = contribuyente.trim();
            }

        } else if (tipoTexto.includes('Multimedia')) {
            // Criterio de multimedia editable
            const tipoMultimedia = card.querySelector('.tipo-multimedia-editable')?.value;

            if (tipoMultimedia) {
                criterio['@type'] = 'CriterioTipoMultimedia';
                criterio.tipoContenidoMultimedia = tipoMultimedia;
            }

        } else {
            // Criterios nuevos (con inputs hidden)
            const hiddenInputs = card.querySelectorAll('input[type="hidden"]');
            hiddenInputs.forEach(input => {
                const name = input.name;
                const value = input.value;

                const match = name.match(/criterios\[[^\]]+\]\.(.+)/);
                if (match) {
                    const fieldName = match[1];

                    if (fieldName === 'palabras' || fieldName === 'etiquetas') {
                        if (!criterio[fieldName]) criterio[fieldName] = [];
                        criterio[fieldName].push(value);
                    } else if (fieldName === 'ubicacion.latitud' || fieldName === 'ubicacion.longitud') {
                        if (!criterio.ubicacion) criterio.ubicacion = {};
                        const coordType = fieldName.split('.')[1];
                        criterio.ubicacion[coordType] = parseFloat(value);
                    } else if (fieldName === '@type') {
                        criterio['@type'] = value;
                    } else {
                        criterio[fieldName] = value;
                    }
                }
            });
        }

        if (Object.keys(criterio).length > 0) {
            data.criteriosDePertenencia.push(criterio);
            console.log('✓ Criterio recolectado:', criterio['@type']);
        }
    });

    const username = window.USERNAME || '';
    const accessToken = window.ACCESS_TOKEN || '';

    console.log('Usuario:', username);
    console.log('Token disponible:', accessToken ? 'Sí' : 'No');

    console.log('Datos a enviar:', JSON.stringify(data, null, 2));

    try {
        console.log("Coleccion a editar: " + COLECCION_ID_EDITAR);
        const res = await fetch(`/colecciones/${COLECCION_ID_EDITAR}`, {
            method: "PUT",
            headers: {"Content-Type": "application/json", "username": username, "access_token": accessToken},
            body: JSON.stringify(data)
        });

        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Error ${res.status}: ${errorText}`);
        }

        document.getElementById("mensaje-exito").style.display = "block";
        setTimeout(() => window.location.href = `/colecciones/${COLECCION_ID_EDITAR}`, 1500);
    } catch (err) {
        console.error('Error al guardar:', err);
        document.getElementById("mensaje-error").style.display = "block";
    }
}

// Exportar funciones al scope global
window.mostrarCampoCriterioEditar = mostrarCampoCriterioEditar;
window.agregarCriterioEditar = agregarCriterioEditar;