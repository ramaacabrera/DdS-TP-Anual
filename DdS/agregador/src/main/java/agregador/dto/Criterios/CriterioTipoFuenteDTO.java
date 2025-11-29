package agregador.dto.Criterios;

import java.util.Map;
import java.util.UUID;

public class CriterioTipoFuenteDTO extends CriterioDTO {
    private TipoDeFuenteDTO fuente;

    public CriterioTipoFuenteDTO() {
        super(null, "CRITERIO_TIPO_FUENTE");
    }

    public CriterioTipoFuenteDTO(UUID criterioId, TipoDeFuenteDTO fuente) {
        super(criterioId, "CRITERIO_TIPO_FUENTE");
        this.fuente = fuente;
    }

    // Getters y Setters
    public TipoDeFuenteDTO getFuente() { return fuente; }
    public void setFuente(TipoDeFuenteDTO fuente) { this.fuente = fuente; }

    @Override
    public String getQueryCondition() {
        return "h.agregador.fuente = '" + fuente.toString() + "'";
    }

    @Override
    public Map<String, Object> getQueryParameters() {
        return Map.of();
    }
}