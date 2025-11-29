package cargadorEstatico.domain.consenso;

import cargadorEstatico.domain.hechosycolecciones.Coleccion;
import cargadorEstatico.domain.hechosycolecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
