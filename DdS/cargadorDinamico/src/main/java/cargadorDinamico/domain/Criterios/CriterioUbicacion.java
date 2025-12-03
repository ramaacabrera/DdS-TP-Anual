package cargadorDinamico.domain.Criterios;

import cargadorDinamico.domain.HechosYColecciones.Hecho;
import cargadorDinamico.domain.HechosYColecciones.Ubicacion;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.Map;

@Entity
public class CriterioUbicacion extends Criterio {

    @OneToOne
    @JoinColumn(name = "ubicacionId")
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
        return "h.ubicacionId = " + ubicacion.getUbicacionId();
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        return Map.of();
    }
}
