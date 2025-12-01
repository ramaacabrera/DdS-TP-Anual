package web.domain.Solicitudes;

import web.domain.HechosYColecciones.Hecho;
import web.domain.Usuario.Usuario;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_solicitud", discriminatorType = DiscriminatorType.STRING)
public abstract class Solicitud {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    protected UUID id;

    @Column(columnDefinition = "TEXT")
    protected String justificacion;

    protected Boolean esSpam = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hecho_id")
    protected Hecho hechoAsociado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    protected Usuario usuario;

    public abstract void aceptarSolicitud();
    public abstract void rechazarSolicitud();

    // Getters y Setters base
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }

    public Boolean getEsSpam() { return esSpam; }
    public void setEsSpam(Boolean esSpam) { this.esSpam = esSpam; }

    public Hecho getHechoAsociado() { return hechoAsociado; }
    public void setHechoAsociado(Hecho hechoAsociado) { this.hechoAsociado = hechoAsociado; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}