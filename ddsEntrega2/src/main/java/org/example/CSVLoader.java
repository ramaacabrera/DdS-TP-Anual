package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {
    public List<String> leerCSV(String path) {
        List<String> resultado = new ArrayList<>();
        String linea;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while((linea = br.readLine()) != null){
                resultado.add(linea);
            }
        } catch (Exception e) {
            System.out.println("Error al leer csv: " + e.getMessage());
        }
        return resultado;
    }
}
