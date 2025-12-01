package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class EstadisticasService {
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String urlEstadisticas;

    public EstadisticasService(String urlEstadisticas) {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.urlEstadisticas = urlEstadisticas;
    }


    public Map<String, Object> obtenerEstadisticasGenerales() {
        try{
            return this.hacerConsulta(urlEstadisticas + "/api/estadisticas/categoriaMax");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> obtenerEstadisticasUsuarios() {
        try{
            return this.hacerConsulta(urlEstadisticas + "/api/estadisticas/solicitudesSpam");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> obtenerEstadisticasColeccion(String uuidColeccion) {
        try{
            return this.hacerConsulta(urlEstadisticas + "/api/estadisticas/coleccion/" + uuidColeccion);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, Object> hacerConsulta(String endpoint) throws Exception {
        try{
            URI uri = new URI(urlEstadisticas + endpoint);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(10)) // Timeout para evitar bloqueos
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP " + response.statusCode() + " para " + endpoint);
            }

            return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {
            });
        }catch(Exception e){
            throw new Exception("Error al obtener al consultar servicio de estadisticas: " + endpoint, e);
        }
    }
}
