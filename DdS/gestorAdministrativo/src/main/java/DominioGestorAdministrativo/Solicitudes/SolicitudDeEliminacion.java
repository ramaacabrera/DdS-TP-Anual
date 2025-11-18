package DominioGestorAdministrativo.Solicitudes;


import DominioGestorAdministrativo.HechosYColecciones.Hecho;
import DominioGestorAdministrativo.DTO.Solicitudes.SolicitudDeEliminacionDTO;
import gestorAdministrativo.repository.HechoRepositorio;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.UUID;

@Entity
@DiscriminatorValue("ELIMINACION")
public class SolicitudDeEliminacion extends Solicitud {

    private EstadoSolicitudEliminacion estado;

    /*public SolicitudDeEliminacion(SolicitudDeEliminacionDTO dto, HechoRepositorio hechoRepositorio) {
        this.setJustificacion(dto.getJustificacion());
        this.setUsuario(dto.getUsuario());
        this.setEstadoSolicitudEliminacion(dto.getEstado());

        UUID idHecho = dto.getID_HechoAsociado();
        if (idHecho != null) {
            Hecho hechoCompleto = hechoRepositorio.buscarPorId(idHecho);
            this.setHechoAsociado(hechoCompleto);
        } else {
            throw new IllegalArgumentException("ID de hecho no puede ser nulo");
        }
    }*/

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

