package cargadorEstatico.domain.Consenso;

import cargadorEstatico.domain.HechosYColecciones.Coleccion;
import cargadorEstatico.domain.HechosYColecciones.Hecho;

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
