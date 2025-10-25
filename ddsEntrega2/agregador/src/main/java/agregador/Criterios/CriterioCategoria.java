<<<<<<<< HEAD:DdS/utils/src/main/java/utils/Dominio/Criterios/CriterioCategoria.java
package utils.Dominio.Criterios;

import utils.Dominio.HechosYColecciones.Hecho;
========
package agregador.Criterios;

import agregador.HechosYColecciones.Hecho;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/agregador/src/main/java/agregador/Criterios/CriterioCategoria.java

import javax.persistence.Entity;
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
}
