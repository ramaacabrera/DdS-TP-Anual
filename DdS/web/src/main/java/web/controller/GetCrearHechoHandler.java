package web.controller;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GetCrearHechoHandler implements Handler {

    private final String urlPublica;

    public GetCrearHechoHandler(String urlPublica) {
        this.urlPublica = urlPublica;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Solo renderiza la plantilla con el formulario vacío
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("pageTitle", "Reportar un Hecho");
        modelo.put("urlPublica", urlPublica);
        if(!ctx.sessionAttributeMap().isEmpty()){
            String username = ctx.sessionAttribute("username");
            System.out.println("Usuario: " + username);
            String access_token = ctx.sessionAttribute("access_token");
            modelo.put("username", username);
            modelo.put("access_token", access_token);
        }
        ctx.render("crear-hecho.ftl", modelo);
    }
}