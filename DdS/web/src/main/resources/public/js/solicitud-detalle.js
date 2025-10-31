async function procesarSolicitud(accion) {
    const { id, tipo } = window.solicitudData;

    console.log(`🎯 Procesando solicitud: ${id}, tipo: ${tipo}, acción: ${accion}`);

    try {
        const response = await fetch(`/api/solicitudes/${tipo}/${id}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ estado: accion })
        });

        console.log(`📡 Response status: ${response.status}`);

        if (response.ok) {
            const result = await response.text();
            console.log(`✅ Solicitud ${accion.toLowerCase()} correctamente:`, result);
            alert(`Solicitud ${accion.toLowerCase()} correctamente`);
            // Recargar la página para ver el cambio de estado
            window.location.reload();
        } else {
            const errorText = await response.text();
            console.error(`❌ Error al procesar solicitud: ${response.status}`, errorText);
            alert(`Error: ${errorText}`);
        }
    } catch (error) {
        console.error('❌ Error de red:', error);
        alert('Error de conexión. Intente nuevamente.');
    }
}