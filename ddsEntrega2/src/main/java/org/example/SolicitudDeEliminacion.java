package org.example;


public class SolicitudDeEliminacion extends Solicitud {

    private EstadoSolicitudEliminacion estado;

    @Override
    public void aceptarSolicitud() {
        this.estado = EstadoSolicitudEliminacion.ACEPTADA;
    }

    @Override
    public void rechazarSolicitud() {
        this.estado = EstadoSolicitudEliminacion.RECHAZADA;
    }
}

