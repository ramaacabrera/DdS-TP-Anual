package DominioAgregador.Consenso;

import DominioAgregador.HechosYColecciones.Coleccion;
import DominioAgregador.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
