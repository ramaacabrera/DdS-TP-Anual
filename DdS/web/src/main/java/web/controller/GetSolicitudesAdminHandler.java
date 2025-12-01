package web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import web.domain.Solicitudes.SolicitudDeEliminacion;
import web.domain.Solicitudes.SolicitudDeModificacion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSolicitudesAdminHandler implements Handler {
    private final String urlAdmin;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public GetSolicitudesAdminHandler(String urlAdmin) {
        this.urlAdmin = urlAdmin;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Verificar que el usuario es admin (puedes implementar esta lógica)
        /*if (!esAdministrador(ctx)) {
            ctx.redirect("/login");
            return;
        }*/

        // Obtener solicitudes de eliminación
        List<SolicitudDeEliminacion> solicitudesEliminacion = obtenerSolicitudesEliminacion();

        // Obtener solicitudes de modificación (si las tienes)
        List<SolicitudDeModificacion> solicitudesModificacion = obtenerSolicitudesModificacion();

        Map<String, Object> model = new HashMap<>();
        model.put("solicitudesEliminacion", solicitudesEliminacion);
        model.put("solicitudesModificacion", solicitudesModificacion);

        // Agregar datos de sesión si existen
        if(!ctx.sessionAttributeMap().isEmpty()){
            String username = ctx.sessionAttribute("username");
            String access_token = ctx.sessionAttribute("access_token");
            model.put("username", username);
            model.put("access_token", access_token);
        }

        ctx.render("solicitudes.ftl", model);
    }
/*
    private List<SolicitudDeEliminacion> obtenerSolicitudesEliminacion() throws IOException {
        Request request = new Request.Builder()
                .url(urlAdmin + "/api/solicitudes")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String body = response.body().string();
                return mapper.readValue(body, new TypeReference<List<SolicitudDeEliminacion>>() {});
            } else {
                System.err.println("Error obteniendo solicitudes de eliminación: " + response.code());
                return new ArrayList<>();
            }
        }
    }*/

    private List<SolicitudDeEliminacion> obtenerSolicitudesEliminacion() throws IOException {
        String apiUrl = urlAdmin + "api/solicitudes";
        System.out.println("DEBUG: Intentando obtener solicitudes de: " + apiUrl);

        Request request = new Request.Builder()
                .url(apiUrl)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("DEBUG: Código de respuesta de la API Admin: " + response.code());

            if (response.isSuccessful()) {
                String body = response.body().string();
                System.out.println("DEBUG: Cuerpo de respuesta (JSON recibido): " + (body.length() > 200 ? body.substring(0, 200) + "..." : body));

                // Manejo de errores de Deserialización
                try {
                    List<SolicitudDeEliminacion> lista = mapper.readValue(body, new TypeReference<List<SolicitudDeEliminacion>>() {});
                    System.out.println("DEBUG: Solicitudes encontradas (Deserializadas): " + lista.size());
                    return lista;
                } catch (Exception e) {
                    System.err.println("❌ ERROR CRÍTICO AL DESERIALIZAR JSON de la API Admin: " + e.getMessage());
                    e.printStackTrace();
                    return new ArrayList<>(); // Retorna lista vacía si falla el JSON
                }
            } else {
                System.err.println("❌ Error HTTP en la API Admin: " + response.code() + " - Mensaje: " + response.message());
                return new ArrayList<>(); // Retorna lista vacía si falla la red
            }
        }
    }



    private List<SolicitudDeModificacion> obtenerSolicitudesModificacion() throws IOException {
        // Si tienes endpoint para solicitudes de modificación
        try {
            String urlCompleta = urlAdmin + "api/solicitudes";
            System.out.println("=== DEBUG HTTP REQUEST ===");
            System.out.println("🔍 URL Admin base: " + urlAdmin);
            System.out.println("🔍 URL completa: " + urlCompleta);

            Request request = new Request.Builder()
                    .url(urlAdmin + "/api/solicitudes-modificacion") // Ajusta el endpoint
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    return mapper.readValue(body, new TypeReference<List<SolicitudDeModificacion>>() {});
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo solicitudes de modificación: " + e.getMessage());
        }
        return new ArrayList<>();
    }
/*
    private boolean esAdministrador(Context ctx) {
        // Implementa tu lógica de verificación de admin
        // Por ejemplo, verificar un claim en el token o una sesión
        String username = ctx.sessionAttribute("username");
        String role = ctx.sessionAttribute("role");

        return "admin".equals(role) || (username != null && username.contains("admin"));
        // Esto es un ejemplo, ajusta según tu lógica de roles
    }*/
}