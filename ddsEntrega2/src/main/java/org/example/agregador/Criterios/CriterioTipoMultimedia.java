package org.example.agregador.Criterios;

import org.example.agregador.HechosYColecciones.Hecho;
import org.example.agregador.HechosYColecciones.TipoContenidoMultimedia;

public class CriterioTipoMultimedia extends Criterio {
    private final TipoContenidoMultimedia tipoContenidoMultimedia;

    public CriterioTipoMultimedia(TipoContenidoMultimedia tipoContenidoMultimediaNuevo) {
        this.tipoContenidoMultimedia = tipoContenidoMultimediaNuevo;
    }

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        return hecho.getContenidoMultimedia().stream().anyMatch(contenido -> contenido.getTipoContenido().equals(tipoContenidoMultimedia));
    }
}
