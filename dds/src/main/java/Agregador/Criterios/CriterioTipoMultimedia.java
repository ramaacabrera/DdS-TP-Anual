package Agregador.Criterios;

import Agregador.HechosYColecciones.Hecho;
import Agregador.HechosYColecciones.TipoContenidoMultimedia;

import javax.persistence.*;

@Entity
public class CriterioTipoMultimedia extends Criterio {
    @Enumerated(EnumType.STRING)
    private TipoContenidoMultimedia tipoContenidoMultimedia;

    public CriterioTipoMultimedia(TipoContenidoMultimedia tipoContenidoMultimediaNuevo) {
        this.tipoContenidoMultimedia = tipoContenidoMultimediaNuevo;
    }

    public CriterioTipoMultimedia() {}

    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        return hecho.getContenidoMultimedia().stream().anyMatch(contenido -> contenido.getTipoContenido().equals(tipoContenidoMultimedia));
    }

    @Override
    public String getQueryCondition() {
        return "";//"h.contribuyente = " + contribuyente.getId_usuario();
    }
}
