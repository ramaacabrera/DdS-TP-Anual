package Presentacion;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GetCrearHechoHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Solo renderiza la plantilla con el formulario vacío
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("pageTitle", "Reportar un Hecho");
        ctx.render("crear-hecho.ftl", modelo);
    }
}