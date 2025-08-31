package Agregador;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.DTO.HechoDTO;
import utils.DTO.SolicitudDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ConexionCargador {
    String urlCompEstatico;
    String urlCompDinamico;
    String urlCompProxy;

    public ConexionCargador(String urlCompEstatico, String urlCompDinamico, String urlCompProxy){
        this.urlCompEstatico = urlCompEstatico;
        this.urlCompDinamico = urlCompDinamico;
        this.urlCompProxy = urlCompProxy;
    }

    public List<HechoDTO> obtenerHechosNuevos(){
        List<HechoDTO> listaHecho = new ArrayList<>();

        listaHecho.addAll(this.buscarHechos(urlCompDinamico));
        listaHecho.addAll(this.buscarHechos(urlCompProxy));
        listaHecho.addAll(this.buscarHechos(urlCompEstatico));
        return listaHecho;
    }

    public List<HechoDTO> buscarHechos(String url){
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        try{
            HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), new TypeReference<List<HechoDTO>>() {});
        }
        catch(Exception e){
            System.err.println("Error al buscar Hecho: " + e.getMessage());
            return null;
        }
    }
}
