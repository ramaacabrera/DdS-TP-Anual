package utils.Dominio.Solicitudes;


import utils.DTO.SolicitudDeEliminacionDTO;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.Persistencia.HechoRepositorio;

import javax.persistence.*;
import java.util.UUID;

@Entity
@DiscriminatorValue("ELIMINACION")
public class SolicitudDeEliminacion extends Solicitud {

    private EstadoSolicitudEliminacion estado;

    public SolicitudDeEliminacion(SolicitudDeEliminacionDTO dto, HechoRepositorio hechoRepositorio) {
        //this.setHechoAsociado(new Hecho(dto.getHechoAsociado()));
        this.setJustificacion(dto.getJustificacion());
        this.setUsuario(dto.getUsuario());
        this.setEstadoSolicitudEliminacion(dto.getEstado());
        //this.setId(UUID.randomUUID());

        UUID idHecho = dto.getID_HechoAsociado();
        if (idHecho != null) {
            Hecho hechoCompleto = hechoRepositorio.buscarPorId(idHecho.toString());
            this.setHechoAsociado(hechoCompleto);
        } else {
            throw new IllegalArgumentException("ID de hecho no puede ser nulo");
        }
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

