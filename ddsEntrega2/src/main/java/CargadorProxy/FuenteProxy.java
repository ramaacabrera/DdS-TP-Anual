package CargadorProxy;

import Agregador.fuente.TipoDeFuente;
import Agregador.fuente.Fuente;

public abstract class FuenteProxy extends Fuente {
    public FuenteProxy(String url) { super(TipoDeFuente.PROXY, url); }
}
