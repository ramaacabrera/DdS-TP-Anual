package gestorPublico.dto.Hechos;

import java.util.UUID;

public class FuenteDTO {
    private UUID id;
    private String tipoDeFuente;
    private String descriptor;

    public FuenteDTO() {}

    public FuenteDTO(UUID fuenteId, String tipoFuente, String descriptor) {
        this.id = fuenteId;
        this.tipoDeFuente = tipoFuente;
        this.descriptor = descriptor;
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID fuenteId) { this.id = fuenteId; }

    public String getTipoDeFuente() { return tipoDeFuente; }
    public void setTipoDeFuente(String tipoFuente) { this.tipoDeFuente = tipoFuente; }

    public String getDescriptor() { return descriptor; }
    public void setDescriptor(String descriptor) { this.descriptor = descriptor; }
}