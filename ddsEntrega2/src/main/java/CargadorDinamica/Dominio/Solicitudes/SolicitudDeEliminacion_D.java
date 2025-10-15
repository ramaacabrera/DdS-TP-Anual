package CargadorDinamica.Dominio.Solicitudes;

import CargadorDinamica.DinamicaDto.SolicitudEliminacion_D_DTO;;
import CargadorDinamica.Dominio.Usuario.Usuario_D;
import org.hibernate.annotations.GenericGenerator;

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
    private UUID ID_hechoAsociado;  //LO MODIFIQUEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario_D usuario;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudEliminacion_D estadoSolicitudEliminacion;

    public SolicitudDeEliminacion_D() {}

    public SolicitudDeEliminacion_D(SolicitudEliminacion_D_DTO solElimDTO) {
        this.justificacion = solElimDTO.getJustificacion();
        this.ID_hechoAsociado = solElimDTO.getid_HechoAsociado();
        this.usuario = solElimDTO.getUsuario();
        this.estadoSolicitudEliminacion = EstadoSolicitudEliminacion_D.PENDIENTE;
    }

    //Getters y Setters
    public void setUsuario(Usuario_D _usuario) {this.usuario = _usuario;}
    public void setid_HechoAsociado(UUID idHechoAsociado) {
        this.ID_hechoAsociado = idHechoAsociado;
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
    public UUID getid_HechoAsociado() {
        return ID_hechoAsociado;
    }
    public String getJustificacion() {
        return justificacion;
    }
    public UUID getId() { return id_solicitud_eliminacion; }
    public EstadoSolicitudEliminacion_D getEstadoSolicitudEliminacion() {return estadoSolicitudEliminacion;}

}