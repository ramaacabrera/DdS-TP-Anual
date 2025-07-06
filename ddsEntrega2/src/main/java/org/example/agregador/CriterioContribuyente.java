package org.example.agregador;

public class CriterioContribuyente extends Criterio{
    private Contribuyente contribuyente;

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        return hecho.getContribuyente()==contribuyente;
    }
}
