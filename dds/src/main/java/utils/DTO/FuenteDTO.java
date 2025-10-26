package utils.DTO;

import Agregador.fuente.TipoDeFuente;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FuenteDTO {
    private TipoDeFuente tipoDeFuente;
    private String ruta;

    public FuenteDTO() {}

    @JsonCreator
    public FuenteDTO(@JsonProperty("tipoDeFuente") TipoDeFuente tipoDeFuente,
                     @JsonProperty("ruta") String ruta) {
        this.tipoDeFuente = tipoDeFuente;
        this.ruta = ruta;
    }

    @JsonProperty("tipoDeFuente")
    public TipoDeFuente getTipoDeFuente() {
        return tipoDeFuente;
    }

    @JsonProperty("tipoDeFuente")
    public void setTipoDeFuente(TipoDeFuente tipoDeFuente) {
        this.tipoDeFuente = tipoDeFuente;
    }

    @JsonProperty("ruta")
    public String getRuta() {
        return ruta;
    }

    @JsonProperty("ruta")
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}