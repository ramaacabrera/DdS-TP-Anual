package CargadorDinamica.Dominio.Solicitudes;


import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import CargadorDinamica.DinamicaDto.SolicitudModificacion_D_DTO;
import CargadorDinamica.Dominio.Solicitudes.EstadoSolicitudModificacion_D;
import CargadorDinamica.Dominio.HechosYColecciones.Hecho_D;
import CargadorDinamica.Dominio.Usuario.Usuario_D;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.util.UUID;

@Entity
public class SolicitudDeModificacion_D {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id_solicitud_modificacion", updatable = false, nullable = false)
    protected UUID id_solicitud_modificacion;

    protected String justificacion;

    @ManyToOne
    @JoinColumn(name = "hecho_id")
    private UUID ID_hechoAsociado;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario_D usuario;

    @ManyToOne
    @JoinColumn(name = "hechoModificado_id")
    private Hecho_D hechoModificado; //NO SE SI SE HACE ASI O SI HAY QUE HACER OTRA TABLA O SI HAY QUE DIFERENCIARLOS POR UN ATRIBUTO

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudModificacion_D estadoSolicitudModificacion;

    public SolicitudDeModificacion_D() {}

    public SolicitudDeModificacion_D(SolicitudModificacion_D_DTO solModDTO) {
        this.justificacion = solModDTO.getJustificacion();
        this.ID_hechoAsociado = solModDTO.getid_HechoAsociado();
        this.usuario = solModDTO.getUsuario();
        this.hechoModificado = solModDTO.getHechoModificado();
        this.estadoSolicitudModificacion = EstadoSolicitudModificacion_D.PENDIENTE;
    }

    //Getters y Setters
    public void setUsuario(Usuario_D _usuario) {this.usuario = _usuario;}
    public void setID_HechoAsociado(UUID idHechoAsociado) {
        this.ID_hechoAsociado = idHechoAsociado;
    }
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }
    public void setId(UUID _id_solicitud) {
        this.id_solicitud_modificacion = _id_solicitud;
    }
    public void setEstadoSolicitudModificacion(EstadoSolicitudModificacion_D estadoSolicitudModificacion){
        this.estadoSolicitudModificacion = estadoSolicitudModificacion;
    }
    public void serHechoModificado(Hecho_D hechoM) {this.hechoModificado = hechoM; }

    public Usuario_D getUsuario() { return usuario; }
    public UUID getID_HechoAsociado() {
        return ID_hechoAsociado;
    }
    public String getJustificacion() {
        return justificacion;
    }
    public UUID getId() { return id_solicitud_modificacion; }
    public EstadoSolicitudModificacion_D getEstadoSolicitudModificacion() {return estadoSolicitudModificacion;}
    public Hecho_D getHechoModificado() {return hechoModificado; }
}
