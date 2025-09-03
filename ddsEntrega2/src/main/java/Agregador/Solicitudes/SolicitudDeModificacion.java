package Agregador.Solicitudes;

import Agregador.HechosYColecciones.Hecho;
import utils.DTO.SolicitudDeModificacionDTO;

public class SolicitudDeModificacion extends Solicitud {
    private Hecho hechoModificado;
    private EstadoSolicitudModificacion estadoSolicitudModificacion;

    public SolicitudDeModificacion(SolicitudDeModificacionDTO solicitud){
        this.setHechoAsociado(solicitud.getHechoAsociado());
        this.estadoSolicitudModificacion = solicitud.getEstadoSolicitudModificacion();
        this.id = solicitud.getId();
        this.hechoModificado = solicitud.getHechoModificado();
    }

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
