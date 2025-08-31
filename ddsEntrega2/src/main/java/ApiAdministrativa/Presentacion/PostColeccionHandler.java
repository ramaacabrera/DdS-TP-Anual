package ApiAdministrativa.Presentacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import utils.DTO.ColeccionDTO;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PostColeccionHandler implements Handler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        ColeccionDTO nueva = ctx.bodyAsClass(ColeccionDTO.class);

        // Serializamos la coleccion a JSON
        String coleccionJson = objectMapper.writeValueAsString(nueva);

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/colecciones"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(coleccionJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        ctx.status(response.statusCode()).result(response.body());
    }
}
