package org.example;

import java.util.List;

public class CriterioEtiquetas extends Criterio{
    private List<String> etiquetas;

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        boolean existeEnHecho = false;
        for(String etiqueta : etiquetas){
            existeEnHecho = hecho.getEtiquetas().contains(etiqueta);
        }
        return existeEnHecho;
    }
}
