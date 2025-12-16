package cargadorDinamico.domain.Solicitudes;

import javax.persistence.*;
import cargadorDinamico.domain.HechosYColeccionesD.HechoModificado_D;
import cargadorDinamico.domain.Usuario.Usuario_D;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
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
    @Column(name = "id_solicitud_modificacion", length = 36, updatable = false, nullable = false)
    protected UUID id_solicitud_modificacion;

    protected String justificacion;

    @Type(type = "uuid-char")
    @Column(name = "hecho_id", length = 36 , updatable = false, nullable = false)
    private UUID ID_HechoAsociado;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "usuario_id")
    private Usuario_D usuario;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hechoModificado_id")
    private HechoModificado_D hechoModificado;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudModificacion_D estadoSolicitudModificacion;

    public SolicitudDeModificacion_D() {}

    // --- GETTERS Y SETTERS CORREGIDOS ---

    // 1. ID de la Solicitud (Faltaba)
    public UUID getId() {
        return id_solicitud_modificacion;
    }

    public void setId(UUID id) {
        this.id_solicitud_modificacion = id;
    }

    // 2. Estado de la Solicitud (Faltaba el Getter)
    public EstadoSolicitudModificacion_D getEstadoSolicitudModificacion() {
        return estadoSolicitudModificacion;
    }

    public void setEstadoSolicitudModificacion(EstadoSolicitudModificacion_D estado) {
        this.estadoSolicitudModificacion = estado;
    }

    // 3. Resto de Getters y Setters (Ya estaban, los mantengo)
    public void setID_HechoAsociado(UUID id_hecho) {
        this.ID_HechoAsociado = id_hecho;
    }

    public UUID getID_HechoAsociado() {
        return ID_HechoAsociado;
    }

    public void setHechoModificado(HechoModificado_D hechoM) {
        this.hechoModificado = hechoM;
    }

    public HechoModificado_D getHechoModificado() {
        return hechoModificado;
    }

    public void setUsuario(Usuario_D _usuario) {
        this.usuario = _usuario;
    }

    public Usuario_D getUsuario() {
        return usuario;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getJustificacion() {
        return justificacion;
    }
}