package estadisticas.domain.Criterios;

import estadisticas.domain.Criterios.Criterio;
import estadisticas.domain.HechosYColecciones.Hecho;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;


public class CriterioContribuyente extends Criterio {

    private String nombreContribuyente;

    public CriterioContribuyente(String nombreContribuyente) {
        this.nombreContribuyente = nombreContribuyente;
    }

    public CriterioContribuyente() {}

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        if (hecho.getContribuyente() == null || this.nombreContribuyente == null) {
            return false;
        }
        return hecho.getContribuyente().getUsername().toLowerCase()
                .contains(this.nombreContribuyente.toLowerCase());
    }

    public String getNombreContribuyente() { return this.nombreContribuyente; };
    public void setNombreContribuyente(String nombreContribuyente) { this.nombreContribuyente = nombreContribuyente; }

    @Override
    public String getQueryCondition() {
        if (nombreContribuyente == null || nombreContribuyente.trim().isEmpty()) {
            return "1=1";
        }
        return "LOWER(h.contribuyente.nombre) LIKE LOWER(:nombreContrib)";
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();
        if (nombreContribuyente != null) {
            params.put("nombreContrib", "%" + nombreContribuyente + "%");
        }
        return params;
    }
}