package Agregador.DTO;

import Agregador.Solicitudes.EstadoSolicitudModificacion;
import Agregador.HechosYColecciones.Hecho;

public class SolicitudDeModificacionDTO extends SolicitudDTO {
    private Hecho hechoModificado;
    private EstadoSolicitudModificacion estadoSolicitudModificacion;

    public SolicitudDeModificacionDTO() {}

    public Hecho getHechoModificado() {
        return hechoModificado;
    }

    public void setHechoModificado(Hecho hechoModificado) {
        this.hechoModificado = hechoModificado;
    }

    public EstadoSolicitudModificacion getEstadoSolicitudModificacion() {
        return estadoSolicitudModificacion;
    }

    public void setEstadoSolicitudModificacion(EstadoSolicitudModificacion estadoSolicitudModificacion) {
        this.estadoSolicitudModificacion = estadoSolicitudModificacion;
    }
}
