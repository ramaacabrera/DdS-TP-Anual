// Variables globales
var contadorCriteriosEditar = 0;
var nuevosCriterios = [];

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM cargado - Inicializando eventos de edición');

    if (window.URL_ADMIN_EDITAR) {
        URL_ADMIN_EDITAR = window.URL_ADMIN_EDITAR;
    }
    if (window.COLECCION_ID_EDITAR) {
        COLECCION_ID_EDITAR = window.COLECCION_ID_EDITAR;
    }

    hacerCriteriosEditables();
    const criteriosExistentes = document.querySelectorAll('.criterio-card');
    contadorCriteriosEditar = criteriosExistentes.length;
    inicializarEventosEditar();
});

function hacerCriteriosEditables() {
    const criteriosCards = document.querySelectorAll('.criterio-card');
    criteriosCards.forEach((card, index) => {
        const criterioId = card.id || `criterio_existente_${index}`;
        if (!card.id) card.id = criterioId;

        const tipoElement = card.querySelector('.criterio-tipo');
        if (!tipoElement) return;

        const tipoTexto = tipoElement.textContent.trim();

        if (tipoTexto.includes('Texto')) hacerCriterioTextoEditable(card, criterioId);
        else if (tipoTexto.includes('Etiquetas')) hacerCriterioEtiquetasEditable(card, criterioId);
        else if (tipoTexto.includes('Fecha')) hacerCriterioFechaEditable(card, criterioId);
        else if (tipoTexto.includes('Ubicación')) hacerCriterioUbicacionEditable(card, criterioId);
        else if (tipoTexto.includes('Contribuyente')) hacerCriterioContribuyenteEditable(card, criterioId);
        else if (tipoTexto.includes('Multimedia')) hacerCriterioMultimediaEditable(card, criterioId);
    });
}

function hacerCriterioTextoEditable(card, criterioId) {
    const divs = card.querySelectorAll('div');
    let palabrasElement = null, tipoTextoElement = null;
    divs.forEach(div => {
        const text = div.textContent || '';
        if (text.includes('Palabras:')) palabrasElement = div;
        else if (text.includes('Tipo de texto:')) tipoTextoElement = div;
    });
    if (!palabrasElement || !tipoTextoElement) return;

    const palabrasTexto = palabrasElement.textContent.replace('Palabras:', '').trim();
    const tipoTextoValor = tipoTextoElement.textContent.replace('Tipo de texto:', '').trim();
    const palabrasArray = palabrasTexto ? palabrasTexto.split(', ').filter(p => p) : [];

    palabrasElement.innerHTML = `
        <strong>Palabras clave:</strong>
        <div id="palabras-edit-${criterioId}" class="palabras-container-editable" style="margin-top: 5px;">
            ${palabrasArray.map(palabra => `
                <div class="input-palabra-group-editable" style="display: flex; align-items: center; margin-bottom: 5px;">
                    <input type="text" class="form-input palabra-input-editable" value="${palabra.replace(/"/g, '&quot;')}" style="flex: 1; margin-right: 5px; padding: 5px;">
                    <button type="button" class="btn btn-danger btn-eliminar-palabra" style="padding: 2px 8px;">✖</button>
                </div>
            `).join('')}
        </div>
        <button type="button" class="btn btn-outline-secondary btn-agregar-palabra" data-criterio="${criterioId}" style="margin-top: 5px; padding: 3px 10px; font-size: 0.9em;">+ Agregar otra palabra</button>
    `;
    const tipoTextoOptions = ['TITULO', 'DESCRIPCION', 'CATEGORIA', 'BUSQUEDA'];
    tipoTextoElement.innerHTML = `
        <strong>Tipo de texto:</strong>
        <select class="form-select tipo-texto-editable" style="margin-top: 5px; padding: 5px;">
            ${tipoTextoOptions.map(opt => `<option value="${opt}" ${opt === tipoTextoValor ? 'selected' : ''}>${opt}</option>`).join('')}
        </select>
    `;
}

function hacerCriterioEtiquetasEditable(card, criterioId) {
    const divs = card.querySelectorAll('div');
    let etiquetasElement = null;
    divs.forEach(div => { if ((div.textContent || '').includes('Etiquetas:')) etiquetasElement = div; });
    if (!etiquetasElement) return;

    const etiquetasTexto = etiquetasElement.textContent.replace('Etiquetas:', '').trim();
    const etiquetasArray = etiquetasTexto ? etiquetasTexto.split(', ').filter(e => e) : [];

    etiquetasElement.innerHTML = `
        <strong>Etiquetas:</strong>
        <div id="etiquetas-edit-${criterioId}" class="etiquetas-container-editable" style="margin-top: 5px;">
            ${etiquetasArray.map(e => `
                <div class="input-etiqueta-group-editable" style="display: flex; align-items: center; margin-bottom: 5px;">
                    <input type="text" class="form-input etiqueta-input-editable" value="${e.replace(/"/g, '&quot;')}" style="flex: 1; margin-right: 5px; padding: 5px;">
                    <button type="button" class="btn btn-danger btn-eliminar-etiqueta" style="padding: 2px 8px;">✖</button>
                </div>
            `).join('')}
        </div>
        <button type="button" class="btn btn-outline-secondary btn-agregar-etiqueta" data-criterio="${criterioId}" style="margin-top: 5px; padding: 3px 10px; font-size: 0.9em;">+ Agregar otra etiqueta</button>
    `;
}

function hacerCriterioFechaEditable(card, criterioId) {
    const divs = card.querySelectorAll('div');
    let desdeElement, hastaElement, tipoFechaElement;
    divs.forEach(div => {
        const t = div.textContent || '';
        if (t.includes('Desde:')) desdeElement = div;
        else if (t.includes('Hasta:')) hastaElement = div;
        else if (t.includes('Tipo de fecha:')) tipoFechaElement = div;
    });

    if (desdeElement) {
        const val = desdeElement.textContent.replace('Desde:', '').trim();
        desdeElement.innerHTML = `<strong>Desde:</strong><input type="date" class="form-input fecha-desde-editable" value="${val}" style="margin-top: 5px; padding: 5px;">`;
    }
    if (hastaElement) {
        const val = hastaElement.textContent.replace('Hasta:', '').trim();
        hastaElement.innerHTML = `<strong>Hasta:</strong><input type="date" class="form-input fecha-hasta-editable" value="${val}" style="margin-top: 5px; padding: 5px;">`;
    }
    if (tipoFechaElement) {
        const val = tipoFechaElement.textContent.replace('Tipo de fecha:', '').trim();
        const opts = ['fechaDeCarga', 'fechaDeAcontecimiento'];
        tipoFechaElement.innerHTML = `<strong>Tipo de fecha:</strong><select class="form-select tipo-fecha-editable" style="margin-top: 5px; padding: 5px;">
            ${opts.map(o => `<option value="${o}" ${o === val ? 'selected' : ''}>${o === 'fechaDeCarga' ? 'Fecha de carga' : 'Fecha del acontecimiento'}</option>`).join('')}
        </select>`;
    }
}

function hacerCriterioUbicacionEditable(card, criterioId) {
    const divs = card.querySelectorAll('div');
    let descElement = null;
    divs.forEach(div => { if ((div.textContent || '').includes('Descripcion:')) descElement = div; });

    if (descElement) {
        const val = descElement.textContent.replace('Descripcion:', '').trim();
        descElement.innerHTML = `<strong>Descripcion:</strong><input type="text" class="form-input descripcion-ubicacion-editable" value="${val}" style="margin-top: 5px; padding: 5px;">`;
    }
}

function hacerCriterioContribuyenteEditable(card, criterioId) {
    const divs = card.querySelectorAll('div');
    let el = null;
    divs.forEach(div => { if ((div.textContent || '').includes('Contribuyente:')) el = div; });
    if (el) {
        const val = el.textContent.replace('Contribuyente:', '').trim();
        el.innerHTML = `<strong>Contribuyente:</strong><input type="text" class="form-input contribuyente-editable" value="${val.replace(/"/g, '&quot;')}" style="margin-top: 5px; padding: 5px;">`;
    }
}

function hacerCriterioMultimediaEditable(card, criterioId) {
    const divs = card.querySelectorAll('div');
    let el = null;
    divs.forEach(div => { if ((div.textContent || '').includes('Tipo:')) el = div; });
    if (el) {
        const val = el.textContent.replace('Tipo:', '').trim();
        const opts = ['IMAGEN', 'VIDEO', 'AUDIO', 'DOCUMENTO'];
        el.innerHTML = `<strong>Tipo:</strong><select class="form-select tipo-multimedia-editable" style="margin-top: 5px; padding: 5px;">
            ${opts.map(o => `<option value="${o}" ${o === val ? 'selected' : ''}>${o}</option>`).join('')}
        </select>`;
    }
}

// ... [Inicializar eventos y funciones de agregar/eliminar temporal se mantienen igual] ...
function inicializarEventosEditar() {
    const tipoCriterioSelect = document.getElementById('tipoCriterio');
    if (tipoCriterioSelect) tipoCriterioSelect.addEventListener('change', mostrarCampoCriterioEditar);

    const btnAgregarCriterio = document.getElementById('btn-agregar-criterio');
    if (btnAgregarCriterio) btnAgregarCriterio.addEventListener('click', agregarCriterioEditar);

    document.addEventListener('click', function(e) {
        const target = e.target;
        if (target.classList.contains('btn-eliminar-criterio')) {
            const card = target.closest('.criterio-card');
            if (card && confirm('¿Está seguro de eliminar este criterio?')) {
                const cid = card.id;
                if (cid && cid.startsWith('criterio_nuevo_')) {
                    nuevosCriterios[parseInt(cid.replace('criterio_nuevo_', ''))] = null;
                }
                card.remove();
            }
        } else if (target.classList.contains('btn-eliminar-palabra')) eliminarPalabraEditable(target);
        else if (target.classList.contains('btn-agregar-palabra')) agregarPalabraEditable(target.getAttribute('data-criterio'));
        else if (target.classList.contains('btn-eliminar-etiqueta')) eliminarEtiquetaEditable(target);
        else if (target.classList.contains('btn-agregar-etiqueta')) agregarEtiquetaEditable(target.getAttribute('data-criterio'));
        else if (target.textContent && target.textContent.includes('Agregar otra palabra')) agregarInputTemporalEditar('palabras');
        else if (target.textContent && target.textContent.includes('Agregar otra etiqueta')) agregarInputTemporalEditar('etiquetas');
        else if (target.classList.contains('btn-eliminar')) eliminarInputTemporalEditar(target);
    });

    const form = document.getElementById("form-editar-coleccion");
    if (form) form.addEventListener("submit", manejarEnvioFormularioEditar);

    const btnCancelar = document.getElementById("btn-cancelar");
    if (btnCancelar) {
        btnCancelar.addEventListener("click", function() {
            if (confirm("¿Seguro que deseas cancelar?")) window.location.href = `/colecciones/${COLECCION_ID_EDITAR}`;
        });
    }
}

function mostrarCampoCriterioEditar() {
    const tipo = this.value;
    const campoDiv = document.getElementById("campoCriterio");
    const btn = document.getElementById("btn-agregar-criterio");
    if(!campoDiv) return;
    campoDiv.innerHTML = "";
    if(btn) btn.disabled = !tipo;
    if(!tipo) return;

    if(tipo === "CriterioDeTexto") {
        campoDiv.innerHTML = `<div class="form-group"><label class="form-label">Palabras clave:</label><div id="lista-palabras-temporal" class="palabras-container"><div class="input-palabra-group"><input type="text" class="form-input palabra-input-temporal" placeholder="Ej: contaminación" required><button type="button" class="btn btn-danger btn-eliminar" style="display:none;">✖</button></div></div><button type="button" class="btn btn-outline-secondary" style="margin-top:10px;">+ Agregar otra palabra</button></div><div class="form-group"><label style="margin-top:15px;">Tipo de texto:</label><select id="tipoTexto" class="form-select"><option value="TITULO">Título</option><option value="DESCRIPCION">Descripción</option><option value="CATEGORIA">Categoría</option><option value="BUSQUEDA">Búsqueda</option></select></div>`;
    } else if(tipo === "CriterioEtiquetas") {
        campoDiv.innerHTML = `<div class="form-group"><label class="form-label">Etiquetas:</label><div id="lista-etiquetas-temporal" class="palabras-container"><div class="input-palabra-group"><input type="text" class="form-input palabra-input-temporal" placeholder="Ej: contaminación" required><button type="button" class="btn btn-danger btn-eliminar" style="display:none;">✖</button></div></div><button type="button" class="btn btn-outline-secondary" style="margin-top:10px;">+ Agregar otra etiqueta</button></div>`;
    } else if(tipo === "CriterioTipoMultimedia") {
        campoDiv.innerHTML = `<div class="form-group"><label>Tipo de archivo:</label><select id="tipoMultimedia" class="form-select"><option value="IMAGEN">Imagen</option><option value="VIDEO">Video</option><option value="AUDIO">Audio</option></select></div>`;
    } else if(tipo === "CriterioFecha") {
        campoDiv.innerHTML = `<div class="form-group"><label>Desde:</label><input type="date" id="fechaInicio" class="form-input" required></div><div class="form-group"><label>Hasta:</label><input type="date" id="fechaFin" class="form-input" required></div><div class="form-group"><label>Tipo:</label><select id="tipoDeFecha" class="form-select"><option value="fechaDeAcontecimiento">Fecha del acontecimiento</option><option value="fechaDeCarga">Fecha de carga</option></select></div>`;
    } else if(tipo === "CriterioUbicacion") {
        campoDiv.innerHTML = `<div class="form-group"><label>Descripción:</label><textarea id="descripcionUbicacion" class="form-textarea" rows="3" required></textarea></div>`;
    } else if(tipo === "CriterioContribuyente") {
        campoDiv.innerHTML = `<div class="form-group"><label>Nombre:</label><input type="text" id="contribuyente" class="form-input" required></div>`;
    }
}

function agregarPalabraEditable(criterioId) {
    const c = document.getElementById(`palabras-edit-${criterioId}`);
    if(!c) return;
    const div = document.createElement("div");
    div.className = "input-palabra-group-editable";
    div.style.cssText = "display: flex; align-items: center; margin-bottom: 5px;";
    div.innerHTML = `<input type="text" class="form-input palabra-input-editable" placeholder="Nueva palabra" style="flex:1; margin-right:5px; padding:5px;"><button type="button" class="btn btn-danger btn-eliminar-palabra" style="padding:2px 8px;">✖</button>`;
    c.appendChild(div);
}
function eliminarPalabraEditable(btn) { btn.parentElement.remove(); }
function agregarEtiquetaEditable(criterioId) {
    const c = document.getElementById(`etiquetas-edit-${criterioId}`);
    if(!c) return;
    const div = document.createElement("div");
    div.className = "input-etiqueta-group-editable";
    div.style.cssText = "display: flex; align-items: center; margin-bottom: 5px;";
    div.innerHTML = `<input type="text" class="form-input etiqueta-input-editable" placeholder="Nueva etiqueta" style="flex:1; margin-right:5px; padding:5px;"><button type="button" class="btn btn-danger btn-eliminar-etiqueta" style="padding:2px 8px;">✖</button>`;
    c.appendChild(div);
}
function eliminarEtiquetaEditable(btn) { btn.parentElement.remove(); }
function agregarInputTemporalEditar(type) {
    const id = type === 'palabras' ? 'lista-palabras-temporal' : 'lista-etiquetas-temporal';
    const c = document.getElementById(id);
    const div = document.createElement("div");
    div.className = "input-palabra-group";
    div.innerHTML = `<input type="text" class="form-input palabra-input-temporal" required><button type="button" class="btn btn-danger btn-eliminar">✖</button>`;
    c.appendChild(div);
    const btns = c.querySelectorAll('.btn-eliminar');
    if(btns.length > 0) btns.forEach(b => b.style.display = 'inline-block');
}
function eliminarInputTemporalEditar(btn) {
    const p = btn.parentElement.parentElement;
    btn.parentElement.remove();
    const rest = p.querySelectorAll('.input-palabra-group');
    if(rest.length === 1) rest[0].querySelector('.btn-eliminar').style.display='none';
}

function agregarCriterioEditar() {
    const tipo = document.getElementById("tipoCriterio").value;
    if(!tipo) return;
    const cid = "criterio_nuevo_" + contadorCriteriosEditar++;
    const container = document.getElementById("criterios-agregados");
    let html = '', obj = {'@type': tipo};

    if(tipo === "CriterioDeTexto") {
        const words = Array.from(document.querySelectorAll('#lista-palabras-temporal .palabra-input-temporal')).map(i=>i.value).filter(v=>v.trim());
        if(!words.length) return alert('Ingrese palabras');
        obj.palabras = words;
        obj.tipoDeTexto = document.getElementById('tipoTexto').value;
        html = `<div><strong>Palabras:</strong> ${words.join(', ')}</div><div><strong>Tipo:</strong> ${obj.tipoDeTexto}</div>`;
    } else if (tipo === "CriterioEtiquetas") {
        const tags = Array.from(document.querySelectorAll('#lista-etiquetas-temporal .palabra-input-temporal')).map(i=>i.value).filter(v=>v.trim());
        if(!tags.length) return alert('Ingrese etiquetas');
        obj.etiquetas = tags.map(n => ({nombre:n}));
        html = `<div><strong>Etiquetas:</strong> ${tags.join(', ')}</div>`;
    } else if (tipo === "CriterioTipoMultimedia") {
        obj.tipoContenidoMultimedia = document.getElementById('tipoMultimedia').value;
        html = `<div><strong>Tipo:</strong> ${obj.tipoContenidoMultimedia}</div>`;
    } else if (tipo === "CriterioFecha") {
        const i = document.getElementById('fechaInicio').value, f = document.getElementById('fechaFin').value;
        if(!i || !f) return alert('Ingrese fechas');
        obj.fechaInicio = new Date(i).getTime(); obj.fechaFin = new Date(f).getTime(); obj.tipoFecha = document.getElementById('tipoDeFecha').value;
        html = `<div><strong>Desde:</strong> ${i}</div><div><strong>Hasta:</strong> ${f}</div>`;
    } else if (tipo === "CriterioUbicacion") {
        const d = document.getElementById('descripcionUbicacion').value;
        if(!d) return alert('Ingrese descripción');
        obj.ubicacion = {descripcion: d};
        html = `<div><strong>Descripción:</strong> ${d}</div>`;
    } else if (tipo === "CriterioContribuyente") {
        const c = document.getElementById('contribuyente').value;
        if(!c) return alert('Ingrese nombre');
        obj.nombreContribuyente = c;
        html = `<div><strong>Contribuyente:</strong> ${c}</div>`;
    }

    nuevosCriterios.push(obj);
    container.insertAdjacentHTML('beforeend', `<div class="criterio-card" id="${cid}"><div class="criterio-header"><span class="criterio-tipo">${tipo}</span><button type="button" class="btn-eliminar-criterio">Eliminar</button></div>${html}</div>`);

    document.getElementById("tipoCriterio").value = "";
    document.getElementById("campoCriterio").innerHTML = "";
    document.getElementById("btn-agregar-criterio").disabled = true;
}

async function manejarEnvioFormularioEditar(e) {
    e.preventDefault();
    console.log('Enviando formulario de edición');

    const fuentesSelect = document.getElementById('fuentes');
    let fuentesSelected = [];
    if (fuentesSelect) {
        fuentesSelected = Array.from(fuentesSelect.selectedOptions).map(option => option.value);
    }

    const data = {
        titulo: document.getElementById("titulo").value,
        descripcion: document.getElementById("descripcion").value,
        algoritmoDeConsenso: document.getElementById("algoritmo").value,
        fuentes: fuentesSelected,
        criteriosDePertenencia: []
    };

    const criteriosCards = document.querySelectorAll('.criterio-card');
    criteriosCards.forEach((card) => {
        const criterio = {};
        const hiddenInputs = card.querySelectorAll('input[type="hidden"]');
        const tieneEditables = card.querySelector('.palabra-input-editable, .etiqueta-input-editable, .fecha-desde-editable, .descripcion-ubicacion-editable, .tipo-texto-editable, .tipo-fecha-editable, .tipo-multimedia-editable');

        if (hiddenInputs.length > 0 && !tieneEditables) {
            let tipoCriterio = '';
            const tipoEl = card.querySelector('input[name*="@type"]');
            if (tipoEl) { tipoCriterio = tipoEl.value; criterio['@type'] = tipoCriterio; }

            hiddenInputs.forEach(hidden => {
                const name = hidden.name, value = hidden.value;
                if (name.includes('.palabras')) { if(!criterio.palabras) criterio.palabras=[]; criterio.palabras.push(value); }
                else if (name.includes('.tipoDeTexto')) criterio.tipoDeTexto = value;
                else if (name.includes('.etiquetas')) { if(!criterio.etiquetas) criterio.etiquetas=[]; criterio.etiquetas.push({nombre: value}); }
                else if (name.includes('.tipoContenidoMultimedia')) criterio.tipoContenidoMultimedia = value;
                else if (name.includes('.fechaInicio')) criterio.fechaInicio = new Date(value).getTime();
                else if (name.includes('.fechaFin')) criterio.fechaFin = new Date(value).getTime();
                else if (name.includes('.tipoFecha')) criterio.tipoFecha = value;
                else if (name.includes('.ubicacion.descripcion')) { if(!criterio.ubicacion) criterio.ubicacion={}; criterio.ubicacion.descripcion = value; }
                else if (name.includes('.nombreContribuyente')) criterio.nombreContribuyente = value;
                else if (name.includes('.fuente')) criterio.fuente = value;
            });

        } else if (tieneEditables) {
            // Criterios existentes editados
            const tipoTexto = card.querySelector('.criterio-tipo').textContent.trim();
            if (tipoTexto.includes('Texto')) {
                criterio['@type'] = 'CriterioDeTexto';
                criterio.palabras = Array.from(card.querySelectorAll('.palabra-input-editable')).map(i=>i.value.trim()).filter(v=>v);
                criterio.tipoDeTexto = card.querySelector('.tipo-texto-editable').value;
            } else if (tipoTexto.includes('Etiquetas')) {
                criterio['@type'] = 'CriterioEtiquetas';
                criterio.etiquetas = Array.from(card.querySelectorAll('.etiqueta-input-editable')).map(i=>i.value.trim()).filter(v=>v).map(n=>({nombre:n}));
            } else if (tipoTexto.includes('Fecha')) {
                criterio['@type'] = 'CriterioFecha';
                criterio.fechaInicio = new Date(card.querySelector('.fecha-desde-editable').value).getTime();
                criterio.fechaFin = new Date(card.querySelector('.fecha-hasta-editable').value).getTime();
                criterio.tipoFecha = card.querySelector('.tipo-fecha-editable').value;
            } else if (tipoTexto.includes('Ubicación')) {
                criterio['@type'] = 'CriterioUbicacion';
                criterio.ubicacion = {descripcion: card.querySelector('.descripcion-ubicacion-editable').value.trim()};
            } else if (tipoTexto.includes('Contribuyente')) {
                criterio['@type'] = 'CriterioContribuyente';
                criterio.nombreContribuyente = card.querySelector('.contribuyente-editable').value.trim();
            } else if (tipoTexto.includes('Multimedia')) {
                criterio['@type'] = 'CriterioTipoMultimedia';
                criterio.tipoContenidoMultimedia = card.querySelector('.tipo-multimedia-editable').value;
            }
        }

        if (Object.keys(criterio).length > 0 && criterio['@type']) data.criteriosDePertenencia.push(criterio);
    });

    const nuevosFiltrados = nuevosCriterios.filter(c => c !== null && c['@type']);
    data.criteriosDePertenencia = data.criteriosDePertenencia.concat(nuevosFiltrados);

    const username = window.USERNAME || '', accessToken = window.ACCESS_TOKEN || '';
    try {
        const res = await fetch(`/colecciones/${COLECCION_ID_EDITAR}`, {
            method: "PUT",
            headers: {"Content-Type": "application/json", "username": username, "access_token": accessToken},
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error(await res.text());
        document.getElementById("mensaje-exito").style.display = "block";
        setTimeout(() => window.location.href = `/colecciones/${COLECCION_ID_EDITAR}`, 1500);
    } catch (err) {
        console.error(err);
        document.getElementById("mensaje-error").style.display = "block";
    }
}

window.mostrarCampoCriterioEditar = mostrarCampoCriterioEditar;
window.agregarCriterioEditar = agregarCriterioEditar;