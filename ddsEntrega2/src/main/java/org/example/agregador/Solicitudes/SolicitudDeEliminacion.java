package org.example.agregador.Solicitudes;


public class SolicitudDeEliminacion extends Solicitud {

    private EstadoSolicitudEliminacion estado;

    @Override
    public void aceptarSolicitud() {
        this.estado = EstadoSolicitudEliminacion.ACEPTADA;
        // Setear hecho como oculto
    }

    @Override
    public void rechazarSolicitud() {
        this.estado = EstadoSolicitudEliminacion.RECHAZADA;
    }
}

