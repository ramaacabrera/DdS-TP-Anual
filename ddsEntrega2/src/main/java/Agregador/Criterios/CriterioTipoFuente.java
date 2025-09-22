package Agregador.Criterios;

import Agregador.HechosYColecciones.Hecho;
import Agregador.fuente.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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
}
