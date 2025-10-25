package utils.Dominio.Solicitudes;

import utils.DTO.SolicitudDeModificacionDTO;

import utils.Dominio.HechosYColecciones.Hecho;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
@DiscriminatorValue("MODIFICACION")
public class SolicitudDeModificacion extends Solicitud {

    // CORRECCIÓN: Agregar annotation @Enumerated para JPA
    @Enumerated(EnumType.STRING)
    private EstadoSolicitudModificacion estadoSolicitudModificacion;

    // CORRECCIÓN: Si necesitas referencia al hecho modificado, descomenta y usa:
    /*
    @OneToOne
    @JoinColumn(name = "hecho_modificado_id")
    private HechoModificado hechoModificado;
    */

    public SolicitudDeModificacion(SolicitudDeModificacionDTO solicitud){
        this.setHechoAsociado(new Hecho(solicitud.getHechoAsociado()));

        // CORRECCIÓN: Manejar posible null
        if (solicitud.getEstadoSolicitudModificacion() != null) {
            this.estadoSolicitudModificacion = solicitud.getEstadoSolicitudModificacion();
        } else {
            this.estadoSolicitudModificacion = EstadoSolicitudModificacion.PENDIENTE; // valor por defecto
        }

        //this.setId(UUID.randomUUID());
        //this.hechoModificado = solicitud.getHechoModificado();
    }

    public SolicitudDeModificacion(){}

    // CORRECCIÓN: Métodos actualizados
    /*
    public void aceptarSolicitudConSugerencia(HechoModificado hecho){
        this.hechoModificado = hecho;
        this.estadoSolicitudModificacion = EstadoSolicitudModificacion.ACEPTADA_CON_SUGERENCIA;
    }
    */

    @Override
    public void aceptarSolicitud() {
        this.estadoSolicitudModificacion = EstadoSolicitudModificacion.ACEPTADA;
    }

    @Override
    public void rechazarSolicitud() {
        this.estadoSolicitudModificacion = EstadoSolicitudModificacion.RECHAZADA;
    }

    // CORRECCIÓN: Getters y setters necesarios
    public EstadoSolicitudModificacion getEstadoSolicitudModificacion() {
        return estadoSolicitudModificacion;
    }

    public void setEstadoSolicitudModificacion(EstadoSolicitudModificacion estadoSolicitudModificacion) {
        this.estadoSolicitudModificacion = estadoSolicitudModificacion;
    }

    /*
    public HechoModificado getHechoModificado() {
        return hechoModificado;
    }

    public void setHechoModificado(HechoModificado hechoModificado) {
        this.hechoModificado = hechoModificado;
    }
    */
}