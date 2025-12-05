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

        case "CriterioTipoFuente":
            campoDiv.innerHTML = `
                <label for="tipoFuente" class="form-label">Tipo de fuente:</label>
                <select id="tipoFuente" class="form-select" required>
                    <option value="">Seleccione un tipo de fuente</option>
                    <option value="ESTATICA">💾 Estática</option>
                    <option value="DINAMICA">⚡ Dinámica</option>
                    <option value="METAMAPA">🗺️ Metamapa</option>
                    <option value="DEMO">🧪 Demo</option>
                </select>
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

    const criterioId = "criterio_" + contadorCriterios;
    contadorCriterios++;
    const criteriosContainer = document.getElementById("criterios-agregados");

    let criterioHTML = '';
    let camposHTML = '';

    switch (tipo) {
        case "CriterioDeTexto":
            const palabrasInputs = document.querySelectorAll('#lista-palabras-temporal .palabra-input-temporal');
            const palabras = Array.from(palabrasInputs).map(input => input.value).filter(val => val.trim() !== '');
            const tipoTexto = document.getElementById('tipoTexto').value;

            camposHTML = palabras.map(palabra =>
                '<input type="hidden" name="criterios[' + criterioId + '].palabras" value="' + palabra + '">'
            ).join('');
            camposHTML += '<input type="hidden" name="criterios[' + criterioId + '].tipoDeTexto" value="' + tipoTexto + '">';

            criterioHTML = `
                <div class="criterio-card">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Texto</span>
                        <button type="button" class="btn-eliminar-criterio" onclick="eliminarCriterio('${criterioId}')">Eliminar</button>
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

            camposHTML = etiquetas.map(etiqueta =>
                '<input type="hidden" name="criterios[' + criterioId + '].etiquetas" value="' + etiqueta + '">'
            ).join('');

            criterioHTML = `
                <div class="criterio-card">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Etiquetas</span>
                        <button type="button" class="btn-eliminar-criterio" onclick="eliminarCriterio('${criterioId}')">Eliminar</button>
                    </div>
                    <div><strong>Etiquetas:</strong> ${etiquetas.join(', ')}</div>
                    ${camposHTML}
                    <input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">
                </div>
            `;
            break;

        case "CriterioTipoMultimedia":
            const tipoMultimedia = document.getElementById('tipoMultimedia').value;
            camposHTML = '<input type="hidden" name="criterios[' + criterioId + '].tipoContenidoMultimedia" value="' + tipoMultimedia + '">';

            criterioHTML = `
                <div class="criterio-card">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Tipo Multimedia</span>
                        <button type="button" class="btn-eliminar-criterio" onclick="eliminarCriterio('${criterioId}')">Eliminar</button>
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

            camposHTML = '<input type="hidden" name="criterios[' + criterioId + '].fechaInicio" value="' + fechaInicio + '">';
            camposHTML += '<input type="hidden" name="criterios[' + criterioId + '].fechaFin" value="' + fechaFin + '">';
            camposHTML += '<input type="hidden" name="criterios[' + criterioId + '].tipoFecha" value="' + tipoDeFecha + '">';

            criterioHTML = `
                <div class="criterio-card">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Fecha</span>
                        <button type="button" class="btn-eliminar-criterio" onclick="eliminarCriterio('${criterioId}')">Eliminar</button>
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
            const descripcionUbicacion = document.getElementById('descripcionUbicacion').value.trim();

            if (!descripcionUbicacion) {
                alert('Por favor, ingrese una descripción de la ubicación');
                return;
            }

            camposHTML = '<input type="hidden" name="criterios[' + criterioId + '].ubicacion.descripcion" value="' + descripcionUbicacion + '">';

            criterioHTML = `
                <div class="criterio-card">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Ubicación</span>
                        <button type="button" class="btn-eliminar-criterio" onclick="eliminarCriterio('${criterioId}')">Eliminar</button>
                    </div>
                    <div><strong>Descripción:</strong> ${descripcionUbicacion}</div>
                    ${camposHTML}
                    <input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">
                </div>
            `;
            break;

        case "CriterioContribuyente":
            const contribuyente = document.getElementById('contribuyente').value;
            camposHTML = '<input type="hidden" name="criterios[' + criterioId + '].nombreContribuyente" value="' + contribuyente + '">';

            criterioHTML = `
                <div class="criterio-card">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Contribuyente</span>
                        <button type="button" class="btn-eliminar-criterio" onclick="eliminarCriterio('${criterioId}')">Eliminar</button>
                    </div>
                    <div><strong>Contribuyente:</strong> ${contribuyente}</div>
                    ${camposHTML}
                    <input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">
                </div>
            `;
            break;

        case "CriterioTipoFuente":
            const tipoFuente = document.getElementById('tipoFuente').value;
            const tipoFuenteTexto = {
                'ESTATICA': '💾 Estática',
                'DINAMICA': '⚡ Dinámica',
                'METAMAPA': '🗺️ Metamapa',
                'DEMO': '🧪 Demo'
            }[tipoFuente] || tipoFuente;

            camposHTML = '<input type="hidden" name="criterios[' + criterioId + '].fuente" value="' + tipoFuente + '">';

            criterioHTML = `
                <div class="criterio-card">
                    <div class="criterio-header">
                        <span class="criterio-tipo">Criterio de Tipo de Fuente</span>
                        <button type="button" class="btn-eliminar-criterio" onclick="eliminarCriterio('${criterioId}')">Eliminar</button>
                    </div>
                    <div><strong>Tipo de fuente:</strong> ${tipoFuenteTexto}</div>
                    ${camposHTML}
                    <input type="hidden" name="criterios[${criterioId}].@type" value="${tipo}">
                </div>
            `;
            break;
    }

    criteriosContainer.insertAdjacentHTML('beforeend', criterioHTML);

    // Limpiar el formulario temporal
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
            window.location.href = '/colecciones';
        }
    }, 1000);

    // Configurar botón de redirección manual
    btnRedirigir.onclick = function() {
        clearInterval(intervalo);
        window.location.href = '/colecciones';
    };

    // Cerrar modal con ESC
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            modal.classList.remove('active');
            window.location.href = '/colecciones';
        }
    });

    // Cerrar modal haciendo click fuera
    modal.onclick = function(e) {
        if (e.target === modal) {
            modal.classList.remove('active');
            window.location.href = '/colecciones';
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
            window.location.href = '/colecciones';
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
            mensajeError.textContent = '❌ Error de conexión. Verifique su internet e intente nuevamente.';
            mensajeError.style.display = 'block';

            // Re-habilitar botón
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        }
    });
});