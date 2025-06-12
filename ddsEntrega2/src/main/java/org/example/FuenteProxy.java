package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;

public class FuenteProxy extends Fuente {

    private Conexion conexion;
    private String url;

    public FuenteProxy(Conexion conexion, String url) {
        this.conexion = conexion;
        this.url = url;
    }

    @Override
    public List<Hecho> obtenerHechos() {
        List<Hecho> hechos = new ArrayList<>();
        Date ultimaConsulta = new Date(); // Fecha de la última consulta

        Map<String, Object> datosHecho = conexion.siguienteHecho(url, ultimaConsulta);

        while (datosHecho != null) {
            Hecho hecho = convertirAHecho(datosHecho);
            hechos.add(hecho);

            //actualizar la fecha de la última consulta
            ultimaConsulta = new Date();

            datosHecho = conexion.siguienteHecho(url, ultimaConsulta);
        }

        return hechos;
    }

    private Hecho convertirAHecho(Map<String, Object> datosHecho) {

        String titulo = (String) datosHecho.get("titulo");
        String descripcion = (String) datosHecho.get("descripcion");
        String categoria = (String) datosHecho.get("categoria");
        Ubicacion ubicacion = (Ubicacion) datosHecho.get("ubicacion");
        Date fechaDeAcontecimiento = (Date) datosHecho.get("fechaDeAcontecimiento");
        Date fechaDeCarga = new Date();
        Fuente fuente = this;
        EstadoHecho estadoHecho = EstadoHecho.ACTIVO;
        boolean esEditable = false;
        return new Hecho(titulo, descripcion, categoria, ubicacion,
                fechaDeAcontecimiento, fechaDeCarga, fuente,
                estadoHecho, null, new ArrayList<>(), esEditable,
                new ArrayList<>());
    }
}

