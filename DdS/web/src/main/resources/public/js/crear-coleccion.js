let contadorCriterios = 0;

function mostrarCampoCriterio() {
    const tipo = document.getElementById("tipoCriterio").value;
    const campoDiv = document.getElementById("campoCriterio");
    const btnAgregar = document.getElementById("btn-agregar-criterio");

    campoDiv.innerHTML = "";
    btnAgregar.disabled = !tipo;

    if (!tipo) return;

    switch (tipo) {
        case "CriterioDeTexto":
            campoDiv.innerHTML = `
                <label class="form-label">Palabras clave:</label>
                <div id="lista-palabras-temporal" class="palabras-container">
                    <div class="input-palabra-group">
                        <input type="text" class="form-input palabra-input-temporal" placeholder="Ej: contaminación" required>
                        <button type="button" class="btn btn-danger btn-eliminar" onclick="eliminarInputTemporal(this)" style="display:none;">✖</button>
                    </div>
                </div>
                <button type="button" class="btn btn-outline-secondary" onclick="agregarInputTemporal('palabras')" style="margin-top: 10px;">+ Agregar otra palabra</button>
                <label for="tipoTexto" class="form-label" style="margin-top: 15px;">Tipo de texto:</label>
                <select id="tipoTexto" class="form-select" required>
                    <option value="TITULO">Título</option>
                    <option value="DESCRIPCION">Descripción</option>
                    <option value="CATEGORIA">Categoría</option>
                    <option value="BUSQUEDA">Busqueda</option>
                </select>
            `;
            break;

        case "CriterioEtiquetas":
            campoDiv.innerHTML = `
                <label class="form-label">Etiquetas:</label>
                <div id="lista-etiquetas-temporal" class="palabras-container">
                    <div class="input-palabra-group">
                        <input type="text" class="form-input palabra-input-temporal" placeholder="Ej: contaminación" required>
                        <button type="button" class="btn btn-danger btn-eliminar" onclick="eliminarInputTemporal(this)" style="display:none;">✖</button>
                    </div>
                </div>
                <button type="button" class="btn btn-outline-secondary" onclick="agregarInputTemporal('etiquetas')" style="margin-top: 10px;">+ Agregar otra etiqueta</button>
            `;
            break;

        case "CriterioTipoMultimedia":
            campoDiv.innerHTML = `
                <label for="tipoMultimedia" class="form-label">Tipo de archivo:</label>
                <select id="tipoMultimedia" class="form-select" required>
                    <option value="IMAGEN">Imagen</option>
                    <option value="VIDEO">Video</option>
                    <option value="AUDIO">Audio</option>
                </select>
            `;
            break;

        case "CriterioFecha":
            campoDiv.innerHTML = `
                <label for="fechaInicio" class="form-label">Desde:</label>
                <input type="date" id="fechaInicio" class="form-input" required>
                <label for="fechaFin" class="form-label">Hasta:</label>
                <input type="date" id="fechaFin" class="form-input" required>
                <label for="tipoDeFecha" class="form-label">Tipo de fecha:</label>
                <select id="tipoDeFecha" class="form-select" required>
                    <option value="fechaDelAcontecimiento">Fecha del acontecimiento</option>
                    <option value="fechaDeCarga">Fecha de carga</option>
                </select>
            `;
            break;

        case "CriterioUbicacion":
            campoDiv.innerHTML = `
                <label for="descripcionUbicacion" class="form-label">Descripción de la ubicación:</label>
                <textarea id="descripcionUbicacion" class="form-input" rows="3" required
                          placeholder=" Ej: Argentina o Argentina, Buenos Aires"></textarea>
                <small class="form-text">Ingrese una descripción textual de la ubicación</small>
            `;
            break;

        case "CriterioContribuyente":
            campoDiv.innerHTML = `
                <label for="contribuyente" class="form-label">Nombre del contribuyente:</label>
                <input type="text" id="contribuyente" class="form-input" required>
            `;
            break;
    }
}

function agregarInputTemporal(tipoInput) {
    const contenedorId = tipoInput === 'palabras' ? 'lista-palabras-temporal' : 'lista-etiquetas-temporal';
    const placeholder = tipoInput === 'palabras' ? 'Otra palabra clave' : 'Otra etiqueta';

    const contenedor = document.getElementById(contenedorId);
    const inputsExistentes = contenedor.querySelectorAll('.input-palabra-group');

    if (inputsExistentes.length === 1) {
        const primerBoton = inputsExistentes[0].querySelector('.btn-eliminar');
        primerBoton.style.display = 'inline-block';
    }

    const nuevoInput = document.createElement("div");
    nuevoInput.classList.add("input-palabra-group");
    nuevoInput.innerHTML = `
        <input type="text" class="form-input palabra-input-temporal" placeholder="${placeholder}" required>
        <button type="button" class="btn btn-danger btn-eliminar" onclick="eliminarInputTemporal(this)">✖</button>
    `;
    contenedor.appendChild(nuevoInput);
}

function eliminarInputTemporal(boton) {
    const grupoInput = boton.parentElement;
    const contenedor = grupoInput.parentElement;
    const gruposRestantes = contenedor.querySelectorAll('.input-palabra-group');

    if (gruposRestantes.length === 2) {
        const primerGrupo = gruposRestantes[0];
        const primerBoton = primerGrupo.querySelector('.btn-eliminar');
        primerBoton.style.display = 'none';
    }

    grupoInput.remove();
}

function agregarCriterio() {
    const tipo = document.getElementById("tipoCriterio").value;
    if (!tipo) return;

    const criterioId = "criterio_nuevo_" + Date.now(); // Usamos timestamp para ID único
    const criteriosContainer = document.getElementById("criterios-agregados");

    let cardClass = "", iconClass = "", titulo = "";

    switch(tipo) {
        case "CriterioDeTexto": cardClass = "type-texto"; iconClass = "fas fa-font"; titulo = "Texto"; break;
        case "CriterioEtiquetas": cardClass = "type-etiqueta"; iconClass = "fas fa-tags"; titulo = "Etiquetas"; break;
        case "CriterioTipoMultimedia": cardClass = "type-multimedia"; iconClass = "fas fa-photo-video"; titulo = "Multimedia"; break;
        case "CriterioFecha": cardClass = "type-fecha"; iconClass = "far fa-calendar-alt"; titulo = "Fecha"; break;
        case "CriterioUbicacion": cardClass = "type-ubicacion"; iconClass = "fas fa-map-marker-alt"; titulo = "Ubicación"; break;
        case "CriterioContribuyente": cardClass = "type-contribuyente"; iconClass = "fas fa-user"; titulo = "Contribuyente"; break;
    }

    let cuerpoHTML = '';
    let hiddenInputs = '';

    switch (tipo) {
        case "CriterioDeTexto":
            const palabrasInputs = document.querySelectorAll('#lista-palabras-temporal .palabra-input-temporal');
            const palabras = Array.from(palabrasInputs).map(input => input.value).filter(val => val.trim() !== '');
            const tipoTexto = document.getElementById('tipoTexto').value;

            const badgesPalabras = palabras.map(p => `<span class="badge-item">${p}</span>`).join('');

            cuerpoHTML = `
                <div class="dato-fila">
                    <span class="dato-label">Tipo:</span>
                    <span>${tipoTexto}</span>
                </div>
                <div class="dato-fila" style="align-items: flex-start;">
                    <span class="dato-label">Palabras:</span>
                    <div class="badge-container">${badgesPalabras}</div>
                </div>
            `;

            hiddenInputs = palabras.map(palabra =>
                `<input type="hidden" name="criterios[${criterioId}].palabras" value="${palabra}">`
            ).join('');
            hiddenInputs += `<input type="hidden" name="criterios[${criterioId}].tipoDeTexto" value="${tipoTexto}">`;
            break;

        case "CriterioEtiquetas":
            const etiquetasInputs = document.querySelectorAll('#lista-etiquetas-temporal .palabra-input-temporal');
            const etiquetas = Array.from(etiquetasInputs).map(input => input.value).filter(val => val.trim() !== '');

            const badgesEtiquetas = etiquetas.map(e => `<span class="badge-item">#${e}</span>`).join('');

            cuerpoHTML = `
                <div class="dato-fila" style="align-items: flex-start;">
                    <span class="dato-label">Etiquetas:</span>
                    <div class="badge-container">${badgesEtiquetas}</div>
                </div>
            `;

            hiddenInputs = etiquetas.map(etiqueta =>
                `<input type="hidden" name="criterios[${criterioId}].etiquetas" value="${etiqueta}">`
            ).join('');
            break;

        case "CriterioTipoMultimedia":
            const tipoMultimedia = document.getElementById('tipoMultimedia').value;
            cuerpoHTML = `
                <div class="dato-fila">
                    <span class="dato-label">Formato:</span>
                    <strong>${tipoMultimedia}</strong>
                </div>
            `;
            hiddenInputs = `<input type="hidden" name="criterios[${criterioId}].tipoContenidoMultimedia" value="${tipoMultimedia}">`;
            break;

        case "CriterioFecha":
            const fechaInicio = document.getElementById('fechaInicio').value;
            const fechaFin = document.getElementById('fechaFin').value;
            const tipoDeFecha = document.getElementById('tipoDeFecha').value;
            const tipoFechaLegible = tipoDeFecha === 'fechaDeCarga' ? 'Fecha de Carga' : 'Fecha del Hecho';

            cuerpoHTML = `
                <div class="dato-fila">
                    <span class="dato-label">Rango:</span>
                    <span>${fechaInicio} <strong>al</strong> ${fechaFin}</span>
                </div>
                <div class="dato-fila">
                    <span class="dato-label">Aplica a:</span>
                    <span>${tipoFechaLegible}</span>
                </div>
            `;

            hiddenInputs = `<input type="hidden" name="criterios[${criterioId}].fechaInicio" value="${fechaInicio}">`;
            hiddenInputs += `<input type="hidden" name="criterios[${criterioId}].fechaFin" value="${fechaFin}">`;
            hiddenInputs += `<input type="hidden" name="criterios[${criterioId}].tipoFecha" value="${tipoDeFecha}">`;
            break;

        case "CriterioUbicacion":
            const descripcionUbicacion = document.getElementById('descripcionUbicacion').value.trim();
            if (!descripcionUbicacion) { alert('Ingrese una descripción'); return; }

            cuerpoHTML = `
                <div class="dato-fila">
                    <span class="dato-label">Lugar:</span>
                    <strong>${descripcionUbicacion}</strong>
                </div>
            `;
            // Input oculto plano (solo descripción)
            hiddenInputs = `<input type="hidden" name="criterios[${criterioId}].descripcion" value="${descripcionUbicacion}">`;
            break;

        case "CriterioContribuyente":
            const contribuyente = document.getElementById('contribuyente').value;
            cuerpoHTML = `
                <div class="dato-fila">
                    <span class="dato-label">Usuario:</span>
                    <span>@${contribuyente}</span>
                </div>
            `;
            hiddenInputs = `<input type="hidden" name="criterios[${criterioId}].nombreContribuyente" value="${contribuyente}">`;
            break;
    }

    hiddenInputs += `<input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">`;

    const htmlFinal = `
        <div class="criterio-card ${cardClass}" id="${criterioId}">
            <div class="criterio-header">
                <div class="criterio-info">
                    <span class="criterio-icon"><i class="${iconClass}"></i></span>
                    <span class="criterio-titulo">${titulo}</span>
                </div>
                <button type="button" class="btn-eliminar-criterio" onclick="eliminarCriterio('${criterioId}')">
                    <i class="fas fa-trash"></i> Eliminar
                </button>
            </div>
            <div class="criterio-body">
                ${cuerpoHTML}
                ${hiddenInputs}
            </div>
        </div>
    `;

    criteriosContainer.insertAdjacentHTML('beforeend', htmlFinal);

    document.getElementById("tipoCriterio").value = "";
    document.getElementById("campoCriterio").innerHTML = "";
    document.getElementById("btn-agregar-criterio").disabled = true;
}

function eliminarCriterio(criterioId) {
    const criterioElement = document.querySelector('[id="' + criterioId + '"]') ||
        document.querySelector('input[name*="' + criterioId + '"]')?.closest('.criterio-card');
    if (criterioElement) {
        criterioElement.remove();
    }
}

// Inicializar eventos
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('tipoCriterio').addEventListener('change', mostrarCampoCriterio);
    document.getElementById('btn-agregar-criterio').addEventListener('click', agregarCriterio);
});


// Función para mostrar el modal de éxito
function mostrarModalExito() {
    const modal = document.getElementById('modal-exito');
    const btnRedirigir = document.getElementById('btn-redirigir');

    // Mostrar modal
    modal.classList.add('active');

    // Configurar redirección automática después de 3 segundos
    let contador = 3;
    const intervalo = setInterval(() => {
        contador--;
        if (contador <= 0) {
            clearInterval(intervalo);
            window.location.href = '/colecciones?callback=/admin/panel';
        }
    }, 1000);

    // Configurar botón de redirección manual
    btnRedirigir.onclick = function() {
        clearInterval(intervalo);
        window.location.href = '/colecciones?callback=/admin/panel';
    };

    // Cerrar modal con ESC
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            modal.classList.remove('active');
            window.location.href = '/colecciones?callback=/admin/panel';
        }
    });

    // Cerrar modal haciendo click fuera
    modal.onclick = function(e) {
        if (e.target === modal) {
            modal.classList.remove('active');
            window.location.href = '/colecciones?callback=/admin/panel';
        }
    };
}

// Manejar el envío del formulario con AJAX
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar eventos existentes
    document.getElementById('tipoCriterio').addEventListener('change', mostrarCampoCriterio);
    document.getElementById('btn-agregar-criterio').addEventListener('click', agregarCriterio);

    // Cancelar button
    document.getElementById('btn-cancelar').addEventListener('click', function() {
        if (confirm('¿Seguro que deseas cancelar? Los datos no guardados se perderán.')) {
            window.location.href = '/colecciones?callback=/admin/panel';
        }
    });

    // Manejar envío del formulario
    const form = document.getElementById('form-crear-coleccion');
    const mensajeExito = document.getElementById('mensaje-exito');
    const mensajeError = document.getElementById('mensaje-error');

    form.addEventListener('submit', async function(e) {
        e.preventDefault(); // Prevenir envío tradicional

        // Ocultar mensajes anteriores
        mensajeExito.style.display = 'none';
        mensajeError.style.display = 'none';

        // Validación básica
        if (!form.checkValidity()) {
            alert('Por favor, complete todos los campos requeridos.');
            return;
        }

        // Deshabilitar botón para evitar múltiples envíos
        const submitBtn = form.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.disabled = true;
        submitBtn.textContent = 'Creando...';

        try {
            // Preparar datos del formulario
            const formData = new FormData(form);

            // Enviar datos
            const response = await fetch('/colecciones', {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                // Éxito: mostrar modal
                mostrarModalExito();

                // Opcional: limpiar formulario después de éxito
                setTimeout(() => {
                    form.reset();
                    document.getElementById('criterios-agregados').innerHTML = '';
                    contadorCriterios = 0;
                }, 100);

            } else {
                // Error
                const errorData = await response.json();
                mensajeError.textContent = errorData.message || '❌ Error al crear la colección. Intente nuevamente.';
                mensajeError.style.display = 'block';

                // Scroll al mensaje de error
                mensajeError.scrollIntoView({ behavior: 'smooth' });

                // Re-habilitar botón
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            }

        } catch (error) {
            console.error('Error:', error);
            mensajeError.textContent = '❌ Error de conexión. Verifique su internet e intente.';
            mensajeError.style.display = 'block';

            // Re-habilitar botón
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        }
    });
});