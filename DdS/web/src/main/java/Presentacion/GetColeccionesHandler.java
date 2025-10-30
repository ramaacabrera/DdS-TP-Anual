package Presentacion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetColeccionesHandler implements Handler {

    private final String urlAdmin;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetColeccionesHandler(String urlAdmin) {
        this.urlAdmin = urlAdmin;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            System.out.println("Listando todas las colecciones desde " + urlAdmin);

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlAdmin + "/colecciones"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                ctx.status(response.statusCode())
                        .result("Error al obtener las colecciones desde la API administrativa.");
                return;
            }

            // Parsear JSON → lista de colecciones
            List<Map<String, Object>> colecciones = mapper.readValue(response.body(), new TypeReference<>() {});

            // Armar modelo para FreeMarker
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Colecciones");
            modelo.put("colecciones", colecciones);
            modelo.put("urlAdmin", urlAdmin);

            // Renderizar plantilla
            ctx.render("colecciones.ftl", modelo);

        } catch (Exception e) {
            System.err.println("ERROR en GetColeccionesHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar las colecciones: " + e.getMessage());
        }
    }
}

