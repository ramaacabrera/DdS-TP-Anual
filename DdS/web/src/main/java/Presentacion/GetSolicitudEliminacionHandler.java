package Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GetSolicitudEliminacionHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String hechoId = ctx.pathParam("id");

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("pageTitle", "Solicitar Eliminación");
        modelo.put("hechoId", hechoId);

        ctx.render("solicitud-eliminacion.ftl", modelo);
    }
}