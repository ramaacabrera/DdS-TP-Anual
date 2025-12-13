package estadisticas.domain.Criterios;

import estadisticas.domain.Criterios.Criterio;
import estadisticas.domain.HechosYColecciones.Hecho;
import estadisticas.domain.HechosYColecciones.Ubicacion;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;


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
        return hecho.getUbicacion().getId_ubicacion().equals(ubicacion.getId_ubicacion());
    }

    public Ubicacion getUbicacion() { return ubicacion; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }

    @Override
    public String getQueryCondition() {
        return "h.ubicacion.descripcion = "+ ubicacion.getDescripcion();
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();
        if (ubicacion != null) {
            params.put("idUbicacionParam", ubicacion.getId_ubicacion());
        }
        return params;
    }
}