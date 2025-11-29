package gestorAdministrativo.domain.Solicitudes;

import javax.persistence.*;

@Entity
@DiscriminatorValue("ELIMINACION")
public class SolicitudDeEliminacion extends Solicitud {

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_eliminacion")
    private EstadoSolicitudEliminacion estado;

    public SolicitudDeEliminacion() {
        this.estado = EstadoSolicitudEliminacion.PENDIENTE; // Estado por defecto
    }

    public SolicitudDeEliminacion(String justificacion) {
        this();
        this.justificacion = justificacion;
    }

    @Override
    public void aceptarSolicitud() {
        this.estado = EstadoSolicitudEliminacion.ACEPTADA;
    }

    @Override
    public void rechazarSolicitud() {
        this.estado = EstadoSolicitudEliminacion.RECHAZADA;
    }

    public EstadoSolicitudEliminacion getEstado() { return estado; }
    public void setEstado(EstadoSolicitudEliminacion estado) { this.estado = estado; }
}