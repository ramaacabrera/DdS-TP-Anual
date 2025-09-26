package Agregador.Criterios;

import Agregador.HechosYColecciones.Hecho;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class CriterioEtiquetas extends Criterio {
    private List<String> etiquetas;

    public CriterioEtiquetas() {}
    public CriterioEtiquetas(List<String> listaEtiquetas) {etiquetas = listaEtiquetas;}

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        boolean existeEnHecho = false;
        for(String etiqueta : etiquetas){
            existeEnHecho = hecho.getEtiquetas().contains(etiqueta);
        }
        return existeEnHecho;
    }

    public List<String> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<String> etiquetas) {etiquetas = etiquetas;}

    @Override
    public String getQueryCondition() {
        return "";
    }
//        StringBuilder retorno = new StringBuilder("( 1=1");
//        for(String etiqueta : etiquetas){
//        retorno.append(" or h.descripcion like '%").append(etiqueta).append("%'");
//
//        }
//        return retorno.toString();}
}
