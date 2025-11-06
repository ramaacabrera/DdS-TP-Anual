package cargadorEstatico.controller;

import DominioCargadorEstatico.ConversorCSV;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import utils.DTO.HechoDTO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConexionEstatica {
    private String path;

    public ConexionEstatica(String filename) {
        this.path = filename;
    }


    public List<HechoDTO> obtenerHechos() {
        ConversorCSV conversor = new ConversorCSV();
        List<HechoDTO> hechos = new ArrayList<>();

        try {
            // Obtener el InputStream del recurso desde /resources
            Path ruta = Paths.get(path);

            Reader reader = Files.newBufferedReader(ruta);
            CSVParser parser = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            // Leer los registros
            List<CSVRecord> registros = parser.getRecords();
            for (CSVRecord registro : registros) {
                HechoDTO hechoDTO = conversor.mapearAHecho(registro);
                //hechoDTO.setFuente(path);
                hechos.add(hechoDTO);
            }
            System.out.println("Hechos encontrados: " + hechos.size());
        } catch (Exception e) {
            System.err.println("Error al leer el archivo CSV: " + e.getMessage());
        }
        return hechos;











        /*

        CSVLoader csv = new CSVLoader();
        List<HechoDTO> hechos = new ArrayList<>();



        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename)){

        }
        catch (IOException e){
            e.printStackTrace();
        }



        List<String> lineas = csv.leerCSV(path);
        System.out.printf("Lineas leidas %d", lineas.size());
        lineas.remove(0);
        for (String linea : lineas) {

            HechoDTO hecho = conversor.convertirAHecho(linea);
            //System.out.println(hecho);
            hechos.add(hecho);
        }



        //File csvFile = new File(path);

        CSVParser parser = null;
        HechoDTO hecho;
        try(Reader reader = new FileReader(csvFile);){
            parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : parser) {
                hecho = conversor.mapearAHecho(record);
                hechos.add(hecho);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        */
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
