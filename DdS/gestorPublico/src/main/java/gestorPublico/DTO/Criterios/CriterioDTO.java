package gestorPublico.DTO.Criterios;

import java.util.Map;
import java.util.UUID;

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