package DominioGestorAdministrativo.Criterios;

import DominioGestorAdministrativo.HechosYColecciones.Hecho;
import DominioGestorAdministrativo.HechosYColecciones.TipoContenidoMultimedia;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
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
