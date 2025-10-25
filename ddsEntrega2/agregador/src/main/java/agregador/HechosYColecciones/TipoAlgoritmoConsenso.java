<<<<<<<< HEAD:DdS/utils/src/main/java/utils/Dominio/HechosYColecciones/TipoAlgoritmoConsenso.java
package utils.Dominio.HechosYColecciones;
========
package agregador.HechosYColecciones;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/agregador/src/main/java/agregador/HechosYColecciones/TipoAlgoritmoConsenso.java

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoAlgoritmoConsenso {
    ABSOLUTA, MAYORIASIMPLE, MULTIPLESMENCIONES;

    @JsonValue
    public String toValue() {
        return this.name().toUpperCase();
    }
}
