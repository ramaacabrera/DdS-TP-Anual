package utils.DTO;

import Agregador.fuente.TipoDeFuente;

public class FuenteDTO {
    private TipoDeFuente tipoDeFuente;
    private String ruta;

    public FuenteDTO(TipoDeFuente tipoDeFuente, String ruta) {
        this.tipoDeFuente = tipoDeFuente;
        this.ruta = ruta;
    }

    public TipoDeFuente getTipoDeFuente() {
        return tipoDeFuente;
    }

    public void setTipoDeFuente(TipoDeFuente tipoDeFuente) {
        this.tipoDeFuente = tipoDeFuente;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }


}
