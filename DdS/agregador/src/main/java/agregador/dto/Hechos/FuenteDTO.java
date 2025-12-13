package agregador.dto.Hechos;

import agregador.domain.fuente.TipoDeFuente;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuenteDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID fuenteId;

    private TipoDeFuente tipoDeFuente;
    private String descriptor;

    public FuenteDTO() {}

    public FuenteDTO(UUID fuenteId, TipoDeFuente tipoFuente, String descriptor) {
        this.fuenteId = fuenteId;
        this.tipoDeFuente = tipoFuente;
        this.descriptor = descriptor;
    }

    // Getters y Setters
    public UUID getFuenteId() { return fuenteId; }
    public void setFuenteId(UUID fuenteId) { this.fuenteId = fuenteId; }

    public TipoDeFuente getTipoDeFuente() { return tipoDeFuente; }
    public void setTipoDeFuente(TipoDeFuente tipoFuente) { this.tipoDeFuente = tipoFuente; }

    public String getDescriptor() { return descriptor; }
    public void setDescriptor(String descriptor) { this.descriptor = descriptor; }

    public String getId() {
        return fuenteId != null ? fuenteId.toString() : null;
    }
}