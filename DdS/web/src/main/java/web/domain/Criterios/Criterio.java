package web.domain.Criterios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import web.domain.HechosYColecciones.Hecho;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Map;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CriterioDeTexto.class, name = "CriterioDeTexto"),
        @JsonSubTypes.Type(value = CriterioUbicacion.class, name = "CriterioUbicacion"),
        @JsonSubTypes.Type(value = CriterioFecha.class, name = "CriterioFecha"),
        @JsonSubTypes.Type(value = CriterioEtiquetas.class, name = "CriterioEtiquetas"),
        @JsonSubTypes.Type(value = CriterioTipoFuente.class, name = "CriterioTipoFuente"),
        @JsonSubTypes.Type(value = CriterioTipoMultimedia.class, name = "CriterioTipoMultimedia"),
        @JsonSubTypes.Type(value = CriterioContribuyente.class, name = "CriterioContribuyente")
})

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Criterio {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    @Column(name = "id_criterio", length = 36, updatable = false, nullable = false)
    private UUID id;

    public Criterio() {}

    public abstract boolean cumpleConCriterio(Hecho hecho);

    public abstract String getQueryCondition();
    public abstract Map<String, Object> getQueryParameters();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTipoCriterio() {
        return this.getClass().getSimpleName();
    }
}