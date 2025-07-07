package org.example.fuenteEstatica;

import org.example.agregador.TipoDeFuente;
import org.example.fuente.*;
import org.example.fuente.Conexion;


public class FuenteEstatica extends Fuente {

    public FuenteEstatica(Conexion conexion) {
        super(TipoDeFuente.ESTATICA, conexion);
    }

}
