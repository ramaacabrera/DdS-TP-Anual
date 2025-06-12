package org.example;

import java.util.ArrayList;
import java.util.List;

public class ConexionMetaMapa extends Conexion {

    private static ConexionMetaMapa instance = null;

    private ConexionMetaMapa() {}

    public static ConexionMetaMapa GetInstance()
    {
        if (instance == null)
            instance = new ConexionMetaMapa();
        return instance;
    }

    public List<Hecho> obtenerHechos() {
        // implementar
        return new ArrayList<Hecho>();
    }
}
