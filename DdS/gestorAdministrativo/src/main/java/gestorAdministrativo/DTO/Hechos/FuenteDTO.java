package gestorAdministrativo.DTO.Hechos;

import java.util.UUID;

public class FuenteDTO {
    private UUID fuenteId;
    private String tipoFuente;
    private String descriptor;

    public FuenteDTO() {}

    public FuenteDTO(UUID fuenteId, String tipoFuente, String descriptor) {
        this.fuenteId = fuenteId;
        this.tipoFuente = tipoFuente;
        this.descriptor = descriptor;
    }

    // Getters y Setters
    public UUID getFuenteId() { return fuenteId; }
    public void setFuenteId(UUID fuenteId) { this.fuenteId = fuenteId; }

    public String getTipoFuente() { return tipoFuente; }
    public void setTipoFuente(String tipoFuente) { this.tipoFuente = tipoFuente; }

    public String getDescriptor() { return descriptor; }
    public void setDescriptor(String descriptor) { this.descriptor = descriptor; }
}