package utils.Dominio.Criterios;

import utils.Dominio.HechosYColecciones.Hecho;
import utils.Dominio.HechosYColecciones.TipoContenidoMultimedia;

import javax.persistence.*;
import java.util.Map;

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

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        return Map.of();
    }
}
