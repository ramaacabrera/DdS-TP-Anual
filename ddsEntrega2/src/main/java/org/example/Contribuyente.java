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


}
