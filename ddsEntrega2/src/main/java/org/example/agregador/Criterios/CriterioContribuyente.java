package org.example.agregador.Criterios;

import org.example.agregador.Contribuyente;
import org.example.agregador.HechosYColecciones.Hecho;

public class CriterioContribuyente extends Criterio {
    private Contribuyente contribuyente;

    public CriterioContribuyente(Contribuyente contribuyenteNuevo) {contribuyente = contribuyenteNuevo;}
    public CriterioContribuyente() {}
    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        return hecho.getContribuyente()==contribuyente;
    }
}
