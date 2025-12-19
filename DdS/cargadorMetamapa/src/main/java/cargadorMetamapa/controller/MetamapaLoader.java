package cargadorMetamapa.controller;

import utils.Conexiones.FuenteExternaConexion;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.DTO.HechoDTO;

import java.io.InputStream;
//import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

public class MetamapaLoader implements FuenteExternaConexion {
    protected final String url;
    public MetamapaLoader(String url) {this.url = url;}

    @Override
    public List<HechoDTO> obtenerHechos() {
        URL urlHecho;
        try {
            urlHecho = new URL(url + "/hecho");
            return this.conseguirHechos(urlHecho);
        } catch (Exception e) {
            System.out.println("Error al crear url " + e.getMessage());
        }
        return null;
    }

    public List<HechoDTO> conseguirHechos(URL urlHecho){
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) urlHecho.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "application/json");

            InputStream input = urlConnection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(input, new TypeReference<List<HechoDTO>>() {
            });
        } catch (Exception e){
            System.out.println("Error al conseguir hechos " + e.getMessage());
            return null;
        }
    }

}
