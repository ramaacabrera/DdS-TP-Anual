package utils.Dominio.Criterios;

import utils.Dominio.HechosYColecciones.Hecho;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Map;
import java.util.Objects;

@Entity
public class CriterioCategoria extends Criterio {
    private String categoria;
    public CriterioCategoria() {}
    public CriterioCategoria(String categoria) {
        this.categoria = categoria;
    }


    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        return Objects.equals(hecho.getCategoria(), categoria);
    }

    @Override
    public String getQueryCondition() {return "h.categoria = '" + categoria + "'";}

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) {this.categoria = categoria;}

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        return Map.of();
    }
}
