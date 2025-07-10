package org.example.fuenteEstatica;

import org.example.agregador.fuente.TipoDeFuente;
import org.example.agregador.fuente.*;


public class FuenteEstatica extends Fuente {

    public FuenteEstatica() {
        super();
    }

    public FuenteEstatica(Conexion conexion) {
        super(TipoDeFuente.ESTATICA, conexion);
    }

}
