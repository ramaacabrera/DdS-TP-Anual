package org.example.agregador.fuente;

import org.example.agregador.DTO.HechoDTO;
import org.example.agregador.Criterios.Criterio;

import java.util.List;

public abstract class Conexion {
    public Fuente fuente;

    public abstract List<HechoDTO> obtenerHechos(List<Criterio> criterios);

    public Fuente getFuente() { return fuente; }
    public void setFuente(Fuente fuente) { this.fuente = fuente; }
}
