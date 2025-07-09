package org.example.agregador.Consenso;

import org.example.agregador.HechosYColecciones.Coleccion;
import org.example.agregador.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
