package org.example.fuenteProxy;

import org.example.agregador.fuente.TipoDeFuente;
import org.example.agregador.fuente.Fuente;
import org.example.agregador.fuente.Conexion;

public abstract class FuenteProxy extends Fuente {
    public FuenteProxy(Conexion conexion) { super(TipoDeFuente.PROXY, conexion); }
}
