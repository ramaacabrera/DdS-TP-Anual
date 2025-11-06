package DominioCargadorDinamico.fuente;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoDeFuente {
    ESTATICA, DINAMICA, METAMAPA, DEMO;

    @JsonValue
    public String toValue() {
        return this.name().toUpperCase();
    }
}