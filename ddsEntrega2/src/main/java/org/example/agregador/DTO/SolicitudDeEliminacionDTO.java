package org.example.agregador.DTO;

import org.example.agregador.Solicitudes.EstadoSolicitudEliminacion;

public class SolicitudDeEliminacionDTO extends SolicitudDTO {
    private EstadoSolicitudEliminacion estado;

    public SolicitudDeEliminacionDTO() {}

    public EstadoSolicitudEliminacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitudEliminacion estado) {
        this.estado = estado;
    }
}
