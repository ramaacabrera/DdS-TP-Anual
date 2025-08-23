package Agregador.Solicitudes;

import Agregador.HechosYColecciones.Hecho;

public class SolicitudDeModificacion extends Solicitud {
    private Hecho hechoModificado;
    private EstadoSolicitudModificacion estadoSolicitudModificacion;

    public void aceptarSolicitudConSugerencia(Hecho hecho){
        hechoModificado = hecho;
        estadoSolicitudModificacion = EstadoSolicitudModificacion.ACEPTADACONSUGERENCIA;
    }

    @Override
    public void aceptarSolicitud() {
        estadoSolicitudModificacion = EstadoSolicitudModificacion.ACEPTADA;
    }

    @Override
    public void rechazarSolicitud() {
        estadoSolicitudModificacion = EstadoSolicitudModificacion.RECHAZADA;
    }
}
