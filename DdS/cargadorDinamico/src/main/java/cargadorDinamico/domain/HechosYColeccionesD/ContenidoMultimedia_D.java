package cargadorDinamico.domain.HechosYColeccionesD;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class ContenidoMultimedia_D {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "id_contenido", length = 36, updatable = false, nullable = false)
    private UUID id_contenido;

    @Enumerated(EnumType.STRING)
    private TipoContenidoMultimedia_D tipoContenido;
    private String contenido;

    @ManyToOne
    @JoinColumn(name = "hecho_id")
    @JsonIgnore
    private Hecho_D hechoId;

    @JsonCreator
    public ContenidoMultimedia_D(@JsonProperty("tipoContenido") TipoContenidoMultimedia_D tipo,
                               @JsonProperty("contenido") String contenido) {
        this.tipoContenido = tipo;
        this.contenido = contenido;
    }

    public ContenidoMultimedia_D(){}

    // GETTERS
    public UUID getId_contenido() {return id_contenido;}

    public TipoContenidoMultimedia_D getTipoContenido() {return tipoContenido;}

    public String getContenido() {return contenido;}

    public Hecho_D getHechoId() {return hechoId;}

    // SETTERS
    public void setId_contenido(UUID id_contenido_) {this.id_contenido = id_contenido_;}

    public void setTipoContenido(TipoContenidoMultimedia_D tipoContenido_) {this.tipoContenido = tipoContenido_;}

    public void setContenido(String contenido_) {this.contenido = contenido_;}

    public void setHechoId(Hecho_D hecho_) {this.hechoId = hecho_;}
}