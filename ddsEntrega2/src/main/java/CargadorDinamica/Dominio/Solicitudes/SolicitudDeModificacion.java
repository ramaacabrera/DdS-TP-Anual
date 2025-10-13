package CargadorDinamica.Dominio.Solicitudes;

import Agregador.HechosYColecciones.HechoModificado;
import utils.DTO.SolicitudDeModificacionDTO;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@DiscriminatorValue("MODIFICACION")
public class SolicitudDeModificacion extends Solicitud {

    @OneToOne
    @JoinColumn(name = "hecho_modificado_id")
    private HechoModificado hechoModificado;
    private EstadoSolicitudModificacion estadoSolicitudModificacion;

    public SolicitudDeModificacion(SolicitudDeModificacionDTO solicitud){
        this.setHechoAsociado(solicitud.getHechoAsociado());
        this.estadoSolicitudModificacion = solicitud.getEstadoSolicitudModificacion();
        //this.setId(UUID.randomUUID());
        this.hechoModificado = solicitud.getHechoModificado();
    }

    public SolicitudDeModificacion(){}

    public void aceptarSolicitudConSugerencia(HechoModificado hecho){
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
