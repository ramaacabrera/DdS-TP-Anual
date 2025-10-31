package Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class GetSolicitudEliminacionHandler implements Handler {
    private final String urlAdmin;

    public GetSolicitudEliminacionHandler(String urlAdmin) {
        this.urlAdmin = urlAdmin;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            String hechoId = ctx.pathParam("id");
            System.out.println("Solicitando formulario de eliminación para hecho ID: " + hechoId);

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Solicitar Eliminación");
            modelo.put("hechoId", hechoId);
            modelo.put("urlPublica", urlAdmin);

            if(!ctx.sessionAttributeMap().isEmpty()){
                String username = ctx.sessionAttribute("username");
                System.out.println("Usuario: " + username);
                String access_token = ctx.sessionAttribute("access_token");
                modelo.put("username", username);
                modelo.put("access_token", access_token);
            }

            ctx.render("crear-solicitud-eliminacion.ftl", modelo);

        } catch (Exception e) {
            System.err.println("ERROR en GetSolicitudEliminacionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar el formulario: " + e.getMessage());
        }
    }
}