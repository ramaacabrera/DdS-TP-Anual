package org.example.agregador;

import org.example.fuente.Fuente;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Coleccion {

    private List<Hecho> hechos = new ArrayList<>();
    private String titulo;
    private String descripcion;
    private List<Fuente> fuente;
    private String handle;
    private List<Criterio> criteriosDePertenencia = new ArrayList<>();
    private ModosDeNavegacion modosDeNavegacion;
    private AlgoritmoConsenso algoritmoDeConsenso;


    public Coleccion(String titulo, String descripcion, Fuente fuente, String handle) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fuente = fuente;
        this.handle = handle;
    } //las listas las seteo antes con new


    public void setCriterio(Criterio criterio) {
        criteriosDePertenencia.add(criterio);
    }

    public List<Hecho> getHechos() {
        return hechos;
    }

    public void agregarHecho(Hecho hecho) {
        hechos.add(hecho);
    }

    public List<Hecho> obtenerHechosQueCumplen(Criterio criterio) {
        return hechos.stream()
                .filter(hecho -> criterio.cumpleConCriterio(hecho))
                .collect(Collectors.toList());
    }

    //como tenemos una lista de criterios, no se si refiere a que un hecho tiene q cumplir todos para q sea de esa coleccion
    /* public List<Hecho> obtenerHechosQueCumplenTodosLosCriterios() {
    return hechos.stream()
            .filter(hecho -> this.cumpleTodosLosCriterios(hecho))
            .collect(Collectors.toList());
}


private boolean cumpleTodosLosCriterios(Hecho hecho) {
    return criteriosDePertenencia.stream()
            .allMatch(criterio -> criterio.cumpleConCriterio(hecho));
}   */

    public String getHandle() {
        return handle;
    }
    
}

