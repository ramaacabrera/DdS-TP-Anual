package Agregador.Solicitudes;

import Agregador.HechosYColecciones.Hecho;

public abstract class Solicitud {

    protected int id;
    protected int hechoAsociado;
    protected String justificacion;

    public abstract void aceptarSolicitud();
    public abstract void rechazarSolicitud();

    /*public void evaluarSpam(){
        if(DetectorDeSpam.esSpam(this.justificacion)){
            this.rechazarSolicitud();
        }
    }*/

    public void setHechoAsociado(int idHechoAsociado) {
        this.hechoAsociado = idHechoAsociado;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public void setId(String justificacion) {
        this.justificacion = justificacion;
    }

    public int getHechoAsociado() {
        return hechoAsociado;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public int getId() {return id;}
}
