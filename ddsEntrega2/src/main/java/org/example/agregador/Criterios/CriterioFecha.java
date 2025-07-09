package org.example.agregador.Criterios;

import org.example.agregador.HechosYColecciones.Hecho;

import java.util.Date;

public class CriterioFecha extends Criterio {
    private final Date fechaInicio;
    private final Date fechaFin;
    private final String tipoFecha;

    public CriterioFecha(Date fechaInicioNuevo, Date fechaFinNuevo, String tipoNuevo) {
        fechaInicio = fechaInicioNuevo;
        fechaFin = fechaFinNuevo;
        tipoFecha = tipoNuevo;
    }

    @Override

    public boolean cumpleConCriterio(Hecho hecho) {
        Date fecha = tipoFecha.equals("fechaDeCarga") ? hecho.getFechaDeCarga() : hecho.getFechaDeAcontecimiento();
        return (fechaInicio == null || !fecha.before(fechaInicio)) &&
                (fechaFin == null || !fecha.after(fechaFin));
    }
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
