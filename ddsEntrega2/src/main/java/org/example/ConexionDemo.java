package org.example;

import java.util.ArrayList;
import java.util.List;

public class ConexionDemo extends Conexion {

    private static ConexionDemo instance = null;

    private ConexionDemo() {}

    public static ConexionDemo GetInstance()
    {
        if (instance == null)
            instance = new ConexionDemo();
        return instance;
    }

    public List<Hecho> obtenerHechos() {
        // implementar
        return new ArrayList<Hecho>();
    }
}

