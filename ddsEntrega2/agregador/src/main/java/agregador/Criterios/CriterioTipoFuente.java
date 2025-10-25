<<<<<<<< HEAD:DdS/utils/src/main/java/utils/Dominio/Criterios/CriterioTipoFuente.java
package utils.Dominio.Criterios;

import utils.Dominio.HechosYColecciones.Hecho;
import utils.Dominio.fuente.*;
========
package agregador.Criterios;

import agregador.HechosYColecciones.Hecho;
import Agregador.fuente.*;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/agregador/src/main/java/agregador/Criterios/CriterioTipoFuente.java

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

    @Override
    public String getQueryCondition() {
        return "h.agregador.fuente = '" + fuente.toString() + "'";
    }
}
