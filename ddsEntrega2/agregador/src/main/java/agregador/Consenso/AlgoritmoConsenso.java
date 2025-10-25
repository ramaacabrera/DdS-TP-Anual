package agregador.Consenso;

import agregador.HechosYColecciones.Coleccion;
import agregador.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
