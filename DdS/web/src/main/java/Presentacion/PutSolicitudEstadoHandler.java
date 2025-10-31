package Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

public class PutSolicitudEstadoHandler implements Handler {
    private final String urlAdmin;
    private final OkHttpClient client = new OkHttpClient();

    public PutSolicitudEstadoHandler(String urlAdmin) {
        this.urlAdmin = urlAdmin;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
       /* if (!esAdministrador(ctx)) {
            ctx.status(403).result("No tiene permisos de administrador");
            return;
        }*/

        String id = ctx.pathParam("id");
        String tipo = ctx.pathParam("tipo");
        String accion = ctx.body(); // "ACEPTADA" o "RECHAZADA"

        String endpoint = "";
        if ("eliminacion".equals(tipo)) {
            endpoint = "/api/solicitudes/" + id;
        } else if ("modificacion".equals(tipo)) {
            endpoint = "/api/solicitudes-modificacion/" + id; // Ajusta según tu API
        } else {
            ctx.status(400).result("Tipo de solicitud inválido");
            return;
        }

        // Llamar a la API Admin para actualizar el estado
        Request request = new Request.Builder()
                .url(urlAdmin + endpoint)
                .put(RequestBody.create(accion, MediaType.parse("text/plain")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            ctx.status(response.code()).result(response.body().string());
        }
    }
/*
    private boolean esAdministrador(Context ctx) {
        String username = ctx.sessionAttribute("username");
        String role = ctx.sessionAttribute("role");
        return "admin".equals(role) || (username != null && username.contains("admin"));
    }*/
}