package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EstadisticasService {
    // CAMBIO: Reemplazo de java.net.http.HttpClient por OkHttpClient
    private final OkHttpClient httpClient;
    private final ObjectMapper mapper;
    private final String urlEstadisticas;

    public EstadisticasService(String urlEstadisticas) {
        // CAMBIO: Inicialización de OkHttpClient con timeouts
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS) // Un poco más largo para el CSV
                .build();
        this.mapper = new ObjectMapper();
        this.urlEstadisticas = urlEstadisticas;
    }

    public Map<String, Object> obtenerEstadisticasGenerales() {
        try {
            return this.hacerConsulta("/categoriaMax");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> obtenerEstadisticasUsuarios() {
        try {
            return this.hacerConsulta("/solicitudesSpam");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> obtenerEstadisticasColeccion(String uuidColeccion) {
        try {
            return this.hacerConsulta("/provinciaMax/colecciones/" + uuidColeccion);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public InputStream descargarReporteCSV() throws Exception {
        String endpoint = "/exportar";
        try {
            // Mantenemos tu lógica de URI para evitar problemas de formato
            URI uri = new URI(urlEstadisticas + endpoint);

            Request request = new Request.Builder()
                    .url(uri.toString())
                    .get()
                    .build();

            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                response.close(); // Cerramos manualmente si falló para liberar recursos
                throw new RuntimeException("Error descargando CSV. Status: " + response.code());
            }

            return response.body().byteStream();

        } catch (Exception e) {
            throw new Exception("Error al conectar con el servicio de descarga de estadísticas", e);
        }
    }

    private Map<String, Object> hacerConsulta(String path) throws Exception {
        try {
            // --- Lógica de Limpieza y Sanitización Original ---
            String baseUrl = urlEstadisticas.trim();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }

            String cleanPath = path.trim();
            if (!cleanPath.startsWith("/")) {
                cleanPath = "/" + cleanPath;
            }

            URI uri = new URI(baseUrl + cleanPath);
            // Esto convierte 'Inundación' a 'Inundaci%C3%B3n' y evita el error 400 de Jetty
            uri = URI.create(uri.toASCIIString());

            System.out.println("🚀 Consultando (Sanitized): " + uri.toString());

            // --- Petición con OkHttp ---
            Request request = new Request.Builder()
                    .url(uri.toString())
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .get()
                    .build();

            // Usamos try-with-resources para cerrar automáticamente la respuesta
            try (Response response = httpClient.newCall(request).execute()) {

                String responseBody = response.body() != null ? response.body().string() : "";

                if (!response.isSuccessful()) {
                    // El log del error con el cuerpo es vital
                    throw new RuntimeException("Error HTTP " + response.code() + " URL: " + uri + "\nBODY: " + responseBody);
                }

                return mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
            }

        } catch (Exception e) {
            System.err.println("❌ Fallo crítico consultando: " + path);
            e.printStackTrace();
            throw e;
        }
    }
}