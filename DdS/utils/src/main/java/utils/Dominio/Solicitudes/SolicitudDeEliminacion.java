package utils.Dominio.Solicitudes;


import utils.DTO.SolicitudDeEliminacionDTO;
import utils.Dominio.HechosYColecciones.Hecho;

import javax.persistence.*;

@Entity
@DiscriminatorValue("ELIMINACION")
public class SolicitudDeEliminacion extends Solicitud {

    private EstadoSolicitudEliminacion estado;

    public SolicitudDeEliminacion(SolicitudDeEliminacionDTO dto) {
        this.setHechoAsociado(new Hecho(dto.getHechoAsociado()));
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

