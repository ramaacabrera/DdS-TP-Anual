package gestorAdministrativo.dto.Criterios;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CriterioDeTextoDTO.class, name = "CriterioDeTexto"),
        @JsonSubTypes.Type(value = CriterioContribuyenteDTO.class, name = "CriterioContribuyente"),
        @JsonSubTypes.Type(value = CriterioEtiquetasDTO.class, name = "CriterioEtiquetas"),
        @JsonSubTypes.Type(value = CriterioFechaDTO.class, name = "CriterioFecha"),
        @JsonSubTypes.Type(value = CriterioTipoFuenteDTO.class, name = "CriterioTipoFuente"),
        @JsonSubTypes.Type(value = CriterioTipoMultimediaDTO.class, name = "CriterioTipoMultimedia"),
        @JsonSubTypes.Type(value = CriterioUbicacionDTO.class, name = "CriterioUbicacion")
})
public abstract class CriterioDTO {
    private UUID criterioId;
    private String tipoCriterio;

    public CriterioDTO() {}

    public CriterioDTO(UUID criterioId, String tipoCriterio) {
        this.criterioId = criterioId;
        this.tipoCriterio = tipoCriterio;
    }

    // Getters y Setters
    public UUID getCriterioId() { return criterioId; }
    public void setCriterioId(UUID criterioId) { this.criterioId = criterioId; }

    public String getTipoCriterio() { return tipoCriterio; }
    public void setTipoCriterio(String tipoCriterio) { this.tipoCriterio = tipoCriterio; }

    // Métodos abstractos que pueden ser útiles
    public abstract Map<String, Object> getQueryParameters();
    public abstract String getQueryCondition();
}