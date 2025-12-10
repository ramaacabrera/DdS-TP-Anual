package estadisticas.domain.Consenso;

import estadisticas.domain.HechosYColecciones.Coleccion;
import estadisticas.domain.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
