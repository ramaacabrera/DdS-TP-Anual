package utils.DTO;

import Agregador.HechosYColecciones.Hecho;

public class SolicitudDTO {
    private Hecho hechoAsociado;
    private String justificacion;

    public SolicitudDTO() {}

    public Hecho getHechoAsociado() {
        return hechoAsociado;
    }

    public void setHechoAsociado(Hecho hechoAsociado) {
        this.hechoAsociado = hechoAsociado;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }
}
