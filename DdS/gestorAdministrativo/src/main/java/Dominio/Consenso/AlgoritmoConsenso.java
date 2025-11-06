package Dominio.Consenso;

import Dominio.HechosYColecciones.Coleccion;
import Dominio.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
