package web.dto.Hechos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class FuenteDTO {
    @JsonProperty("id")
    private UUID fuenteId;
    private String tipoDeFuente;
    private String descriptor;

    public FuenteDTO() {}

    public FuenteDTO(UUID fuenteId, String tipoFuente, String descriptor) {
        this.fuenteId = fuenteId;
        this.tipoDeFuente = tipoFuente;
        this.descriptor = descriptor;
    }

    // Getters y Setters
    public UUID getFuenteId() { return fuenteId; }
    public void setFuenteId(UUID fuenteId) { this.fuenteId = fuenteId; }

    public String getTipoDeFuente() { return tipoDeFuente; }
    public void setTipoDeFuente(String tipoFuente) { this.tipoDeFuente = tipoFuente; }

    public String getDescriptor() { return descriptor; }
    public void setDescriptor(String descriptor) { this.descriptor = descriptor; }
}