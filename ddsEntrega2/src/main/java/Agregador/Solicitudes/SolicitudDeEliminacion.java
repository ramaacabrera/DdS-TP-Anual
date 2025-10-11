package Agregador.Solicitudes;


import utils.DTO.SolicitudDTO;
import utils.DTO.SolicitudDeEliminacionDTO;

import javax.persistence.*;
import java.util.UUID;

@Entity
@DiscriminatorValue("ELIMINACION")
public class SolicitudDeEliminacion extends Solicitud {

    private EstadoSolicitudEliminacion estado;

    public SolicitudDeEliminacion(SolicitudDeEliminacionDTO dto) {
        this.setHechoAsociado(dto.getHechoAsociado());
        this.setJustificacion(dto.getJustificacion());
        this.setUsuario(dto.getUsuario());
        this.setEstadoSolicitudEliminacion(dto.getEstado());
        //this.setId(UUID.randomUUID());
    }

    public SolicitudDeEliminacion(){}

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

