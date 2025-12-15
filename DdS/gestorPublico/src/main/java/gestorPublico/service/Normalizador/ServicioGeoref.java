package gestorPublico.service.Normalizador;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class ServicioGeoref {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final List<Region> REGIONES = List.of(
            new Region("Uruguay", -35.0, -30.0, -58.5, -53.0),
            new Region("Chile", -56.0, -17.5, -76.0, -69.0),
            new Region("CABA, Buenos Aires", -34.7, -34.5, -58.55, -58.35),
            new Region("Buenos Aires", -41.0, -33.0, -63.4, -56.6),
            new Region("Córdoba", -35.0, -29.5, -65.5, -61.7),
            new Region("Santa Fe", -34.4, -28.0, -63.0, -59.0),
            new Region("Argentina (General)", -55.0, -21.0, -73.5, -53.5)
    );

    public ServicioGeoref() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String enriquecerDescripcion(double latitud, double longitud, String descripcionActual) {
        boolean tieneCoordenadas = latitud != 0 && longitud != 0;
        boolean faltaDescripcion = descripcionActual == null || descripcionActual.trim().isEmpty();

        if (tieneCoordenadas && faltaDescripcion) {
            System.out.println("📍 Buscando descripción para coord: " + latitud + ", " + longitud);

            // Intentar API Georef
            String descripcionApi = consultarApiGeoref(latitud, longitud);
            if (descripcionApi != null) {
                System.out.println("✅ Ubicación actualizada (API): " + descripcionApi);
                return descripcionApi;
            }

            // Fallback Offline
            System.out.println("⚠ API falló, calculando ubicación aproximada offline...");
            String descripcionOffline = consultarOffline(latitud, longitud);
            System.out.println("✅ Ubicación actualizada (Offline): " + descripcionOffline);
            return descripcionOffline;
        }

        return descripcionActual;
    }

    private String consultarApiGeoref(double lat, double lon) {
        try {
            String url = String.format("https://apis.datos.gob.ar/georef/api/v2.0/ubicacion?lat=%s&lon=%s&aplanar=true&campos=estandar", lat, lon);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("accept", "application/json")
                    .GET()
                    .timeout(Duration.ofSeconds(3))
                    .build();

            // Ahora sí funciona 'this.httpClient'
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = this.objectMapper.readTree(response.body());
                JsonNode ubicacion = root.path("ubicacion");

                if (!ubicacion.isMissingNode()) {
                    String provincia = ubicacion.path("provincia_nombre").asText(null);
                    String localidad = ubicacion.path("municipio_nombre").asText(null);
                    if (localidad == null) localidad = ubicacion.path("departamento_nombre").asText(null);

                    if (provincia != null && localidad != null) return localidad + ", " + provincia;
                    if (provincia != null) return provincia;
                }
            }
        } catch (Exception e) {
            System.err.println("⚠ Error conectando a Georef interno: " + e.getMessage());
        }
        return null;
    }

    private String consultarOffline(double lat, double lon) {
        for (Region r : REGIONES) {
            if (lat >= r.minLat && lat <= r.maxLat && lon >= r.minLon && lon <= r.maxLon) {
                return r.nombre + " (Aprox)";
            }
        }
        return "Ubicación desconocida";
    }

    private static class Region {
        String nombre;
        double minLat, maxLat, minLon, maxLon;
        public Region(String n, double minLat, double maxLat, double minLon, double maxLon) {
            this.nombre = n; this.minLat = minLat; this.maxLat = maxLat; this.minLon = minLon; this.maxLon = maxLon;
        }
    }
}