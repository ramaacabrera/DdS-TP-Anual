package gestorPublico.domain.Consenso;

import gestorPublico.domain.HechosYColecciones.Coleccion;
import gestorPublico.domain.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
