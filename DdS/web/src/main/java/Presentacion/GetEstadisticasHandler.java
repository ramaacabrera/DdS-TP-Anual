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
    private final String baseUrl;

    public GetEstadisticasHandler(String puertoEstadisticas) {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.puertoEstadisticas = Integer.parseInt(puertoEstadisticas);
        this.baseUrl = "http://localhost:"+ puertoEstadisticas;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            String uuidColeccion = ctx.queryParam("uuid");

            // Obtener estadísticas generales
            Map<String, Object> statsGenerales = hacerConsulta("/api/estadisticas/categoriaMax");
            Map<String, Object> statsUsuarios = hacerConsulta("/api/estadisticas/solicitudesSpam");

            List<String> categoriasTotales = this.obtenerTodasLasCategorias();

            // Procesar categorías en paralelo
            List<Map<String, Object>> statsCategoria = procesarCategorias(categoriasTotales);

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("categoriaMax", obtenerValorConFallback(statsGenerales,
                    Arrays.asList("categoria", "estadisticas_categoria_max_hechos"), "N/A"));
            modelo.put("solicitudesSpam", obtenerValorConFallback(statsUsuarios,
                    Arrays.asList("spam", "spamCount", "estadisticas_spam"), 0));
            modelo.put("categorias", statsCategoria);
            modelo.put("baseUrl", baseUrl);
            modelo.put("totalCategorias", categoriasTotales.size());

            // ... resto del código para manejar UUID de colección
            if (uuidColeccion != null && !uuidColeccion.trim().isEmpty() && esUUIDValido(uuidColeccion)) {
                try {
                    Map<String, Object> statsColeccion = hacerConsulta(
                            "/api/estadisticas/provinciaMax/colecciones/" + uuidColeccion.trim()
                    );
                    modelo.put("uuidColeccion", uuidColeccion.trim());
                    modelo.put("statsColeccion", statsColeccion);
                    if (statsColeccion.containsKey("nombre")) {
                        modelo.put("nombreColeccion", statsColeccion.get("nombre"));
                    }
                } catch (Exception e) {
                    System.err.println("Error obteniendo stats de colección " + uuidColeccion + ": " + e.getMessage());
                    modelo.put("uuidColeccion", uuidColeccion.trim());
                }
            }

            if(!ctx.sessionAttributeMap().isEmpty()){
                String username = ctx.sessionAttribute("username");
                System.out.println("Usuario: " + username);
                String access_token = ctx.sessionAttribute("access_token");
                modelo.put("username", username);
                modelo.put("access_token", access_token);
            }

            ctx.render("estadisticas.ftl", modelo);

        } catch (Exception e) {
            System.err.println("Error general en GetEstadisticasHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).json(Map.of(
                    "error", "Error al obtener estadísticas",
                    "detalle", e.getMessage()
            ));
        }
    }

    private List<String> obtenerTodasLasCategorias(){
        try {
            URI uri = new URI(baseUrl + "/api/estadisticas/categorias");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP " + response.statusCode() + " para /api/estadisticas/categorias");
            }

            Map<String, Object> respuesta = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});

            if (respuesta.containsKey("categorias")) {
                @SuppressWarnings("unchecked")
                List<String> categorias = (List<String>) respuesta.get("categorias");
                return categorias;
            } else {
                throw new RuntimeException("Formato de respuesta inesperado para categorías");
            }

        } catch (Exception e) {
            System.err.println("Error obteniendo categorías: " + e.getMessage());
            System.out.println("Usando categorías por defecto como fallback");
            return Arrays.asList("Incendio", "Accidente", "Inundación");
        }
    }

    private List<Map<String, Object>> procesarCategorias(List<String> categorias) {
        List<Map<String, Object>> resultados = new ArrayList<>();

        for (String categoria : categorias) {
            try {
                Map<String, Object> provincia = hacerConsulta(
                        "/api/estadisticas/provinciaMax/categorias/" + encodeURL(categoria)
                );
                Map<String, Object> hora = hacerConsulta(
                        "/api/estadisticas/horaMax/categorias/" + encodeURL(categoria)
                );

                Map<String, Object> categoriaData = new HashMap<>();
                categoriaData.put("nombre", categoria);
                categoriaData.put("provincia", obtenerValorConFallback(provincia,
                        Arrays.asList("provincia", "estadisticasCategoria_provincia"), "N/A"));
                categoriaData.put("hora", obtenerValorConFallback(hora,
                        Arrays.asList("hora", "estadisticasCategoria_hora"), "N/A"));

                resultados.add(categoriaData);
            } catch (Exception e) {
                System.err.println("Error consultando categoría " + categoria + ": " + e.getMessage());

                // Agregar categoría con datos por defecto en caso de error
                Map<String, Object> categoriaData = new HashMap<>();
                categoriaData.put("nombre", categoria);
                categoriaData.put("provincia", "Error");
                categoriaData.put("hora", "Error");
                resultados.add(categoriaData);
            }
        }
        return resultados;
    }

    private Map<String, Object> hacerConsulta(String endpoint) throws Exception {
        URI uri = new URI(baseUrl + endpoint);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .timeout(java.time.Duration.ofSeconds(10)) // Timeout para evitar bloqueos
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP " + response.statusCode() + " para " + endpoint);
        }

        return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
    }

    private Object obtenerValorConFallback(Map<String, Object> map, List<String> keys, Object defaultValue) {
        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return defaultValue;
    }

    private boolean esUUIDValido(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(uuid.trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String encodeURL(String text) {
        return java.net.URLEncoder.encode(text, java.nio.charset.StandardCharsets.UTF_8);
    }
}