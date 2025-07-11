package org.example.agregador.Solicitudes;


import org.example.agregador.DTO.SolicitudDeEliminacionDTO;

import java.util.UUID;

public class SolicitudDeEliminacion extends Solicitud {

    private EstadoSolicitudEliminacion estado;

    public SolicitudDeEliminacion(SolicitudDeEliminacionDTO dto) {
        this.setHechoAsociado(dto.getHechoAsociado());
        this.setJustificacion(dto.getJustificacion());
        this.estado = dto.getEstado();
        this.setId(UUID.randomUUID().toString());
    }

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

