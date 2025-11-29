package agregador.domain.Criterios;

import agregador.domain.HechosYColecciones.Hecho;
import agregador.domain.HechosYColecciones.Ubicacion;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.Map;

@Entity
public class CriterioUbicacion extends Criterio {

    @OneToOne
    @JoinColumn(name = "id_ubicacion")
    private Ubicacion ubicacion;

    public CriterioUbicacion() {}
    public CriterioUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public boolean cumpleConCriterio(Hecho hecho){
        return hecho.getUbicacion().equals(ubicacion);
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public String getQueryCondition() {
        return "h.id_ubicacion = " + ubicacion.getId_ubicacion();
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        return Map.of();
    }
}
