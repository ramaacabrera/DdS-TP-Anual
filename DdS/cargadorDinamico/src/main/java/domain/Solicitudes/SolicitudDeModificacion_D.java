package domain.Solicitudes;


import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import domain.HechosYColeccionesD.Hecho_D;
import domain.Usuario.Usuario_D;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;


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
    @Type(type = "uuid-char")
    @Column(name = "id_solicitud_modificacion", length =36,  updatable = false, nullable = false)
    protected UUID id_solicitud_modificacion;

    protected String justificacion;
    
    @Type(type = "uuid-char")
    @Column(name = "hecho_id", length = 36 , updatable = false, nullable = false)
    private UUID hecho_id;

    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id")
    private Usuario_D usuario;

    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "hechoModificado_id")
    private Hecho_D hechoModificado; //NO SE SI SE HACE ASI O SI HAY QUE HACER OTRA TABLA O SI HAY QUE DIFERENCIARLOS POR UN ATRIBUTO

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudModificacion_D estadoSolicitudModificacion;

    public SolicitudDeModificacion_D() {}

    /*public SolicitudDeModificacion_D(SolicitudModificacion_D_DTO solModDTO) {
        this.justificacion = solModDTO.getJustificacion();
        this.hechoAsociado = solModDTO.getID_HechoAsociado();
        this.usuario = solModDTO.getUsuario();
        this.hechoModificado = solModDTO.getHechoModificado();
        this.estadoSolicitudModificacion = EstadoSolicitudModificacion_D.PENDIENTE;
    }*/

    //Getters y Setters

    public void setID_HechoAsociado(UUID id_hecho) { this.hecho_id = id_hecho;}
    public void setUsuario(Usuario_D _usuario) {this.usuario = _usuario;}
    //public void setHechoAsociado(Hecho_D HechoAsociado) { this.hechoAsociado = HechoAsociado;}
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }
    public void setId(UUID _id_solicitud) {
        this.id_solicitud_modificacion = _id_solicitud;
    }
    public void setEstadoSolicitudModificacion(EstadoSolicitudModificacion_D estadoSolicitudModificacion){
        this.estadoSolicitudModificacion = estadoSolicitudModificacion;
    }
    public void setHechoModificado(Hecho_D hechoM) {this.hechoModificado = hechoM; }

    public Usuario_D getUsuario() { return usuario; }
    public UUID getID_HechoAsociado() {
        return hecho_id;
    }
    public String getJustificacion() {
        return justificacion;
    }
    public UUID getId() { return id_solicitud_modificacion; }
    public EstadoSolicitudModificacion_D getEstadoSolicitudModificacion() {return estadoSolicitudModificacion;}
    public Hecho_D getHechoModificado() {return hechoModificado; }
}
