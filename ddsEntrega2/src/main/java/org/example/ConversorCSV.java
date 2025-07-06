package org.example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversorCSV {
    public Hecho mapearAHecho(String hechoCSV){
        String[] datos = hechoCSV.split(",");
        String titulo = datos[0].trim();
        String descripcion = datos[1].trim();
        String categoria = datos[2].trim();
        Integer latitud = Integer.parseInt(datos[3].trim());
        Integer longitud = Integer.parseInt(datos[4].trim());
        Ubicacion ubicacion = new Ubicacion(latitud, longitud);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaDeAcontecimiento = new Date();
        try {
            fechaDeAcontecimiento = sdf.parse(datos[5].trim());
        } catch (Exception e) {
            System.out.println("Error al convertir fecha" + e.getMessage());
        }
        Fuente fuente;
        EstadoHecho estadoHecho;
        Contribuyente contribuyente;
        List<String> etiquetas = new ArrayList<>();
        boolean esEditable = false;
        List<ContenidoMultimedia> contenidoMultimedia = new ArrayList<>();
        return new Hecho(titulo, descripcion, categoria, ubicacion, fechaDeAcontecimiento, fuente, estadoHecho,contribuyente,etiquetas,esEditable,contenidoMultimedia);
    }
}
