package DominioGestorAdministrativo.Consenso;

import DominioGestorAdministrativo.HechosYColecciones.Coleccion;
import DominioGestorAdministrativo.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
