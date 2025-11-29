package cargadorEstatico.domain.criterios;

import cargadorEstatico.domain.hechosycolecciones.Hecho;
import cargadorEstatico.domain.fuente.TipoDeFuente;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.util.Map;

@Entity
public class CriterioTipoFuente extends Criterio {

    @Enumerated(EnumType.STRING)
    private TipoDeFuente fuente;

    public CriterioTipoFuente() {}

    public CriterioTipoFuente(TipoDeFuente fuente) { this.fuente = fuente;}

    @Override
    public boolean cumpleConCriterio(Hecho hecho) { return hecho.getFuente().equals(fuente);}

    public TipoDeFuente getFuente() { return fuente; }

    public void setFuente(TipoDeFuente fuente) {this.fuente = fuente;}

    @Override
    public String getQueryCondition() {
        return "h.agregador.fuente = '" + fuente.toString() + "'";
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        return Map.of();
    }
}
