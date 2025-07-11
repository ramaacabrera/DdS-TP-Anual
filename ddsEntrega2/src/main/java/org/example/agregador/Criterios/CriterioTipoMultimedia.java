package org.example.agregador.Criterios;

import org.example.agregador.HechosYColecciones.Hecho;
import org.example.agregador.HechosYColecciones.TipoContenidoMultimedia;

public class CriterioTipoMultimedia extends Criterio {
    private TipoContenidoMultimedia tipoContenidoMultimedia;

    public CriterioTipoMultimedia(TipoContenidoMultimedia tipoContenidoMultimediaNuevo) {
        this.tipoContenidoMultimedia = tipoContenidoMultimediaNuevo;
    }

    public CriterioTipoMultimedia() {}

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        return hecho.getContenidoMultimedia().stream().anyMatch(contenido -> contenido.getTipoContenido().equals(tipoContenidoMultimedia));
    }
}
