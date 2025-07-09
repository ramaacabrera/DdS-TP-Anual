package org.example.agregador;

public class CriterioContribuyente extends Criterio{
    private final Contribuyente contribuyente;

    public CriterioContribuyente(Contribuyente contribuyenteNuevo) {contribuyente = contribuyenteNuevo;}

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        return hecho.getContribuyente()==contribuyente;
    }
}
