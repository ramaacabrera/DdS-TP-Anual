package gestorPublico.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SolicitudService {
    private final int puertoDinamica;
    private final HttpClient httpClient;

    public SolicitudService(int puertoDinamica) {
        this.puertoDinamica = puertoDinamica;
        this.httpClient = HttpClient.newHttpClient();
    }

    public HttpResponse<String> enviarSolicitudEliminacion(String bodyJson) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:" + puertoDinamica + "/solicitudesEliminacion"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Error comunicando con servicio dinámico: " + e.getMessage(), e);
        }
    }

    public HttpResponse<String> enviarSolicitudModificacion(String bodyJson) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:" + puertoDinamica + "/solicitudesModificacion"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Error comunicando con servicio dinámico: " + e.getMessage(), e);
        }
    }
}