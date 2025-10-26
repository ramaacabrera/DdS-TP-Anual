package cargadorDinamico.Dominio.Solicitudes;

import cargadorDinamico.DinamicaDto.SolicitudEliminacion_D_DTO;;
import cargadorDinamico.Dominio.Usuario.Usuario_D;
import cargadorDinamico.Dominio.HechosYColeccionesD.Hecho_D;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

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
    @Type(type = "uuid-char")
    @Column(name = "id_solicitud_eliminacion",length = 36, updatable = false, nullable = false)
    protected UUID id_solicitud_eliminacion;

    protected String justificacion;

    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "hecho_id")
    private Hecho_D hechoAsociado;

    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id")
    private Usuario_D usuario;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudEliminacion_D estadoSolicitudEliminacion;

    public SolicitudDeEliminacion_D() {}

    /*public SolicitudDeEliminacion_D(SolicitudEliminacion_D_DTO solElimDTO) {
        this.justificacion = solElimDTO.getJustificacion();
        this.hechoAsociado = solElimDTO.getHechoAsociado();
        this.usuario = solElimDTO.getUsuario();
        this.estadoSolicitudEliminacion = EstadoSolicitudEliminacion_D.PENDIENTE;
    }*/

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