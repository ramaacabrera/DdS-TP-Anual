package gestorAdministrativo.domain.Criterios;

import gestorAdministrativo.domain.HechosYColecciones.Hecho;
import gestorAdministrativo.domain.HechosYColecciones.Ubicacion;

import javax.persistence.*;
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
        return hecho.getUbicacion().getId_ubicacion().equals(ubicacion.getId_ubicacion());
    }

    public Ubicacion getUbicacion() { return ubicacion; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }

    @Override
    public String getQueryCondition() {
        return "h.ubicacion.id_ubicacion = :idUbicacionParam";
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