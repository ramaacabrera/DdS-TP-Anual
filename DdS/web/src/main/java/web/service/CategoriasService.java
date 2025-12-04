package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import web.service.Normalizador.DesnormalizadorCategorias;
import web.service.Normalizador.NormalizadorCategorias;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class CategoriasService {
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String urlEstadisticas;

    public CategoriasService(String urlEstadisticas) {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.urlEstadisticas = urlEstadisticas;
    }

    public List<String> obtenerCategorias(){
        try {
            URI uri = new URI(urlEstadisticas + "/categorias");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                // Logueamos la URL para debug
                throw new RuntimeException("HTTP " + response.statusCode() + " consultando: " + uri);
            }

            Map<String, Object> respuesta = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});

            if (respuesta.containsKey("categorias")) {
                @SuppressWarnings("unchecked")
                List<String> categorias = (List<String>) respuesta.get("categorias");
                return categorias;
            } else {
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.err.println("Error obteniendo categorías: " + e.getMessage());
            System.out.println("Usando fallback de categorías.");
            return Arrays.asList("Incendio", "Accidente", "Inundación");
        }
    }

    public List<Map<String, Object>> procesarCategorias(List<String> categorias) {
        return categorias.parallelStream().map(this::obtenerDetalleCategoria).collect(Collectors.toList());
    }

    private Map<String, Object> obtenerDetalleCategoria(String categoria) {
        Map<String, Object> categoriaData = new HashMap<>();
        categoriaData.put("nombre", categoria);

        try {
            String categoriaNormalizada = NormalizadorCategorias.normalizar(categoria);

            categoriaData.put("nombre", DesnormalizadorCategorias.desnormalizar(categoriaNormalizada));

            Map<String, Object> provincia = hacerConsulta(
                    "/provinciaMax/categorias/" + encodeURL(categoriaNormalizada)
            );

            Map<String, Object> hora = hacerConsulta(
                    "/horaMax/categorias/" + encodeURL(categoriaNormalizada)
            );

            categoriaData.put("provincia", obtenerValorConFallback(provincia,
                    Arrays.asList("provincia", "estadisticasCategoria_provincia"), "N/A"));

            categoriaData.put("hora", obtenerValorConFallback(hora,
                    Arrays.asList("hora", "estadisticasCategoria_hora"), "N/A"));

        } catch (Exception e) {
            System.err.println("❌ Error procesando categoría '" + categoria + "': " + e.getMessage());
            categoriaData.put("provincia", "N/A");
            categoriaData.put("hora", "N/A");
        }
        return categoriaData;
    }

    private Map<String, Object> hacerConsulta(String endpoint) throws Exception {
        URI uri = new URI(urlEstadisticas + endpoint);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .timeout(java.time.Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP " + response.statusCode() + " para " + uri);
        }

        return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
    }

    private Object obtenerValorConFallback(Map<String, Object> map, List<String> keys, Object defaultValue) {
        if (map == null) return defaultValue;
        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return defaultValue;
    }

    private String encodeURL(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

    public Map<String, Object> buscarProvinciaPorCategoria(String categoria) {
        try {
            String catNormalizada = NormalizadorCategorias.normalizar(categoria);
            // Llamamos al microservicio
            return hacerConsulta("/provinciaMax/categorias/" + encodeURL(catNormalizada));
        } catch (Exception e) {
            System.err.println("Error buscando provincia: " + e.getMessage());
            return Map.of("provincia", "N/A");
        }
    }

    public Map<String, Object> buscarHoraPorCategoria(String categoria) {
        try {
            String catNormalizada = NormalizadorCategorias.normalizar(categoria);
            return hacerConsulta("/horaMax/categorias/" + encodeURL(catNormalizada));
        } catch (Exception e) {
            System.err.println("Error buscando hora: " + e.getMessage());
            return Map.of("hora", "N/A");
        }
    }
}