package Agregador.Criterios;

import Agregador.HechosYColecciones.Hecho;

import java.util.Objects;

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
}
