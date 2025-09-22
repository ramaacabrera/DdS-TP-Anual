package Agregador.Solicitudes;

import Agregador.HechosYColecciones.Hecho;
import utils.DTO.SolicitudDeModificacionDTO;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
@DiscriminatorValue("MODIFICACION")
public class SolicitudDeModificacion extends Solicitud {

    @OneToOne
    @JoinColumn(name = "hecho_id")
    private Hecho hechoModificado;
    private EstadoSolicitudModificacion estadoSolicitudModificacion;

    public SolicitudDeModificacion(SolicitudDeModificacionDTO solicitud){
        this.setHechoAsociado(solicitud.getHechoAsociado());
        this.estadoSolicitudModificacion = solicitud.getEstadoSolicitudModificacion();
        this.setId(UUID.randomUUID());
        this.hechoModificado = solicitud.getHechoModificado();
    }

    public SolicitudDeModificacion(){}

    public void aceptarSolicitudConSugerencia(Hecho hecho){
        hechoModificado = hecho;
        estadoSolicitudModificacion = EstadoSolicitudModificacion.ACEPTADACONSUGERENCIA;
    }

    @Override
    public void aceptarSolicitud() {
        estadoSolicitudModificacion = EstadoSolicitudModificacion.ACEPTADA;
    }

    @Override
    public void rechazarSolicitud() {
        estadoSolicitudModificacion = EstadoSolicitudModificacion.RECHAZADA;
    }
}
