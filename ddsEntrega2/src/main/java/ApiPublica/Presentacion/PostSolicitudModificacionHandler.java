package ApiPublica.Presentacion;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.net.URI;

public class PostSolicitudModificacionHandler implements Handler {
    private final int puertoDinamica;

    public PostSolicitudModificacionHandler(int puertoDinamicaNuevo) {
        puertoDinamica = puertoDinamicaNuevo;
    }

    @Override
    public void handle(Context context) throws Exception {
        String bodyJson = context.body(); // request JSON completo

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + puertoDinamica + "/solicitudesModificacion"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        context.status(response.statusCode()).result(response.body());
    }
}
