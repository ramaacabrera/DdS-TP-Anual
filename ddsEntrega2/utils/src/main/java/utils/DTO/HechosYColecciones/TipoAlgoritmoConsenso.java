package utils.DTO.HechosYColecciones;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoAlgoritmoConsenso {
    ABSOLUTA, MAYORIASIMPLE, MULTIPLESMENCIONES;

    @JsonValue
    public String toValue() {
        return this.name().toUpperCase();
    }
}
