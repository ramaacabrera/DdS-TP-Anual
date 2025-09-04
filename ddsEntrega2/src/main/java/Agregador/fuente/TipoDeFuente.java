package Agregador.fuente;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoDeFuente {
    ESTATICA, DINAMICA, PROXY;

    @JsonValue
    public String toValue() {
        return this.name().toUpperCase();
    }
}