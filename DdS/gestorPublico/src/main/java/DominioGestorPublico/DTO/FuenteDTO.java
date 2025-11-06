package DominioGestorPublico.DTO;

import DominioGestorPublico.fuente.TipoDeFuente;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FuenteDTO {
    private TipoDeFuente tipoDeFuente;
    private String descriptor;

    public FuenteDTO() {}

    @JsonCreator
    public FuenteDTO(@JsonProperty("tipoDeFuente") TipoDeFuente tipoDeFuente,
                     @JsonProperty("ruta") String descriptorNuevo) {
        this.tipoDeFuente = tipoDeFuente;
        this.descriptor = descriptorNuevo;
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
        return descriptor;
    }

    @JsonProperty("ruta")
    public void setRuta(String descriptorNuevo) {
        this.descriptor = descriptorNuevo;
    }
}