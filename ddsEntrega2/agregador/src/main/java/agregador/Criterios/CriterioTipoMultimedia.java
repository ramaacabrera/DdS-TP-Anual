<<<<<<<< HEAD:DdS/utils/src/main/java/utils/Dominio/Criterios/CriterioTipoMultimedia.java
package utils.Dominio.Criterios;

import utils.Dominio.HechosYColecciones.Hecho;
import utils.Dominio.HechosYColecciones.TipoContenidoMultimedia;
========
package agregador.Criterios;

import agregador.HechosYColecciones.Hecho;
import agregador.HechosYColecciones.TipoContenidoMultimedia;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/agregador/src/main/java/agregador/Criterios/CriterioTipoMultimedia.java

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
