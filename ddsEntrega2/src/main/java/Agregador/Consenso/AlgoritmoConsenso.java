package Agregador.Consenso;

import Agregador.HechosYColecciones.Coleccion;
import Agregador.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
