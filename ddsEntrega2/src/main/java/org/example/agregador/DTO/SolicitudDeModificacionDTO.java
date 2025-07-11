package org.example.agregador.DTO;

import org.example.agregador.Solicitudes.EstadoSolicitudModificacion;
import org.example.agregador.HechosYColecciones.Hecho;

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
