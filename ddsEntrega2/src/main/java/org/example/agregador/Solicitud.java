package org.example.agregador;

public abstract class Solicitud {

    private Hecho hechoAsociado;
    private String justificacion;

    public abstract void aceptarSolicitud();
    public abstract void rechazarSolicitud();

    public void evaluarSpam(){
        if(DetectorDeSpam.esSpam(this.justificacion)){
            this.rechazarSolicitud();
        };
    }

    public void setHechoAsociado(Hecho hechoAsociado) {
        this.hechoAsociado = hechoAsociado;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public Hecho getHechoAsociado() {
        return hechoAsociado;
    }

    public String getJustificacion() {
        return justificacion;
    }
}
