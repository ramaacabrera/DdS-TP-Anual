package org.example;

import java.util.ArrayList;
import java.util.List;

public class ConexionMetaMapa implements Conexion {
    @Override
    public List<Hecho> obtenerHechos(){
        return new ArrayList<>();
    }
}
