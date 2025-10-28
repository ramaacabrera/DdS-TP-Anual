package utils.DTO;

import utils.Dominio.HechosYColecciones.HechoModificado;
import utils.Dominio.Solicitudes.EstadoSolicitudModificacion;

public class SolicitudDeModificacionDTO extends SolicitudDTO {
    private HechoModificado hechoModificado;
    private EstadoSolicitudModificacion estadoSolicitudModificacion;

    public SolicitudDeModificacionDTO() {}

   public HechoModificado getHechoModificado() {
        return hechoModificado;
    }

    public void setHechoModificado(HechoModificado hechoModificado) {
        this.hechoModificado = hechoModificado;
    }

    public EstadoSolicitudModificacion getEstadoSolicitudModificacion() {
        return estadoSolicitudModificacion;
    }

    public void setEstadoSolicitudModificacion(EstadoSolicitudModificacion estadoSolicitudModificacion) {
        this.estadoSolicitudModificacion = estadoSolicitudModificacion;
    }
}
