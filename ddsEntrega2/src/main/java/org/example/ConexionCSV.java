package org.example;

import java.util.ArrayList;
import java.util.List;

public class ConexionCSV extends Conexion {

    private static ConexionCSV instance = null;

    private ConexionCSV() {}

    public static ConexionCSV GetInstance()
    {
        if (instance == null)
            instance = new ConexionCSV();
        return instance;
    }

    public List<Hecho> obtenerHechos() {
        // implementar
        return new ArrayList<Hecho>();
    }
}
