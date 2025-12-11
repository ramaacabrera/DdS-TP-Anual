package estadisticas.domain.Solicitudes;

import estadisticas.domain.HechosYColecciones.HechoModificado;

import javax.persistence.*;


@DiscriminatorValue("MODIFICACION")
public class SolicitudDeModificacion extends Solicitud {

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_modificacion")
    private EstadoSolicitudModificacion estado;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hecho_modificado_id")
    private HechoModificado hechoModificado;

    public SolicitudDeModificacion() {
        this.estado = EstadoSolicitudModificacion.PENDIENTE;
    }

    @Override
    public void aceptarSolicitud() {
        this.estado = EstadoSolicitudModificacion.ACEPTADA;
    }

    @Override
    public void rechazarSolicitud() {
        this.estado = EstadoSolicitudModificacion.RECHAZADA;
    }

    // Getters y Setters
    public EstadoSolicitudModificacion getEstado() { return estado; }
    public void setEstado(EstadoSolicitudModificacion estado) { this.estado = estado; }

    public HechoModificado getHechoModificado() { return hechoModificado; }
    public void setHechoModificado(HechoModificado hechoModificado) { this.hechoModificado = hechoModificado; }
}