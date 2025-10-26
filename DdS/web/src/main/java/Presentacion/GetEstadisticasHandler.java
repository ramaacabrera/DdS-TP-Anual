package Presentacion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.rendering.template.TemplateUtil;
import org.jetbrains.annotations.NotNull;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.Persistencia.HechoRepositorio;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class GetEstadisticasHandler implements Handler {
    private ObjectMapper mapper = new ObjectMapper();
    private final String puertoEstadisticas;
    private final HttpClient httpClient; // ← Reutilizar mismo cliente

    public GetEstadisticasHandler(String puerto) {
        this.puertoEstadisticas = puerto;
        this.httpClient = HttpClient.newHttpClient(); // ← Crear una sola vez
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String categoriaId = ctx.pathParam("categoriaId");
        String coleccionId = ctx.pathParam("coleccionId");

        try {
            // Estadísticas generales
            Map<String, Object> statsGenerales = hacerConsulta("/api/estadisticas/categoriaMax");
            Map<String, Object> statsUsuarios = hacerConsulta("/api/estadisticas/solicitudesSpam");

            // Particulares (si aplica)
//            Map<String, Object> statsColecciones = hacerConsulta("/api/estadisticas/colecciones");
/// //////////////////////////////////////////////////////////////////////



        ///  COMPLETAR ESTO


        ///!!!!

            // Preparar modelo con todas las estadísticas
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("statsGenerales", statsGenerales);
            modelo.put("statsUsuarios", statsUsuarios);
            modelo.put("baseUrl", "http://localhost:" + puertoEstadisticas);

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