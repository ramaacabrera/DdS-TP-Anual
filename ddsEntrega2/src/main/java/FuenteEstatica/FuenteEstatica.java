package FuenteEstatica;

import Agregador.fuente.TipoDeFuente;
import Agregador.fuente.*;


public class FuenteEstatica extends Fuente {

    public FuenteEstatica() {
        super();
    }

    public FuenteEstatica(Conexion conexion) {
        super(TipoDeFuente.ESTATICA, conexion);
    }

}
