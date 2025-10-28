package Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GetSolicitudEliminacionHandler implements Handler {
    private final String urlPublica;

    public GetSolicitudEliminacionHandler(String urlPublica) {
        this.urlPublica = urlPublica;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            String hechoId = ctx.pathParam("id");
            System.out.println("Solicitando formulario de eliminación para hecho ID: " + hechoId);

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Solicitar Eliminación");
            modelo.put("hechoId", hechoId);
            modelo.put("urlPublica", urlPublica);

            ctx.render("crear-solicitud-eliminacion.ftl", modelo);

        } catch (Exception e) {
            System.err.println("ERROR en GetSolicitudEliminacionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar el formulario: " + e.getMessage());
        }
    }
}