package web.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import web.dto.Hechos.HechoDTO;
import web.service.HechoService;

import java.util.HashMap;
import java.util.Map;

public class GetHechoEspecificoHandler implements Handler {

    private final HechoService hechoService;

    public GetHechoEspecificoHandler(HechoService hechoService) {
        this.hechoService = hechoService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String hechoIdString = ctx.pathParam("id");

        if (hechoIdString == null || hechoIdString.trim().isEmpty()) {
            ctx.status(400).result("Error 400: ID de hecho no proporcionado.");
            return;
        }

        HechoDTO hecho = hechoService.obtenerHechoPorId(hechoIdString);

        if (hecho == null) {
            ctx.status(404).result("Hecho no encontrado o servicio no disponible.");
            return;
        }

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("hecho", hecho);

        if (!ctx.sessionAttributeMap().isEmpty()) {
            modelo.put("username", ctx.sessionAttribute("username"));
            modelo.put("access_token", ctx.sessionAttribute("access_token"));
        }

        ctx.render("hecho-especifico.ftl", modelo);
    }
}