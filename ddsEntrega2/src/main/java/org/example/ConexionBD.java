package org.example;

import java.util.ArrayList;
import java.util.List;

public class ConexionBD extends Conexion {

    private static ConexionBD instance = null;

    private ConexionBD() {}

    public static ConexionBD GetInstance()
    {
        if (instance == null)
            instance = new ConexionBD();
        return instance;
    }

    public List<Hecho> obtenerHechos() {
        // implementar
        return new ArrayList<Hecho>();
    }
}
