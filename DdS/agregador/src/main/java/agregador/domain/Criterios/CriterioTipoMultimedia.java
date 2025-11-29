package agregador.domain.Criterios;

import agregador.domain.HechosYColecciones.Hecho;
import agregador.domain.HechosYColecciones.TipoContenidoMultimedia;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.util.HashMap;
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
        if (hecho.getContenidoMultimedia() == null) return false;
        return hecho.getContenidoMultimedia().stream()
                .anyMatch(contenido -> contenido.getTipoContenido().equals(tipoContenidoMultimedia));
    }

    @Override
    public String getQueryCondition() {
        return "EXISTS (SELECT m FROM h.contenidoMultimedia m WHERE m.tipoContenido = :tipoMultiParam)";
    }

    @Override
    @Transient
    public Map<String, Object> getQueryParameters() {
        Map<String, Object> params = new HashMap<>();
        if (tipoContenidoMultimedia != null) {
            params.put("tipoMultiParam", tipoContenidoMultimedia);
        }
        return params;
    }

    public TipoContenidoMultimedia getTipoContenidoMultimedia() {
        return tipoContenidoMultimedia;
    }
}