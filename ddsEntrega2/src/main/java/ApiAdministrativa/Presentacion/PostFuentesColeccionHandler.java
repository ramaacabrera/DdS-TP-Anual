package ApiAdministrativa.Presentacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import Agregador.Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import Agregador.HechosYColecciones.Coleccion;
import Agregador.fuente.Fuente;
import org.jetbrains.annotations.NotNull;
import utils.DTO.ColeccionDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class PostFuentesColeccionHandler implements Handler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String handle = ctx.pathParam("id");
        Fuente nueva = ctx.bodyAsClass(Fuente.class);

        // Serializamos la coleccion a JSON
        String fuenteJson = objectMapper.writeValueAsString(nueva);

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/colecciones" + handle + "/fuente"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(fuenteJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        ctx.status(response.statusCode()).result(response.body());
    }
}
