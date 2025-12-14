package gestorPublico.domain.Criterios;

import gestorPublico.domain.HechosYColecciones.Hecho;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class CriterioUbicacion extends Criterio {

    @Column(name = "descripcion_ubicacion")
    private String descripcion;

    public CriterioUbicacion() {}

    public CriterioUbicacion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean cumpleConCriterio(Hecho hecho){
        if (hecho.getUbicacion() == null ||
                hecho.getUbicacion().getDescripcion() == null ||
                this.descripcion == null) {
            return false;
        }

        String hechoDesc = hecho.getUbicacion().getDescripcion().toLowerCase();
        String criterioDesc = this.descripcion.toLowerCase();

        return hechoDesc.contains(criterioDesc);
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String getQueryCondition() {
        return "LOWER(h.ubicacion.descripcion) LIKE LOWER(CONCAT('%', :descripcionUbicacionParam, '%'))";
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();
        if (descripcion != null) {
            params.put("descripcionUbicacionParam", descripcion);
        }
        return params;
    }
}