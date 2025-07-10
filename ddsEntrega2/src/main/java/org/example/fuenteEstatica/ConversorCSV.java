package org.example.fuenteEstatica;

import org.example.agregador.DTO.HechoDTO;
import org.example.agregador.*;
import org.example.agregador.HechosYColecciones.ContenidoMultimedia;
import org.example.agregador.HechosYColecciones.EstadoHecho;
import org.example.agregador.HechosYColecciones.Ubicacion;
import org.example.agregador.fuente.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversorCSV {

    public HechoDTO mapearAHecho(String hechoCSV){
        String[] datos = hechoCSV.split(",");
        String titulo = datos[0].trim();
        String descripcion = datos[1].trim();
        String categoria = datos[2].trim();
        int latitud = Integer.parseInt(datos[3].trim());
        int longitud = Integer.parseInt(datos[4].trim());
        Ubicacion ubicacion = new Ubicacion(latitud, longitud);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaDeAcontecimiento = new Date();
        try {
            fechaDeAcontecimiento = sdf.parse(datos[5].trim());
        } catch (Exception e) {
            System.out.println("Error al convertir fecha" + e.getMessage());
        }
        Date fechaDeCarga = new Date();
        EstadoHecho estadoHecho = EstadoHecho.ACTIVO;
        Contribuyente contribuyente = null;
        List<String> etiquetas = new ArrayList<>();
        boolean esEditable = false;
        List<ContenidoMultimedia> contenidoMultimedia = new ArrayList<>();
        return new HechoDTO(titulo, descripcion, categoria, ubicacion, fechaDeAcontecimiento, fechaDeCarga, null, estadoHecho,contribuyente,etiquetas,esEditable,contenidoMultimedia);
    }

}
