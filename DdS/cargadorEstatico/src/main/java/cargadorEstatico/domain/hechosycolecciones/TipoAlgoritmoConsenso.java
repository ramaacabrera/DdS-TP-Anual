package cargadorEstatico.domain.hechosycolecciones;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoAlgoritmoConsenso {
    ABSOLUTA, MAYORIASIMPLE, MULTIPLESMENCIONES;

    @JsonValue
    public String toValue() {
        return this.name().toUpperCase();
    }
}
