package web.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.rendering.template.TemplateUtil;
import org.jetbrains.annotations.NotNull;
import web.domain.hechosycolecciones.Hecho;
import web.service.HechoService;


import java.util.Map;

public class GetHechoEspecificoHandler implements Handler {

    private ObjectMapper mapper = new ObjectMapper();
    private final String urlPublica;
    private final HechoService hechoService;

    public GetHechoEspecificoHandler(String url,  HechoService hechoService) {
        this.hechoService = hechoService;
        this.urlPublica = url;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String hechoIdString = ctx.pathParam("id");

        if (hechoIdString == null || hechoIdString.trim().isEmpty()) {
            ctx.status(400).result("Error 400: ID de hecho no proporcionado.");
            return;
        }

        Hecho hecho = hechoService.obtenerHechoPorId(hechoIdString);

        Map<String, Object> modelo = TemplateUtil.model("hecho", hecho);

        if(!ctx.sessionAttributeMap().isEmpty()){
            String username = ctx.sessionAttribute("username");
            System.out.println("Usuario: " + username);
            String access_token = ctx.sessionAttribute("access_token");
            modelo.put("username", username);
            modelo.put("access_token", access_token);
        }

        ctx.render("hecho-especifico.ftl", modelo);
    }
}