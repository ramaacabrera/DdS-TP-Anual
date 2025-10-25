package utils.DTO;

<<<<<<<< HEAD:DdS/utils/src/main/java/utils/DTO/FuenteDTO.java
import utils.Dominio.fuente.TipoDeFuente;
========
import utils.DTO.HechosYColecciones.TipoDeFuente;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/utils/src/main/java/utils/DTO/FuenteDTO.java
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