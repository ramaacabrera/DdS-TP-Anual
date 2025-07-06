package org.example.fuenteProxy;

import org.example.agregador.Conexion;
import org.example.agregador.Hecho;

import java.util.ArrayList;
import java.util.List;

public class ConexionMetaMapa {
    @Override
    public List<Hecho> obtenerHechos(){
        return new ArrayList<>();
    }
}
