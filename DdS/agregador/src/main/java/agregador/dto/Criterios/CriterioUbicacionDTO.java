package agregador.dto.Criterios;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CriterioUbicacionDTO extends CriterioDTO {

    private String descripcion;

    public CriterioUbicacionDTO() {
        super(null, "CriterioUbicacion");
    }

    public CriterioUbicacionDTO(UUID criterioId, String descripcion) {
        super(criterioId, "CriterioUbicacion");
        this.descripcion = descripcion;
    }

    // Getters y Setters actualizados
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public Map<String, Object> getQueryParameters(){return new HashMap<>();};
    @Override
    public String getQueryCondition(){return "";}
}