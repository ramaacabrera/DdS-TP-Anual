package org.example.agregador;

import java.util.Objects;

public class CriterioCategoria extends Criterio {
    private final String categoria;

    public CriterioCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        return Objects.equals(hecho.getCategoria(), categoria);
    }
}
