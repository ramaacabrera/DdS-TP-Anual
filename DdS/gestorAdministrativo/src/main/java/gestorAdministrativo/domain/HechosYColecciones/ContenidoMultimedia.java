package gestorAdministrativo.domain.HechosYColecciones;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class ContenidoMultimedia {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "id_contenido", length = 36 , updatable = false, nullable = false)
    private UUID id_contenido;

    @Enumerated(EnumType.STRING)
    private TipoContenidoMultimedia tipoContenido;
    private String contenido;

    @ManyToOne
    @JoinColumn(name = "hecho_id")
    private Hecho hecho;

    @JsonCreator
    public ContenidoMultimedia(@JsonProperty("tipoContenido") TipoContenidoMultimedia tipo,
                               @JsonProperty("contenido") String contenido) {
        this.tipoContenido = tipo;
        this.contenido = contenido;
    }

    public ContenidoMultimedia(){}

    // GETTERS
    public UUID getId_contenido() {return id_contenido;}

    public TipoContenidoMultimedia getTipoContenido() {return tipoContenido;}

    public String getContenido() {return contenido;}

    public Hecho getHecho() {return hecho;}

    // SETTERS
    public void setId_contenido(UUID id_contenido_) {this.id_contenido = id_contenido_;}

    public void setTipoContenido(TipoContenidoMultimedia tipoContenido_) {this.tipoContenido = tipoContenido_;}

    public void setContenido(String contenido_) {this.contenido = contenido_;}

    public void setHecho(Hecho hecho_) {this.hecho = hecho_;}
}