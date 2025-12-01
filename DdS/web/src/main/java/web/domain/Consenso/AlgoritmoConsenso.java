package web.domain.Consenso;

import web.domain.HechosYColecciones.Coleccion;
import web.domain.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
