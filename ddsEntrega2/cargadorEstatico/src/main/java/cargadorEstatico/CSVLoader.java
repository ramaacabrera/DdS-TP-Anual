package cargadorEstatico;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {
    public List<String> leerCSV(String fileName) {
        List<String> resultado = new ArrayList<>();
        String linea;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)))) {
            while((linea = br.readLine()) != null){
                resultado.add(linea);
            }
        } catch (Exception e) {
            System.out.println("Error al leer csv: " + e.getMessage());
        }
        return resultado;
    }
}
