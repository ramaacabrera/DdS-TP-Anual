package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.HechosYColecciones.Hecho;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HechoService {

    private ObjectMapper mapper = new ObjectMapper();
    private final String urlPublica;


    public  HechoService(String urlPublica) {
        this.urlPublica = urlPublica;
    }

    public Hecho obtenerHechoPorId(String hechoIdString) {
        try{
            HttpClient httpClient = HttpClient.newHttpClient();

            URI uri = null;
            try {
                uri = new URI(urlPublica + "/hechos/" + hechoIdString);
            } catch (URISyntaxException e) {
                System.err.println("URI invalido " + e.getMessage());
                throw new RuntimeException(e);
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), new TypeReference<>() {});

        }catch(Exception e){
            System.err.println("Error al obtener el hecho" + e.getMessage());
            return null;
        }

    }
}
