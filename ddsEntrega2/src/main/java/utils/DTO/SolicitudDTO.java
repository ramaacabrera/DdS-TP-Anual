package utils.DTO;

import Agregador.HechosYColecciones.Hecho;

public class SolicitudDTO {
    private int id;
    private int hechoAsociado;
    private String justificacion;

    public SolicitudDTO() {}

    public int getHechoAsociado() {
        return hechoAsociado;
    }

    public void setHechoAsociado(int hechoAsociado) {
        this.hechoAsociado = hechoAsociado;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public int getId() {return id;}
}
