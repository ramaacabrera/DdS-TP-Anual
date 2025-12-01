package web.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import web.domain.HechosYColecciones.Coleccion;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import web.service.ColeccionService;

import java.util.HashMap;
import java.util.Map;

public class GetColeccionHandler implements Handler {

    private final String urlPublica;
    private final ColeccionService coleccionService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetColeccionHandler(String urlPublica, ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
        this.urlPublica = urlPublica;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            String coleccionId = ctx.pathParam("id");
            System.out.println("Mostrando detalles para colección ID: " + coleccionId);

            Coleccion coleccion;
            try{
                coleccion = coleccionService.obtenerColeccionPorId(coleccionId);
            }
            catch (Exception e){
                throw new RuntimeException("Error al obtener coleccion por id");
            }

            // 3️Armamos el modelo para FreeMarker
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Detalle de Colección");
            modelo.put("coleccion", coleccion);
            modelo.put("urlAdmin", urlPublica);
            modelo.put("coleccionId", coleccionId);

            if(!ctx.sessionAttributeMap().isEmpty()){
                String username = ctx.sessionAttribute("username");
                System.out.println("Usuario: " + username);
                String access_token = ctx.sessionAttribute("access_token");
                modelo.put("username", username);
                modelo.put("access_token", access_token);
            }

            // 4 Renderizamos la vista
            ctx.render("coleccion.ftl", modelo);

        } catch (Exception e) {
            System.err.println("ERROR en GetColeccionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar la colección: " + e.getMessage());
        }
    }
}
