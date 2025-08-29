package CargadorProxy;

import Agregador.fuente.TipoDeFuente;
import Agregador.fuente.Fuente;
import Agregador.fuente.Conexion;

public abstract class FuenteProxy extends Fuente {
    public FuenteProxy(Conexion conexion) { super(TipoDeFuente.PROXY, conexion); }
}
