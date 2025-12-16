package cargadorDinamico.domain.DinamicaDto;

import cargadorDinamico.domain.Solicitudes.EstadoSolicitudModificacion;

public class SolicitudDeModificacionDTO extends SolicitudDTO {

    private HechoModificadoDTO hechoModificado;

    private EstadoSolicitudModificacion estadoSolicitudModificacion;

    public SolicitudDeModificacionDTO() {}


    public HechoModificadoDTO getHechoModificado() {
        return hechoModificado;
    }

    public void setHechoModificado(HechoModificadoDTO hechoModificado) {
        this.hechoModificado = hechoModificado;
    }

    public EstadoSolicitudModificacion getEstadoSolicitudModificacion() {
        return estadoSolicitudModificacion;
    }

    public void setEstadoSolicitudModificacion(EstadoSolicitudModificacion estadoSolicitudModificacion) {
        this.estadoSolicitudModificacion = estadoSolicitudModificacion;
    }
}