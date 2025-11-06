package web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Dominio.Solicitudes.SolicitudDeEliminacion;
import utils.Dominio.Solicitudes.SolicitudDeModificacion;

import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import java.io.IOException;


public class GetSolicitudAdminHandler implements Handler {
    private final String urlAdmin;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public GetSolicitudAdminHandler(String urlAdmin) {
        this.urlAdmin = urlAdmin;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
       /* if (!esAdministrador(ctx)) {
            ctx.redirect("/login");
            return;
        }*/

        String id = ctx.pathParam("id");
        String tipo = ctx.pathParam("tipo"); // "eliminacion" o "modificacion"

        Map<String, Object> model = new HashMap<>();

        if ("eliminacion".equals(tipo)) {
            SolicitudDeEliminacion solicitud = obtenerSolicitudEliminacion(id);
            if (solicitud != null) {
                model.put("solicitud", solicitud);
                model.put("tipo", "eliminacion");
                model.put("hecho", solicitud.getHechoAsociado());
            } else {
                ctx.status(404).result("Solicitud no encontrada");
                return;
            }
        } else if ("modificacion".equals(tipo)) {
            SolicitudDeModificacion solicitud = obtenerSolicitudModificacion(id);
            if (solicitud != null) {
                model.put("solicitud", solicitud);
                model.put("tipo", "modificacion");
                model.put("hecho", solicitud.getHechoAsociado());
            } else {
                ctx.status(404).result("Solicitud no encontrada");
                return;
            }
        } else {
            ctx.status(400).result("Tipo de solicitud inválido");
            return;
        }

        // Agregar datos de sesión
        if(!ctx.sessionAttributeMap().isEmpty()){
            String username = ctx.sessionAttribute("username");
            String access_token = ctx.sessionAttribute("access_token");
            model.put("username", username);
            model.put("access_token", access_token);
        }

        ctx.render("solicitud-detalle.ftl", model);
    }

    private SolicitudDeEliminacion obtenerSolicitudEliminacion(String id) throws IOException {
        Request request = new Request.Builder()
                .url(urlAdmin + "api/solicitudes/" + id)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String body = response.body().string();
                return mapper.readValue(body, SolicitudDeEliminacion.class);
            } else {
                System.err.println("Error obteniendo solicitud: " + response.code());
                return null;
            }
        }
    }

    private SolicitudDeModificacion obtenerSolicitudModificacion(String id) throws IOException {
        // Si tienes endpoint para solicitudes de modificación
        try {
            Request request = new Request.Builder()
                    .url(urlAdmin + "api/solicitudes-modificacion/" + id)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    return mapper.readValue(body, SolicitudDeModificacion.class);
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo solicitud de modificación: " + e.getMessage());
        }
        return null;
    }
/*
    private boolean esAdministrador(Context ctx) {
        String username = ctx.sessionAttribute("username");
        String role = ctx.sessionAttribute("role");
        return "admin".equals(role) || (username != null && username.contains("admin"));
    }*/
}