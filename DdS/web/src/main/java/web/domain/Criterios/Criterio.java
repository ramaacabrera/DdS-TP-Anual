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
        property = "tipoCriterio"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CriterioContribuyente.class, name = "CRITERIO_CONTRIBUYENTE"),
        @JsonSubTypes.Type(value = CriterioDeTexto.class, name = "CRITERIO_TEXTO"),
        @JsonSubTypes.Type(value = CriterioEtiquetas.class, name = "CRITERIO_ETIQUETAS"),
        @JsonSubTypes.Type(value = CriterioFecha.class, name = "CRITERIO_FECHA"),
        @JsonSubTypes.Type(value = CriterioTipoFuente.class, name = "CRITERIO_TIPO_FUENTE"),
        @JsonSubTypes.Type(value = CriterioTipoMultimedia.class, name = "CRITERIO_TIPO_MULTIMEDIA"),
        @JsonSubTypes.Type(value = CriterioUbicacion.class, name = "CRITERIO_UBICACION")
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
}