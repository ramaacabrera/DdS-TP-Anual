package agregador.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ServicioGeoref {

    private static final String API_URL = "https://apis.datos.gob.ar/georef/api/v2.0/ubicacion";
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public ServicioGeoref(HttpClient client, ObjectMapper mapper) {
        if(client == null || mapper == null) {
            this.httpClient = HttpClient.newHttpClient();
            this.mapper = new ObjectMapper();
        } else {
            this.httpClient = client;
            this.mapper = mapper;
        }
    }

    public String obtenerDescripcionPorCoordenadas(double latitud, double longitud) {
        try {
            if (latitud == 0 && longitud == 0) return null;

            String url = String.format("%s?lat=%s&lon=%s&aplanar=true&campos=estandar",
                    API_URL, latitud, longitud);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("accept", "application/json")
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parsearRespuesta(response.body());
            } else {
                System.err.println("⚠ Error API Georef: Status " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("⚠ Error conectando a Georef: " + e.getMessage());
        }
        return null;
    }

    private String parsearRespuesta(String jsonResponse) {
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode ubicacion = root.path("ubicacion");

            if (ubicacion.isMissingNode()) return null;

            String localidad = "";
            if (ubicacion.has("municipio_nombre") && !ubicacion.path("municipio_nombre").isNull()) {
                localidad = ubicacion.path("municipio_nombre").asText();
            } else if (ubicacion.has("departamento_nombre")) {
                localidad = ubicacion.path("departamento_nombre").asText();
            }

            String provincia = ubicacion.path("provincia_nombre").asText();

            if (!localidad.isEmpty() && !provincia.isEmpty()) {
                return localidad + ", " + provincia;
            } else {
                return provincia;
            }

        } catch (Exception e) {
            return null;
        }
    }
}
