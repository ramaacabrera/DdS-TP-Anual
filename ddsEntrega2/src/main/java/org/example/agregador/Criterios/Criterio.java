package org.example.agregador.Criterios;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.example.agregador.HechosYColecciones.Hecho;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CriterioCategoria.class, name = "CriterioCategoria"),
        @JsonSubTypes.Type(value = CriterioContribuyente.class, name = "CriterioContribuyente"),
        @JsonSubTypes.Type(value = CriterioDeTexto.class, name = "CriterioDeTexto"),
        @JsonSubTypes.Type(value = CriterioEtiquetas.class, name = "CriterioEtiquetas"),
        @JsonSubTypes.Type(value = CriterioFecha.class, name = "CriterioFecha"),
        @JsonSubTypes.Type(value = CriterioTipoFuente.class, name = "CriterioTipoFuente"),
        @JsonSubTypes.Type(value = CriterioTipoMultimedia.class, name = "CriterioTipoMultimedia"),
        @JsonSubTypes.Type(value = CriterioUbicacion.class, name = "CriterioUbicacion")
})

public abstract class Criterio {
    public abstract boolean cumpleConCriterio(Hecho hecho);
    public Criterio() {}
}