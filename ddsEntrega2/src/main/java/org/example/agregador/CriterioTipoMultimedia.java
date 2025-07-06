package org.example.agregador;

public class CriterioTipoMultimedia extends Criterio{
    private TipoContenidoMultimedia tipoContenidoMultimedia;

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        return hecho.getContenidoMultimedia().stream().anyMatch(contenido -> contenido.getTipoContenido().equals(tipoContenidoMultimedia));
    }
}
