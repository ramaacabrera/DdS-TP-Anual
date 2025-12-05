package gestorAdministrativo.domain.Criterios;

import gestorAdministrativo.domain.HechosYColecciones.Hecho;
import gestorAdministrativo.domain.HechosYColecciones.Ubicacion;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class CriterioUbicacion extends Criterio {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacion;

    public CriterioUbicacion() {}
    public CriterioUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public boolean cumpleConCriterio(Hecho hecho){
        if (hecho.getUbicacion() == null || this.ubicacion == null) return false;
        return hecho.getUbicacion().getDescripcion().equals(ubicacion.getDescripcion());
    }

    public Ubicacion getUbicacion() { return ubicacion; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }

    @Override
    public String getQueryCondition() {
        return "h.ubicacion.descripcion LIKE :descripcionUbicacionParam";
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();
        if (ubicacion != null && ubicacion.getDescripcion() != null) {
            // Para búsqueda parcial
            params.put("descripcionUbicacionParam", "%" + ubicacion.getDescripcion() + "%");
            // Para búsqueda exacta (sin %)
            // params.put("descripcionUbicacionParam", ubicacion.getDescripcion());
        }
        return params;
    }
}