package estadisticas.domain.HechosYColecciones;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoAlgoritmoConsenso {
    ABSOLUTA, MAYORIA_SIMPLE, MULTIPLES_MENCIONES;

    @JsonValue
    public String toValue() {
        return this.name().toUpperCase();
    }
}
