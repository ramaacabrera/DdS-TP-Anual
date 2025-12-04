package gestorPublico.dto.Hechos;

import java.util.UUID;

public class FuenteDTO {
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
    public UUID getId() { return fuenteId; }
    public void setId(UUID fuenteId) { this.fuenteId = fuenteId; }

    public String getTipoDeFuente() { return tipoDeFuente; }
    public void setTipoDeFuente(String tipoFuente) { this.tipoDeFuente = tipoFuente; }

    public String getDescriptor() { return descriptor; }
    public void setDescriptor(String descriptor) { this.descriptor = descriptor; }
}