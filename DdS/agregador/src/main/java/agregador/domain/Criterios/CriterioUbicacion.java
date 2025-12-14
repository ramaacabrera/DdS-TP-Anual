package agregador.domain.Criterios;

import agregador.domain.HechosYColecciones.Hecho;
import agregador.domain.HechosYColecciones.Ubicacion;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;

@Entity
public class CriterioUbicacion extends Criterio {

    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacion;

    public CriterioUbicacion() {}
    public CriterioUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public boolean cumpleConCriterio(Hecho hecho){
        if (hecho.getUbicacion() == null || this.ubicacion == null) return false;
        if (hecho.getUbicacion().getDescripcion() == null || this.ubicacion.getDescripcion() == null) return false;

        return hecho.getUbicacion().getDescripcion().equalsIgnoreCase(ubicacion.getDescripcion());
        }

    public Ubicacion getUbicacion() { return ubicacion; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }

    @Override
    public String getQueryCondition() {
        return "LOWER(h.ubicacion.descripcion) = LOWER(:descripcionUbicacionParam)";
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();
        if (ubicacion != null && ubicacion.getDescripcion() != null) {
            params.put("descripcionUbicacionParam", ubicacion.getDescripcion());
        }
        return params;
    }
}