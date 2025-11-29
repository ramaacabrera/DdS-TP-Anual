package cargadorDinamico.domain.Consenso;

import cargadorDinamico.domain.HechosYColecciones.Coleccion;
import cargadorDinamico.domain.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
