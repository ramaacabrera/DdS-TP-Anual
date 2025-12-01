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
import web.service.SolicitudService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSolicitudesAdminHandler implements Handler {

    SolicitudService solicitudService;
    public GetSolicitudesAdminHandler(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Obtener solicitudes de eliminación
        List<SolicitudDeEliminacion> solicitudesEliminacion = solicitudService.obtenerSolicitudesEliminacion();

        // Obtener solicitudes de modificación (si las tienes)
        List<SolicitudDeModificacion> solicitudesModificacion = solicitudService.obtenerSolicitudesModificacion();

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
    private boolean esAdministrador(Context ctx) {
        // Implementa tu lógica de verificación de admin
        // Por ejemplo, verificar un claim en el token o una sesión
        String username = ctx.sessionAttribute("username");
        String role = ctx.sessionAttribute("role");

        return "admin".equals(role) || (username != null && username.contains("admin"));
        // Esto es un ejemplo, ajusta según tu lógica de roles
    }*/
}