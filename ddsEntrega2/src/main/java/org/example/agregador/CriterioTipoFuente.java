package org.example.agregador;

public class CriterioTipoFuente extends Criterio{

    private Fuente fuente;

    public CriterioTipoFuente(Fuente fuente) {
        this.fuente = fuente;
    }

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        return hecho.getFuente().equals(fuente);
    }

    public Fuente getFuente() {
        return fuente;
    }

    public void setFuente(Fuente fuente) {
        this.fuente = fuente;
    }
}
