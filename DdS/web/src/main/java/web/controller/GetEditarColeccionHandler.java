package web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import web.domain.hechosycolecciones.Coleccion;
import web.domain.hechosycolecciones.TipoAlgoritmoConsenso;
import web.domain.fuente.TipoDeFuente;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import web.service.ColeccionService;

import java.util.HashMap;
import java.util.Map;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class GetEditarColeccionHandler implements Handler {

    private final String urlAdmin;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ColeccionService coleccionService;

    public GetEditarColeccionHandler(String urlAdmin, ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
        this.urlAdmin = urlAdmin;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            String coleccionId = ctx.pathParam("id");
            System.out.println("Abriendo formulario de edición para colección ID: " + coleccionId);

            Coleccion coleccion;
            try{
                coleccion = coleccionService.obtenerColeccionPorId(coleccionId);
            }
            catch (Exception e){
                throw new RuntimeException("Error al obtener coleccion por id");
            }

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
