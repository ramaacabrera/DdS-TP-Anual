package web.controller;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import web.domain.hechosycolecciones.TipoAlgoritmoConsenso;
import web.domain.fuente.TipoDeFuente;

import java.util.HashMap;
import java.util.Map;

public class GetCrearColeccionHandler implements Handler {
    private final String urlAdmin;

    public GetCrearColeccionHandler(String urlAdmin) {
        this.urlAdmin = urlAdmin;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("pageTitle", "Crear nueva colección");
        modelo.put("urlAdmin", urlAdmin);
        modelo.put("algoritmos", TipoAlgoritmoConsenso.values());
        modelo.put("fuentes", TipoDeFuente.values());

        if(!ctx.sessionAttributeMap().isEmpty()){
            String username = ctx.sessionAttribute("username");
            System.out.println("Usuario: " + username);
            String access_token = ctx.sessionAttribute("access_token");
            modelo.put("username", username);
            modelo.put("access_token", access_token);
        }

        ctx.render("crear-coleccion.ftl", modelo);
    }
}
