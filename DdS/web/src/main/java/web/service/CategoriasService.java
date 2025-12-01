package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import web.domain.Normalizador.DesnormalizadorCategorias;
import web.domain.Normalizador.NormalizadorCategorias;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

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
            URI uri = new URI(urlEstadisticas);

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

    private Map<String, Object> hacerConsulta(String endpoint) throws Exception {
        URI uri = new URI(urlEstadisticas + endpoint);

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

    public List<Map<String, Object>> procesarCategorias(List<String> categorias) {
        List<Map<String, Object>> resultados = new ArrayList<>();

        for (String categoria : categorias) {
            try {
                // NORMALIZAR la categoría antes de hacer las consultas
                String categoriaNormalizada = NormalizadorCategorias.normalizar(categoria);

                Map<String, Object> provincia = hacerConsulta(
                        "/api/estadisticas/provinciaMax/categorias/" + encodeURL(categoriaNormalizada)
                );
                Map<String, Object> hora = hacerConsulta(
                        "/api/estadisticas/horaMax/categorias/" + encodeURL(categoriaNormalizada)
                );

                Map<String, Object> categoriaData = new HashMap<>();
                // Desnormalizar el nombre para mostrar
                categoriaData.put("nombre", DesnormalizadorCategorias.desnormalizar(categoriaNormalizada));
                categoriaData.put("provincia", obtenerValorConFallback(provincia,
                        Arrays.asList("provincia", "estadisticasCategoria_provincia"), "N/A"));
                categoriaData.put("hora", obtenerValorConFallback(hora,
                        Arrays.asList("hora", "estadisticasCategoria_hora"), "N/A"));

                resultados.add(categoriaData);
            } catch (Exception e) {
                System.err.println("❌ Error procesando categoría '" + categoria + "': " + e.getMessage());

                Map<String, Object> categoriaData = new HashMap<>();
                categoriaData.put("nombre", categoria); // Usar el original en caso de error
                categoriaData.put("provincia", "Error");
                categoriaData.put("hora", "Error");
                categoriaData.put("error", e.getMessage());

                resultados.add(categoriaData);
            }
        }
        return resultados;
    }

    private Object obtenerValorConFallback(Map<String, Object> map, List<String> keys, Object defaultValue) {
        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return defaultValue;
    }

    private String encodeURL(String text) {
        return java.net.URLEncoder.encode(text, java.nio.charset.StandardCharsets.UTF_8);
    }
}
