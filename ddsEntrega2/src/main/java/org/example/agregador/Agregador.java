package org.example.agregador;

import org.example.fuente.*;

import java.util.ArrayList;
import java.util.List;

public class Agregador {

    private List<Coleccion> colecciones = new ArrayList<>();

    public List<Hecho> obtenerHechosExterno(Fuente fuente) {
        List<Hecho> hechos = new ArrayList();
        for (HechoDTO hechoDTO : fuente.obtenerHechos()){
            Hecho hecho = new Hecho(hechoDTO);
            hechos.add(hecho);
        }
        return hechos;
    }

    public void ejecutarAlgoritmoDeConsenso() {
        for (Coleccion coleccion : colecciones) {
            coleccion.ejecutarAlgoritmoDeConsenso();
        }
    }

}
