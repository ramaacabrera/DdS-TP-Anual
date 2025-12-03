package cargadorDinamico.domain.DinamicaDto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import cargadorDinamico.domain.fuente.TipoDeFuente;

public class FuenteDTO {
    private TipoDeFuente tipoDeFuente;
    private String descriptor;

    public FuenteDTO() {}

    @JsonCreator
    public FuenteDTO(@JsonProperty("tipoDeFuente") TipoDeFuente tipoDeFuente,
                     @JsonProperty("descriptor") String descriptorNuevo) {
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
    public String getDescriptor() {
        return descriptor;
    }

    @JsonProperty("ruta")
    public void setDescriptor(String descriptorNuevo) {
        this.descriptor = descriptorNuevo;
    }
}