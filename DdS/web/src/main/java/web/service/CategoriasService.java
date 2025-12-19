package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import web.service.Normalizador.DesnormalizadorCategorias;
import web.service.Normalizador.NormalizadorCategorias;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CategoriasService {
    // CAMBIO: Usamos OkHttpClient en lugar de java.net.http.HttpClient
    private final OkHttpClient httpClient;
    private final ObjectMapper mapper;
    private final String urlEstadisticas;

    public CategoriasService(String urlEstadisticas) {
        // CAMBIO: Inicialización de OkHttpClient con un timeout prudente (similar al código original)
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        this.mapper = new ObjectMapper();
        this.urlEstadisticas = urlEstadisticas;
    }

    public List<String> obtenerCategorias() {
        try {
            String baseUrl = this.urlEstadisticas.trim();

            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }

            URI uri = new URI(baseUrl + "/categorias");

            System.out.println("🧐 Consultando Categorías: '" + uri.toString() + "'");

            // CAMBIO: Construcción del Request estilo OkHttp
            Request request = new Request.Builder()
                    .url(uri.toString()) // OkHttp acepta String o HttpUrl
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .get()
                    .build();

            // CAMBIO: Ejecución con try-with-resources para cerrar el body automáticamente
            try (Response response = httpClient.newCall(request).execute()) {

                // Leemos el body una sola vez (es un stream)
                String responseBody = response.body() != null ? response.body().string() : "";

                if (response.code() != 200) {
                    System.err.println("❌ Error HTTP " + response.code() + " Body: " + responseBody);
                    throw new RuntimeException("HTTP " + response.code() + " consultando: " + uri);
                }

                Map<String, Object> respuesta = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});

                if (respuesta.containsKey("categorias")) {
                    @SuppressWarnings("unchecked")
                    List<String> categorias = (List<String>) respuesta.get("categorias");
                    return categorias;
                } else {
                    return new ArrayList<>();
                }
            }

        } catch (Exception e) {
            System.err.println("Error crítico obteniendo categorías: " + e.getMessage());
            // e.printStackTrace();
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
            System.err.println("Error procesando categoría '" + categoria + "': " + e.getMessage());
            categoriaData.put("provincia", "N/A");
            categoriaData.put("hora", "N/A");
        }
        return categoriaData;
    }

    private Map<String, Object> hacerConsulta(String endpoint) throws Exception {
        // Mantenemos la lógica de URI original para concatenar
        URI uri = new URI(urlEstadisticas + endpoint);

        // CAMBIO: Request Builder de OkHttp
        Request request = new Request.Builder()
                .url(uri.toString())
                .get()
                .build();

        // CAMBIO: Ejecución con OkHttp
        try (Response response = httpClient.newCall(request).execute()) {

            String responseBody = response.body() != null ? response.body().string() : "";

            if (response.code() != 200) {
                throw new RuntimeException("HTTP " + response.code() + " para " + uri);
            }

            return mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
        }
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
            return hacerConsulta("/provinciaMax/categorias/" + encodeURL(catNormalizada));
        } catch (Exception e) {
            System.err.println("Error buscando provincia (posible 404): " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    public Map<String, Object> buscarHoraPorCategoria(String categoria) {
        try {
            String catNormalizada = NormalizadorCategorias.normalizar(categoria);
            return hacerConsulta("/horaMax/categorias/" + encodeURL(catNormalizada));
        } catch (Exception e) {
            System.err.println("Error buscando hora (posible 404): " + e.getMessage());
            return Collections.emptyMap();
        }
    }
}