package handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class GetEstadisticasHandler implements Handler {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final int puertoEstadisticas;

    public GetEstadisticasHandler(String puertoEstadisticas) {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.puertoEstadisticas = Integer.parseInt(puertoEstadisticas);
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            Map<String, Object> statsGenerales = hacerConsulta("/api/estadisticas/categoriaMax");
            Map<String, Object> statsUsuarios = hacerConsulta("/api/estadisticas/solicitudesSpam");

            List<String> categoriasComunes = Arrays.asList(
                    "Asesinato", "Choque", "Inundacion", "Incendio", "Terremoto"
            );

            List<Map<String, Object>> statsCategoria = new ArrayList<>();
            for (String categoria : categoriasComunes) {
                try {
                    Map<String, Object> provincia = hacerConsulta(
                            "/api/estadisticas/provinciaMax/categorias/" + categoria
                    );
                    Map<String, Object> hora = hacerConsulta(
                            "/api/estadisticas/horaMax/categorias/" + categoria
                    );

                    Map<String, Object> categoriaData = new HashMap<>();
                    categoriaData.put("nombre", categoria);
                    categoriaData.put("provincia", provincia.getOrDefault("provincia",
                            provincia.getOrDefault("estadisticasCategoria_provincia", "N/A")));
                    categoriaData.put("hora", hora.getOrDefault("hora",
                            hora.getOrDefault("estadisticasCategoria_hora", "N/A")));

                    statsCategoria.add(categoriaData);
                } catch (Exception e) {
                    // Si falla una categoría, continuar con las demás
                    System.err.println("Error consultando categoría " + categoria + ": " + e.getMessage());
                }
            }

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("categoriaMax", statsGenerales.getOrDefault("categoria",
                    statsGenerales.getOrDefault("estadisticas_categoria_max_hechos", "N/A")));
            modelo.put("solicitudesSpam", statsUsuarios.getOrDefault("spam",
                    statsUsuarios.getOrDefault("estadisticas_spam", 0)));
            modelo.put("categorias", statsCategoria);
            modelo.put("baseUrl", "http://localhost:" + puertoEstadisticas);

            if(!ctx.sessionAttributeMap().isEmpty()){
                String username = ctx.sessionAttribute("username");
                System.out.println("Usuario: " + username);
                String access_token = ctx.sessionAttribute("access_token");
                modelo.put("username", username);
                modelo.put("access_token", access_token);
            }

            ctx.render("estadisticas.ftl", modelo);

        } catch (Exception e) {
            ctx.status(500).json(Map.of(
                    "error", "Error al obtener estadísticas",
                    "detalle", e.getMessage()
            ));
        }
    }

    private Map<String, Object> hacerConsulta(String endpoint) throws Exception {
        URI uri = new URI("http://localhost:" + puertoEstadisticas + endpoint);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
    }
}
