function initMap(hechos, mapId) {
    // Si no hay hechos con ubicación, inicializa un mapa genérico
    if (!hechos || hechos.length === 0) {
        // Inicializa un mapa centrado en un punto por defecto
        const defaultMap = L.map(mapId).setView([0, 0], 2);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(defaultMap);

        // Muestra un mensaje dentro del contenedor del mapa
        document.getElementById(mapId).innerHTML = '<p style="text-align:center; padding-top: 150px; color: #6c757d;">No hay hechos con ubicación para mostrar.</p>';
        return;
    }

    // Centrar el mapa en el primer hecho
    const firstHecho = hechos[0];
    const map = L.map(mapId).setView([firstHecho.lat, firstHecho.lon], 13); // Zoom inicial a 13

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    // Bucle para colocar marcadores y asignar la redirección
    hechos.forEach(hecho => {
        L.marker([hecho.lat, hecho.lon])
            .addTo(map)
            .bindPopup(`
                <b>${hecho.titulo}</b><br>
                ${hecho.desc}<br>
                <a href="/hechos/${hecho.id}" style="font-weight: bold; color: var(--primary-color);">Ver Detalles &rarr;</a>
            `)
            .on('click', function(e) {
                // Redirigir al hacer clic en el marcador
                L.DomEvent.stopPropagation(e); // Prevenir el pop-up nativo
                window.location.href = `/hechos/${hecho.id}`;
            });
    });

    setTimeout(function() {
        map.invalidateSize(true);
    }, 100);
}