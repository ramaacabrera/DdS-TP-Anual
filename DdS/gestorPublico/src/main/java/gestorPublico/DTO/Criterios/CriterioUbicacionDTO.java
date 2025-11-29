package gestorPublico.DTO.Criterios;

import gestorPublico.DTO.Hechos.UbicacionDTO;

import java.util.Map;
import java.util.UUID;

public class CriterioUbicacionDTO extends CriterioDTO {
    private UbicacionDTO ubicacion;

    public CriterioUbicacionDTO() {
        super(null, "CRITERIO_UBICACION");
    }

    public CriterioUbicacionDTO(UUID criterioId, UbicacionDTO ubicacion) {
        super(criterioId, "CRITERIO_UBICACION");
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public UbicacionDTO getUbicacion() { return ubicacion; }
    public void setUbicacion(UbicacionDTO ubicacion) { this.ubicacion = ubicacion; }

    @Override
    public String getQueryCondition() {
        return "h.id_ubicacion = " + ubicacion.getUbicacionId();
    }

    @Override
    public Map<String, Object> getQueryParameters() {
        return Map.of();
    }
}