package org.example.agregador;

import java.util.ArrayList;
import java.util.List;

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
