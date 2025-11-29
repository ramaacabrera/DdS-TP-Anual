package web.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import web.domain.solicitudes.SolicitudDeEliminacion;
import web.domain.solicitudes.SolicitudDeModificacion;
import web.service.SolicitudService;

import java.util.HashMap;
import java.util.Map;

public class GetSolicitudAdminHandler implements Handler {

    private final SolicitudService solicitudService;

    public GetSolicitudAdminHandler(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Validación de permisos (si la descomentas luego)
        /* if (!esAdministrador(ctx)) { ... } */

        String id = ctx.pathParam("id");
        String tipo = ctx.pathParam("tipo"); // "eliminacion" o "modificacion"

        Map<String, Object> model = new HashMap<>();
        boolean encontrado = false;

        // Lógica de selección según el tipo
        if ("eliminacion".equals(tipo)) {
            SolicitudDeEliminacion solicitud = solicitudService.obtenerSolicitudEliminacion(id);
            if (solicitud != null) {
                model.put("solicitud", solicitud);
                model.put("tipo", "eliminacion");
                model.put("hecho", solicitud.getHechoAsociado());
                encontrado = true;
            }
        } else if ("modificacion".equals(tipo)) {
            SolicitudDeModificacion solicitud = solicitudService.obtenerSolicitudModificacion(id);
            if (solicitud != null) {
                model.put("solicitud", solicitud);
                model.put("tipo", "modificacion");
                model.put("hecho", solicitud.getHechoAsociado());
                encontrado = true;
            }
        } else {
            ctx.status(400).result("Tipo de solicitud inválido (use 'eliminacion' o 'modificacion')");
            return;
        }

        if (!encontrado) {
            ctx.status(404).result("Solicitud no encontrada en el sistema administrativo");
            return;
        }

        // Agregar datos de sesión para el Navbar/Header
        if (!ctx.sessionAttributeMap().isEmpty()) {
            model.put("username", ctx.sessionAttribute("username"));
            model.put("access_token", ctx.sessionAttribute("access_token"));
        }

        ctx.render("solicitud-detalle.ftl", model);
    }
}