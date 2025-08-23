package Agregador.Criterios;

import Agregador.HechosYColecciones.Hecho;

import java.util.Date;

public class CriterioFecha extends Criterio {
    private Date fechaInicio;
    private Date fechaFin;
    private String tipoFecha;

    public CriterioFecha(Date fechaInicioNuevo, Date fechaFinNuevo, String tipoNuevo) {
        fechaInicio = fechaInicioNuevo;
        fechaFin = fechaFinNuevo;
        tipoFecha = tipoNuevo;
    }
    public CriterioFecha(){}
    @Override
    public boolean cumpleConCriterio(Hecho hecho) {
        Date fecha = tipoFecha.equals("fechaDeCarga") ? hecho.getFechaDeCarga() : hecho.getFechaDeAcontecimiento();
        return (fechaInicio == null || !fecha.before(fechaInicio)) &&
                (fechaFin == null || !fecha.after(fechaFin));
    }

    public Date getFechaInicio() { return fechaInicio; }
    public Date getFechaFin() { return fechaFin; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio;  }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin;  }
/// con before y after no se incluye la fecha limite y falla si alguna fecha es NULL

//    public boolean cumpleConCriterio(Hecho hecho){
//        if(tipoFecha.equals("fechaDeCarga")){
//            return  hecho.getFechaDeCarga().after(fechaInicio) && hecho.getFechaDeCarga().before(fechaFin);
//        }
//        if(tipoFecha.equals("fechaDeAcontecimiento")){
//            return hecho.getFechaDeAcontecimiento().after(fechaInicio) && hecho.getFechaDeAcontecimiento().before(fechaFin);
//        }
//        return false;
//    }

}
