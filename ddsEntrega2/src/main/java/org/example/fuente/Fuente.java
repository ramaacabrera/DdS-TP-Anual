package org.example.fuente;

import org.example.agregador.TipoDeFuente;

import java.util.List;

public abstract class Fuente {
    protected TipoDeFuente tipoDeFuente;
    protected Conexion conexion;

    public Fuente(TipoDeFuente tipoDeFuente, Conexion conexion) {
        this.tipoDeFuente = tipoDeFuente;
        this.conexion = conexion;
    }

    public List<HechoDTO> obtenerHechos(){
        return conexion.obtenerHechos();
    }
}