package gestorAdministrativo.domain.Consenso;

import gestorAdministrativo.domain.HechosYColecciones.Coleccion;
import gestorAdministrativo.domain.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
