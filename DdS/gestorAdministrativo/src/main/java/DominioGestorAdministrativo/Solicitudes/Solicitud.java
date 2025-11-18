package DominioGestorAdministrativo.Solicitudes;

import DominioGestorAdministrativo.HechosYColecciones.Hecho;
import DominioGestorAdministrativo.Usuario.Usuario;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminador", discriminatorType = DiscriminatorType.STRING)
public abstract class Solicitud {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "id_solicitud", length = 36 , updatable = false, nullable = false)
    protected UUID id_solicitud;

    protected String justificacion;

    private Boolean esSpam = false;

    @ManyToOne
    @JoinColumn(name = "hecho_id")
    private Hecho hechoAsociado;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudModificacion estadoSolicitudModificacion;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudEliminacion estadoSolicitudEliminacion;

    //private String discriminador;

    public abstract void aceptarSolicitud();
    public abstract void rechazarSolicitud();

    //Getters y Setters
    public void setUsuario(Usuario _usuario) {this.usuario = _usuario;}

    public void setHechoAsociado(Hecho idHechoAsociado) {
        this.hechoAsociado = idHechoAsociado;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public void setId(UUID _id_solicitud) {
        this.id_solicitud = _id_solicitud;
    }

    public void setEstadoSolicitudModificacion(EstadoSolicitudModificacion _estadoSolicitudModificacion) {
        this.estadoSolicitudModificacion = _estadoSolicitudModificacion;
    }
    
    public void setEstadoSolicitudEliminacion(EstadoSolicitudEliminacion _estadoSolicitudEliminacion){
        this.estadoSolicitudEliminacion = _estadoSolicitudEliminacion;
    }

    //public void setDiscriminador(String _discriminador) { this.discriminador = _discriminador; }

    public Usuario getUsuario() { return usuario; }


    public Hecho getHechoAsociado() {
        return hechoAsociado;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public UUID getId() { return id_solicitud; }

    public EstadoSolicitudEliminacion getEstadoSolicitudEliminacion() {return estadoSolicitudEliminacion;}

    public EstadoSolicitudModificacion getEstadoSolicitudModificacion() {return estadoSolicitudModificacion;}

    public Boolean getEsSpam() {
        return esSpam;
    }

    public void setEsSpam(Boolean esSpam) {
        this.esSpam = esSpam;
    }

    // public String getDiscriminador() { return discriminador; }
}
