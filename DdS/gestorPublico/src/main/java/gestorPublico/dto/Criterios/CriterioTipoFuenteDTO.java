package gestorPublico.dto.Criterios;

import gestorPublico.domain.Criterios.CriterioTipoFuente;
import gestorPublico.domain.fuente.TipoDeFuente;

import java.util.Map;
import java.util.UUID;

public class CriterioTipoFuenteDTO extends CriterioDTO {
    private TipoDeFuenteDTO fuente;

    public CriterioTipoFuenteDTO() {
        super(null, "CRITERIO_TIPO_FUENTE");
    }
    public CriterioTipoFuenteDTO(CriterioTipoFuente c){
        super(c.getId(), "CRITERIO_TIPO_FUENTE");
        this.fuente = this.convertirFuente(c.getFuente());
    }

    public TipoDeFuenteDTO convertirFuente(TipoDeFuente t){
        switch (t){
            case ESTATICA:
                return TipoDeFuenteDTO.ESTATICA;
            case DINAMICA:
                return TipoDeFuenteDTO.DINAMICA;
            case METAMAPA:
                return TipoDeFuenteDTO.METAMAPA;
            case DEMO:
                return TipoDeFuenteDTO.DEMO;
            default:
                return null;
        }
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