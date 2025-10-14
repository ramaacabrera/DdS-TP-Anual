package CargadorDinamica.Dominio.Solicitudes;

import CargadorDinamica.Dominio.HechosYColecciones.Hecho_D;
import CargadorDinamica.Dominio.Usuario.Usuario_D;
import org.hibernate.annotations.GenericGenerator;
import utils.DTO.SolicitudDeEliminacionDTO;


import javax.persistence.*;
import java.util.UUID;

@Entity
public class SolicitudDeEliminacion_D {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id_solicitud_eliminacion", updatable = false, nullable = false)
    protected UUID id_solicitud_eliminacion;

    protected String justificacion;

    @ManyToOne
    @JoinColumn(name = "hecho_id")
    private Hecho_D hechoAsociado;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario_D usuario;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudEliminacion_D estadoSolicitudEliminacion;

    public SolicitudDeEliminacion_D() {}

    //Getters y Setters
    public void setUsuario(Usuario_D _usuario) {this.usuario = _usuario;}
    public void setHechoAsociado(Hecho_D idHechoAsociado) {
        this.hechoAsociado = idHechoAsociado;
    }
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }
    public void setId(UUID _id_solicitud) {
        this.id_solicitud_eliminacion = _id_solicitud;
    }
    public void setEstadoSolicitudEliminacion(EstadoSolicitudEliminacion_D _estadoSolicitudEliminacion){
        this.estadoSolicitudEliminacion = _estadoSolicitudEliminacion;
    }

    public Usuario_D getUsuario() { return usuario; }
    public Hecho_D getHechoAsociado() {
        return hechoAsociado;
    }
    public String getJustificacion() {
        return justificacion;
    }
    public UUID getId() { return id_solicitud_eliminacion; }
    public EstadoSolicitudEliminacion_D getEstadoSolicitudEliminacion() {return estadoSolicitudEliminacion;}

}