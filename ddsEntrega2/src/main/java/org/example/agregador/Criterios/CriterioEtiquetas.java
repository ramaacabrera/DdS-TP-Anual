package org.example.agregador.Criterios;

import org.example.agregador.HechosYColecciones.Hecho;

import java.util.List;

public class CriterioEtiquetas extends Criterio {
    private List<String> etiquetas;

    public CriterioEtiquetas(List<String> listaEtiquetas) {etiquetas = listaEtiquetas;}

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        boolean existeEnHecho = false;
        for(String etiqueta : etiquetas){
            existeEnHecho = hecho.getEtiquetas().contains(etiqueta);
        }
        return existeEnHecho;
    }
}
