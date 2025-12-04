package gestorPublico.dto.Criterios;

import gestorPublico.domain.Criterios.CriterioContribuyente;

import java.util.Map;
import java.util.UUID;

public class CriterioContribuyenteDTO extends CriterioDTO {
    private String nombreContribuyente;

    public CriterioContribuyenteDTO() {
        super(null, "CRITERIO_CONTRIBUYENTE");
    }

    public CriterioContribuyenteDTO(UUID criterioId, String nombreContribuyente) {
        super(criterioId, "CRITERIO_CONTRIBUYENTE");
        this.nombreContribuyente = nombreContribuyente;
    }
    public CriterioContribuyenteDTO(CriterioContribuyente c) {
        super(c.getId(), "CRITERIO_CONTRIBUYENTE");
        this.nombreContribuyente = c.getNombreContribuyente();
    }

    // Getters y Setters
    public String getNombreContribuyente() { return nombreContribuyente; }
    public void setNombreContribuyente(String nombreContribuyente) { this.nombreContribuyente = nombreContribuyente; }

    @Override
    public String getQueryCondition() {
        if (nombreContribuyente == null || nombreContribuyente.trim().isEmpty()) {
            return "1=1";
        }
        return "h.contribuyente in (SELECT u.id_usuario FROM Usuario u WHERE u.nombre LIKE '%" +
                nombreContribuyente + "%')";
    }

    @Override
    public Map<String, Object> getQueryParameters() {
        return Map.of();
    }
}