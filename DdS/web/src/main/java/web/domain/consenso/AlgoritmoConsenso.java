package web.domain.consenso;

import web.domain.hechosycolecciones.Coleccion;
import web.domain.hechosycolecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
