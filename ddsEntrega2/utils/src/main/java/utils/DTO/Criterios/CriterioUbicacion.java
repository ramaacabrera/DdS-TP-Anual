package utils.DTO.Criterios;

import agregador.HechosYColecciones.Hecho;
import agregador.HechosYColecciones.Ubicacion;

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
}
