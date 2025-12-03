package cargadorEstatico.domain;

import cargadorEstatico.domain.HechosYColecciones.Etiqueta;
import cargadorEstatico.dto.Hechos.EstadoHechoDTO;
import cargadorEstatico.dto.Hechos.EtiquetaDTO;
import cargadorEstatico.dto.Hechos.UbicacionDTO;
import org.apache.commons.csv.CSVRecord;
import cargadorEstatico.dto.Hechos.HechoDTO;
import cargadorEstatico.domain.HechosYColecciones.EstadoHecho;
import cargadorEstatico.domain.HechosYColecciones.Ubicacion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversorCSV {

    public HechoDTO mapearAHecho(CSVRecord record){
        String titulo = record.get("Título");
        String descripcion = record.get("Descripción");
        String categoria = record.get("Categoría");
        double latitud = Double.parseDouble(record.get("Latitud"));
        double longitud = Double.parseDouble(record.get("Longitud"));
        Ubicacion ubicacion = new Ubicacion(latitud, longitud,"");
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaDeAcontecimiento = null;
        try{
            fechaDeAcontecimiento = formato.parse(record.get("Fecha del hecho"));
        } catch (Exception e) {
            System.out.println("Error al mapear fecha" + e.getMessage());
        }
        Date fechaDeCarga = new Date();
        EstadoHechoDTO estadoHecho = EstadoHechoDTO.ACTIVO;
        List<EtiquetaDTO> etiquetas = new ArrayList<>();
        boolean esEditable = false;
        return new HechoDTO(titulo, descripcion, categoria, new UbicacionDTO(ubicacion.getId_ubicacion(),ubicacion.getLatitud(),ubicacion.getLongitud(), ""),
                fechaDeAcontecimiento, fechaDeCarga, null, estadoHecho, null, etiquetas, esEditable, null);
    }

}

