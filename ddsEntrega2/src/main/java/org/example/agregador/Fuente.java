package org.example.agregador;
import java.util.List;


public abstract class Fuente {
    private TipoDeFuente tipoDeFuente;

    public List<Hecho> obtenerHechos() {
        return conexion.obtenerHechos();
    }
}
