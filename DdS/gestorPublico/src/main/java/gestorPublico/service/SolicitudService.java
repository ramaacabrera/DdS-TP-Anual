package gestorPublico.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SolicitudService {
    private final String urlDinamica;
    private final HttpClient httpClient;

    public SolicitudService(String urlDinamica) {
        this.urlDinamica = urlDinamica;
        this.httpClient = HttpClient.newHttpClient();
    }

    public HttpResponse<String> enviarSolicitudEliminacion(String bodyJson) {
        try {
            System.out.println("llegue al try del service de la api publica");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlDinamica + "/solicitudesEliminacion"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("llegue al catch del service de la api publica");
            throw new RuntimeException("Error comunicando con servicio dinámico: " + e.getMessage(), e);
        }
    }

    public HttpResponse<String> enviarSolicitudModificacion(String bodyJson) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlDinamica + "/solicitudesModificacion"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Error comunicando con servicio dinámico: " + e.getMessage(), e);
        }
    }
}