package org.example.fuenteEstatica;

import org.example.fuente.*;
import java.util.ArrayList;
import java.util.List;

public class ConexionEstatica extends Conexion{
    private final String path;

    public ConexionEstatica(String path) {
        this.path = path;
    }

    @Override
    public List<HechoDTO> obtenerHechos() {
        ConversorCSV conversor = new ConversorCSV();
        CSVLoader csv = new CSVLoader();
        List<HechoDTO> hechos = new ArrayList<>();

        List<String> lineas = csv.leerCSV(path);
        for (String linea : lineas) {
            HechoDTO hecho = conversor.mapearAHecho(linea);
            hechos.add(hecho);
        }
        return hechos;
    }


}
