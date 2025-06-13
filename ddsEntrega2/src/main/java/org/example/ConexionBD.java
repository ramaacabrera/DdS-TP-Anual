package org.example;

import java.util.ArrayList;
import java.util.List;

public class ConexionBD implements Conexion {
    @Override
    public List<Hecho> obtenerHechos(){
        return new ArrayList<>();
    }
}