    // Datos iniciales de las solicitudes
    const todasSolicitudes = [
        <#list solicitudesEliminacion as solicitud>
    {
        id: "${solicitud.id?js_string}",
        estado: "${solicitud.estado!'PENDIENTE'?js_string}",
        justificacion: "${solicitud.justificacion?js_string}",
        hechoTitulo: "${solicitud.hechoTitulo?js_string}",
        usuario: <#if solicitud.usuario??>{username: "${solicitud.usuario.username?js_string}"}<#else>null</#if>
    }<#if solicitud?has_next>,</#if>
    </#list>
    ];

    todasSolicitudes.forEach(solicitud =>{
        console.log(solicitud);
    })
    // Contar solicitudes por estado
    function contarSolicitudesPorEstado() {
        const contadores = {
            '': todasSolicitudes.length,
            'PENDIENTE': 0,
            'ACEPTADA': 0,
            'RECHAZADA': 0
        };

        todasSolicitudes.forEach(solicitud => {
            if (solicitud.estado === 'PENDIENTE') contadores.PENDIENTE++;
            else if (solicitud.estado === 'ACEPTADA') contadores.ACEPTADA++;
            else if (solicitud.estado === 'RECHAZADA') contadores.RECHAZADA++;
        });

        return contadores;
    }

    // Inicializar contadores
    document.addEventListener('DOMContentLoaded', function() {
        const contadores = contarSolicitudesPorEstado();

        // Actualizar contadores en botones
        document.getElementById('contador-todas').textContent = contadores[''];
        document.getElementById('contador-pendientes').textContent = contadores.PENDIENTE;
        document.getElementById('contador-aceptadas').textContent = contadores.ACEPTADA;
        document.getElementById('contador-rechazadas').textContent = contadores.RECHAZADA;
    });

    // Función para filtrar solicitudes
    function filtrarSolicitudes(estado) {
        // Actualizar estado activo de botones
        document.querySelectorAll('.filtro-btn').forEach(btn => {
            btn.classList.remove('filtro-btn-activo');
            btn.style.backgroundColor = 'var(--bg-light)';
            btn.style.color = 'var(--text-color)';
            btn.style.borderColor = 'var(--border-color)';

            const badge = btn.querySelector('.badge-contador');
            if (badge) {
                badge.style.backgroundColor = 'var(--primary-color)';
                badge.style.color = 'white';
            }
        });

        // Activar botón seleccionado
        const btnActivo = document.querySelector(`.filtro-btn[data-estado="${estado}"]`);
        if (btnActivo) {
            btnActivo.classList.add('filtro-btn-activo');

            if (estado === '') {
                btnActivo.style.backgroundColor = 'var(--primary-color)';
                btnActivo.style.color = 'white';
                btnActivo.style.borderColor = 'var(--primary-dark)';

                const badge = btnActivo.querySelector('.badge-contador');
                if (badge) {
                    badge.style.backgroundColor = 'white';
                    badge.style.color = 'var(--primary-color)';
                }
            } else if (estado === 'PENDIENTE') {
                btnActivo.style.backgroundColor = '#fff3cd';
                btnActivo.style.color = '#856404';
                btnActivo.style.borderColor = '#ffeaa7';

                const badge = btnActivo.querySelector('.badge-contador');
                if (badge) {
                    badge.style.backgroundColor = '#856404';
                    badge.style.color = '#fff3cd';
                }
            } else if (estado === 'ACEPTADA') {
                btnActivo.style.backgroundColor = '#d1edff';
                btnActivo.style.color = '#004085';
                btnActivo.style.borderColor = '#b3d7ff';

                const badge = btnActivo.querySelector('.badge-contador');
                if (badge) {
                    badge.style.backgroundColor = '#004085';
                    badge.style.color = '#d1edff';
                }
            } else if (estado === 'RECHAZADA') {
                btnActivo.style.backgroundColor = '#f8d7da';
                btnActivo.style.color = '#721c24';
                btnActivo.style.borderColor = '#f5c6cb';

                const badge = btnActivo.querySelector('.badge-contador');
                if (badge) {
                    badge.style.backgroundColor = '#721c24';
                    badge.style.color = '#f8d7da';
                }
            }
        }

        // Filtrar solicitudes
        const solicitudesFiltradas = estado === ''
            ? todasSolicitudes
            : todasSolicitudes.filter(s => s.estado === estado);

        // Actualizar lista
        const listaContainer = document.getElementById('lista-solicitudes');
        const sinResultados = document.getElementById('sin-resultados');
        const paginador = document.getElementById('paginador');

        if (solicitudesFiltradas.length === 0) {
            listaContainer.style.display = 'none';
            sinResultados.style.display = 'block';

            if (paginador) paginador.style.display = 'none';

            // Actualizar mensaje de sin resultados
            let titulo = '';
            let mensaje = '';

            if (estado === 'PENDIENTE') {
                titulo = 'No hay solicitudes pendientes';
                mensaje = 'No se encontraron solicitudes con estado "PENDIENTE"';
            } else if (estado === 'ACEPTADA') {
                titulo = 'No hay solicitudes aceptadas';
                mensaje = 'No se encontraron solicitudes con estado "ACEPTADA"';
            } else if (estado === 'RECHAZADA') {
                titulo = 'No hay solicitudes rechazadas';
                mensaje = 'No se encontraron solicitudes con estado "RECHAZADA"';
            } else {
                titulo = 'No hay solicitudes con ese estado';
                mensaje = 'No se encontraron solicitudes con el filtro aplicado';
            }

            document.getElementById('sin-resultados-titulo').textContent = titulo;
            document.getElementById('sin-resultados-mensaje').textContent = mensaje;
        } else {
            listaContainer.style.display = 'block';
            sinResultados.style.display = 'none';

            // Mostrar/ocultar paginador solo cuando se muestra "Todas"
            if (paginador) {
                paginador.style.display = estado === '' ? 'flex' : 'none';
            }

            // Ocultar todas las solicitudes primero
            document.querySelectorAll('.solicitud-item').forEach(item => {
                item.style.display = 'none';
            });

            // Mostrar solo las filtradas
            solicitudesFiltradas.forEach(solicitud => {
                const item = document.querySelector(`.solicitud-item[data-estado="${solicitud.estado}"]`);
                if (item) {
                    item.style.display = 'block';
                }
            });
        }

        // Actualizar información del filtro
        const filtroInfo = document.getElementById('filtro-info');
        const estadoActual = document.getElementById('estado-actual');
        const totalMostrando = document.getElementById('total-mostrando');
        const filtroActualTexto = document.getElementById('filtro-actual-texto');

        if (estado === '') {
            filtroInfo.style.display = 'none';
            totalMostrando.textContent = todasSolicitudes.length;
            filtroActualTexto.textContent = 'Mostrando todas las solicitudes';
        } else {
            filtroInfo.style.display = 'block';
            estadoActual.textContent = estado;
            estadoActual.className = `estado-badge estado-${estado.toLowerCase()}`;
            totalMostrando.textContent = solicitudesFiltradas.length;
            filtroActualTexto.textContent = `Filtrado por estado: ${estado}`;
        }
    }