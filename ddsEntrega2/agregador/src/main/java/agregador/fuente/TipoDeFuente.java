<<<<<<<< HEAD:DdS/utils/src/main/java/utils/Dominio/fuente/TipoDeFuente.java
package utils.Dominio.fuente;
========
package agregador.fuente;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/agregador/src/main/java/agregador/fuente/TipoDeFuente.java

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoDeFuente {
    ESTATICA, DINAMICA, METAMAPA, DEMO;

    @JsonValue
    public String toValue() {
        return this.name().toUpperCase();
    }
}