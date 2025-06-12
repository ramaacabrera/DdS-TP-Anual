package org.example;
import java.util.List;


public abstract class Fuente {
    private TipoDeFuente tipoDeFuente;
    private final Conexion conexion;

    public Fuente(Conexion conexion) {
        this.conexion = conexion;
    }

    public Conexion getConexion(){ return conexion; }

    public List<Hecho> obtenerHechos() {
        return conexion.obtenerHechos();
    }
}
