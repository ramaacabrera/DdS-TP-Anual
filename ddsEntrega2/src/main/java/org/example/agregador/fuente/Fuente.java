package org.example.agregador.fuente;

import org.example.agregador.DTO.HechoDTO;
import org.example.agregador.Criterios.Criterio;

import java.util.List;

public abstract class Fuente {
    protected TipoDeFuente tipoDeFuente;
    protected Conexion conexion;

    public Fuente(TipoDeFuente tipoDeFuente, Conexion conexion) {
        this.tipoDeFuente = tipoDeFuente;
        this.conexion = conexion;
    }

    public Conexion getConexion() {
        return conexion;
    }

    public List<HechoDTO> obtenerHechos(List<Criterio> criterios){
        return conexion.obtenerHechos(criterios);
    }
}