package org.example;
import java.util.List;

public class Conexion {
    private final TipoDeConexion tipoDeConexion;

    public Conexion(TipoDeConexion tipoDeConexion) {
        this.tipoDeConexion = tipoDeConexion;
    }

    public List<Hecho> obtenerHechos(){
        return tipoDeConexion.obtenerHechos();
    }
}
