package Presentacion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class GetColeccionHandler implements Handler {

    private final String urlPublica;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetColeccionHandler(String urlPublica) {
        this.urlPublica = urlPublica;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            String coleccionId = ctx.pathParam("id");
            System.out.println("Mostrando detalles para colección ID: " + coleccionId);

            // 1️Llamada a la API administrativa
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(urlPublica + "/colecciones/" + coleccionId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                ctx.status(response.statusCode())
                        .result("Error al obtener la colección desde la API administrativa." + response.body());
                return;
            }

            // 2️Parseamos JSON a un Map
            Map<String, Object> coleccion = mapper.readValue(response.body(), new TypeReference<>() {});

            // 3️Armamos el modelo para FreeMarker
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Detalle de Colección");
            modelo.put("coleccion", coleccion);
            modelo.put("urlAdmin", urlPublica);
            modelo.put("coleccionId", coleccionId);

            if(!ctx.sessionAttributeMap().isEmpty()){
                String username = ctx.sessionAttribute("username");
                System.out.println("Usuario: " + username);
                String access_token = ctx.sessionAttribute("access_token");
                modelo.put("username", username);
                modelo.put("access_token", access_token);
            }

            // 4 Renderizamos la vista
            ctx.render("coleccion.ftl", modelo);

        } catch (Exception e) {
            System.err.println("ERROR en GetColeccionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar la colección: " + e.getMessage());
        }
    }
}
