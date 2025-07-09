package org.example.fuenteEstatica;

import org.example.agregador.DTO.HechoDTO;
import org.example.agregador.Criterios.Criterio;
import org.example.agregador.fuente.*;
import java.util.ArrayList;
import java.util.List;

public class ConexionEstatica extends Conexion{
    private final String path;

    public ConexionEstatica(String path, Fuente fuente) {
        this.path = path;
        this.fuente = fuente;
    }

    @Override
    public List<HechoDTO> obtenerHechos(List<Criterio> criterios) {
        ConversorCSV conversor = new ConversorCSV();
        CSVLoader csv = new CSVLoader();
        List<HechoDTO> hechos = new ArrayList<>();

        List<String> lineas = csv.leerCSV(path);
        for (String linea : lineas) {
            HechoDTO hecho = conversor.mapearAHecho(linea, fuente);
            hechos.add(hecho);
        }
        return hechos;
    }


}
