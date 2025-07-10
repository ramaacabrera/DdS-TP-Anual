package org.example.fuenteEstatica;

import org.example.agregador.DTO.HechoDTO;
import org.example.agregador.Criterios.Criterio;
import org.example.agregador.fuente.*;
import java.util.ArrayList;
import java.util.List;

public class ConexionEstatica extends Conexion{
    private String path;

    public ConexionEstatica(String path) {

        this.path = path;
    }

    public ConexionEstatica() {
    }

    @Override
    public List<HechoDTO> obtenerHechos(List<Criterio> criterios) {
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
