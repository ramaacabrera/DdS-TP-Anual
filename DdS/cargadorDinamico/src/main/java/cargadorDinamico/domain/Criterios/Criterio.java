package cargadorDinamico.domain.Criterios;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hibernate.annotations.GenericGenerator;
import cargadorDinamico.domain.HechosYColecciones.Hecho;

import javax.persistence.*;
import java.util.Map;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CriterioContribuyente.class, name = "CriterioContribuyente"),
        @JsonSubTypes.Type(value = CriterioDeTexto.class, name = "CriterioDeTexto"),
        @JsonSubTypes.Type(value = CriterioEtiquetas.class, name = "CriterioEtiquetas"),
        @JsonSubTypes.Type(value = CriterioFecha.class, name = "CriterioFecha"),
        @JsonSubTypes.Type(value = CriterioTipoFuente.class, name = "CriterioTipoFuente"),
        @JsonSubTypes.Type(value = CriterioTipoMultimedia.class, name = "CriterioTipoMultimedia"),
        @JsonSubTypes.Type(value = CriterioUbicacion.class, name = "CriterioUbicacion")
})

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Criterio {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id_criterio", updatable = false, nullable = false)
    private UUID id_criterio;

    public abstract boolean cumpleConCriterio(Hecho hecho);
    public Criterio() {}
    public abstract String getQueryCondition();
    public abstract Map<String, Object> getQueryParameters();
}