package org.example.fuenteProxy;

import org.example.agregador.TipoDeFuente;
import org.example.fuente.Conexion;
import org.example.fuente.Fuente;
import org.example.fuente.HechoDTO;

import java.util.List;

public class FuenteProxy extends Fuente {
    public FuenteProxy(Conexion conexion) { super(TipoDeFuente.PROXY, conexion); }
}
