package org.example.agregador.Solicitudes;

import org.example.agregador.HechosYColecciones.Hecho;

public abstract class Solicitud {

    private String id;
    private Hecho hechoAsociado;
    private String justificacion;

    public abstract void aceptarSolicitud();
    public abstract void rechazarSolicitud();

    public void evaluarSpam(){
        if(DetectorDeSpam.esSpam(this.justificacion)){
            this.rechazarSolicitud();
        }
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

    public String getId() {return id;}
}
