package Presentacion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Dominio.HechosYColecciones.TipoAlgoritmoConsenso;
import utils.Dominio.fuente.TipoDeFuente;
import utils.Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.rendering.template.TemplateUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;



public class GetEditarColeccionHandler implements Handler {

    private final String urlAdmin;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetEditarColeccionHandler(String urlAdmin) {
        this.urlAdmin = urlAdmin;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            String coleccionId = ctx.pathParam("id");
            System.out.println("Abriendo formulario de edición para colección ID: " + coleccionId);

            // 1️ Traemos la colección desde la API administrativa
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request;
            if(!ctx.sessionAttributeMap().isEmpty()){
                request = HttpRequest.newBuilder()
                    .uri(new URI(urlAdmin + "/colecciones/" + coleccionId))
                    .header("username", ctx.sessionAttribute("username"))
                    .header("access_token", ctx.sessionAttribute("access_token"))
                    .GET()
                    .build();
            } else{
                request = HttpRequest.newBuilder()
                        .uri(new URI(urlAdmin + "/colecciones/" + coleccionId))
                        .header("username", ctx.sessionAttribute("username"))
                        .header("access_token", ctx.sessionAttribute("access_token"))
                        .GET()
                        .build();
            }

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                ctx.status(response.statusCode())
                        .result("Error al obtener la colección desde la API administrativa.");
                return;
            }

            // 2️ Parseamos la respuesta JSON a un Map (para FreeMarker)
            Map<String, Object> coleccion = mapper.readValue(response.body(), new TypeReference<>() {});

            // Valido que el usuario sea un admin

            if(ctx.sessionAttributeMap().isEmpty()){
                ctx.status(500).result("Administrador no identificado");
            }

            String username = ctx.sessionAttribute("username");
            String access_token = ctx.sessionAttribute("access_token");

            HttpClient cliente = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(urlAdmin + "/colecciones/" + coleccionId))
                    .GET()
                    .build();

            HttpResponse<String> res = cliente.send(req, HttpResponse.BodyHandlers.ofString());


            // 3️ Armamos el modelo para la vista
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Editar colección");
            modelo.put("coleccion", coleccion);
            modelo.put("coleccionId", coleccionId);
            modelo.put("algoritmos", TipoAlgoritmoConsenso.values());
            modelo.put("fuentes", TipoDeFuente.values());
            modelo.put("urlAdmin", urlAdmin);

            modelo.put("username", username);
            modelo.put("access_token", access_token);


            // Renderizamos el template
            ctx.render("editar-coleccion.ftl", modelo);

        } catch (Exception e) {
            System.err.println(" ERROR en GetEditarColeccionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar el formulario: " + e.getMessage());
        }
    }
}



/*Map<String, Object> modelo = new HashMap<>();
        modelo.put("pageTitle", "Editar colección");
        modelo.put("coleccion", coleccion);
        modelo.put("algoritmosDisponibles", TipoAlgoritmoConsenso.values());
        modelo.put("fuentes", TipoDeFuente.values());
        modelo.put("urlAdmin", urlAdmin);
*/
