package org.example;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConexionMetaMapa implements Conexion {

    private URL url;

    @Override
    public List<Hecho> obtenerHechos() {
        List<Hecho> hechos = new ArrayList<>();
        try {
            URL nuevaURL = new URL(url + "/hechos");
            hechos = this.recibirHechos(nuevaURL);
        } catch (MalformedURLException e) {
            System.out.println("URL invalida: " + e.getMessage());
        }
        return hechos;
    }

    public List<Hecho> obtenerHechos (Coleccion coleccion){
        List<Hecho> hechos = new ArrayList<>();
        try {
            URL nuevaURL = new URL(url + "/colecciones/" + coleccion.getHandle() + "/hechos");
            hechos = this.recibirHechos(nuevaURL);
        } catch (MalformedURLException e) {
            System.out.println("URL invalida: " + e.getMessage());
        }
        return hechos;
    }

    public List<Hecho> recibirHechos(URL urlAPI){
        List<Hecho> hechos = new ArrayList<>();
        try {
            HttpURLConnection conexion = (HttpURLConnection) urlAPI.openConnection();
            conexion.setRequestMethod("GET");

            int estado = conexion.getResponseCode();
            if(estado == 200){
                BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String linea;
                StringBuilder contenido = new StringBuilder();
                while ((linea = lector.readLine()) != null){
                    contenido.append(linea);
                }
                lector.close();

                String json = contenido.toString();

                ObjectMapper mapper = new ObjectMapper();

                hechos = mapper.readValue(json, new TypeReference<List<Hecho>>() {});
            }
            else {
                System.out.println("Error HTTP: " + estado);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener hechos: " + e.getMessage());
        }
        return hechos;
    }
}
