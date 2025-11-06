package DominioGestorPublico.Consenso;

import DominioGestorPublico.HechosYColecciones.Coleccion;
import DominioGestorPublico.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
