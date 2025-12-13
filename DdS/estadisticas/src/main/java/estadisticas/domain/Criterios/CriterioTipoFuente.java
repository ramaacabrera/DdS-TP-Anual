package estadisticas.domain.Criterios;

import estadisticas.domain.Criterios.Criterio;
import estadisticas.domain.HechosYColecciones.Hecho;
import estadisticas.domain.fuente.TipoDeFuente;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;


public class CriterioTipoFuente extends Criterio {

    @Enumerated(EnumType.STRING)
    private TipoDeFuente fuente;

    public CriterioTipoFuente() {}
    public CriterioTipoFuente(TipoDeFuente fuente) { this.fuente = fuente;}

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        if (hecho.getFuente() == null || this.fuente == null) return false;
        return hecho.getFuente().getTipoDeFuente().equals(fuente);
    }

    public TipoDeFuente getFuente() { return fuente; }
    public void setFuente(TipoDeFuente fuente) {this.fuente = fuente;}

    @Override
    public String getQueryCondition() {
        return "h.fuente.tipoDeFuente = :tipoFuenteParam";
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();
        if (fuente != null) {
            params.put("tipoFuenteParam", fuente);
        }
        return params;
    }
}