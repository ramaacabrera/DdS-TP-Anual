package org.example.agregador;

import java.util.Date;

public class CriterioFecha extends Criterio {
    private Date fechaInicio;
    private Date fechaFin;
    private String tipoFecha;

    @Override
    public boolean cumpleConCriterio(Hecho hecho){
        if(tipoFecha.equals("fechaDeCarga")){
            return  hecho.getFechaDeCarga().after(fechaInicio) && hecho.getFechaDeCarga().before(fechaFin);
        }
        if(tipoFecha.equals("fechaDeAcontecimiento")){
            return hecho.getFechaDeAcontecimiento().after(fechaInicio) && hecho.getFechaDeAcontecimiento().before(fechaFin);
        }
        return false;
    }

}
