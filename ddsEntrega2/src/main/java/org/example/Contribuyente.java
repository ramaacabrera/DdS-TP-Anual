package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Contribuyente {
    private List<Hecho> hechosSubidos = new ArrayList<Hecho>();
    private Integer edad;
    private String nombre;
    private String apellido;

    public void VerificarMayoriaDeEdad() {}

    public void hechoSubido(Hecho hecho){
        hechosSubidos.add(hecho);
    }

    public boolean puedeModificarHecho(Hecho hecho){
        Date fechaActual = new Date();
        long diferencia = (fechaActual.getTime() - hecho.getFechaDeCarga().getTime()) / (1000 * 60 * 60 * 24);
        return hecho.getContribuyente().equals(this) &&  diferencia < 7;
    }
}
