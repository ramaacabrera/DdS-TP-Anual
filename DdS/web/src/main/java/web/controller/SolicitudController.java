package web.controller;

import io.javalin.http.Handler;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import web.domain.Solicitudes.SolicitudDeEliminacion;
import web.domain.Solicitudes.SolicitudDeModificacion;
import web.service.SolicitudService;
import web.utils.ViewUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolicitudController {

    private SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    public Handler listarSolicitudes = ctx -> {
        try {
            System.out.println("Listando todas las solicitudes");

            String username = ctx.sessionAttribute("username");
            String token = ctx.sessionAttribute("access_token");

            if (username == null || token == null) {
                ctx.redirect("/login");
                return;
            }

            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Solicitudes");

            modelo.put("solicitudesEliminacion",
                    solicitudService.obtenerSolicitudesEliminacion(username, token));

            modelo.put("solicitudesModificacion",
                    solicitudService.obtenerSolicitudesModificacion(username, token));

            ctx.render("solicitudes.ftl", modelo);

        } catch (Exception e) {
            System.err.println("Error listarSolicitudes: " + e.getMessage());
            ctx.status(500).result("Error al cargar solicitudes: " + e.getMessage());
        }
    };

    public Handler obtenerSolicitud = ctx -> {
        try {
            String id = ctx.pathParam("id");
            String tipo = ctx.pathParam("tipo");

            String username = ctx.sessionAttribute("username");
            String token = ctx.sessionAttribute("access_token");

            if (username == null || token == null) {
                ctx.redirect("/admin/login");
                return;
            }

            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Detalle de solicitud");

            if ("eliminacion".equals(tipo)) {

                SolicitudDeEliminacion sol =
                        solicitudService.obtenerSolicitudEliminacion(id, username, token);

                if (sol == null) {
                    ctx.status(404).result("Solicitud no encontrada");
                    return;
                }

                modelo.put("solicitud", sol);
                modelo.put("tipo", "eliminacion");
                modelo.put("hecho", sol.getHechoAsociado());
            }
            else if ("modificacion".equals(tipo)) {

                SolicitudDeModificacion sol =
                        solicitudService.obtenerSolicitudModificacion(id, username, token);

                if (sol == null) {
                    ctx.status(404).result("Solicitud no encontrada");
                    return;
                }

                modelo.put("solicitud", sol);
                modelo.put("tipo", "modificacion");
                modelo.put("hecho", sol.getHechoAsociado());
            }
            else {
                ctx.status(400).result("Tipo inválido");
                return;
            }

            ctx.render("solicitud-detalle.ftl", modelo);

        } catch (Exception e) {
            System.err.println("Error obtenerSolicitud: " + e.getMessage());
            ctx.status(500).result("Error al cargar la solicitud: " + e.getMessage());
        }
    };


    public Handler obtenerFormsEliminarSolicitud = ctx -> {
        try {
            String hechoId = ctx.pathParam("id");

            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Solicitar Eliminación");
            modelo.put("hechoId", hechoId);

            ctx.render("crear-solicitud-eliminacion.ftl", modelo);

        } catch (Exception e) {
            System.err.println("Error obtenerFormsEliminarSolicitud: " + e.getMessage());
            ctx.status(500).result("Error al cargar formulario: " + e.getMessage());
        }
    };

    public Handler actualizarEstadoSolicitud = ctx -> {
        try {
            String id = ctx.pathParam("id");
            String tipo = ctx.pathParam("tipo");
            String nuevoEstado = ctx.body(); // "ACEPTADA" o "RECHAZADA"

            // Obtener datos de sesión igual que en Colecciones
            String username = ctx.sessionAttribute("username");
            String token    = ctx.sessionAttribute("access_token");

            if (username == null || token == null) {
                ctx.status(401).result("Sesión expirada. Inicie sesión nuevamente.");
                return;
            }

            int status = solicitudService.actualizarEstadoSolicitud(id, tipo, nuevoEstado, username, token);

            if (status >= 200 && status < 300) {
                ctx.status(200).result("Solicitud actualizada");
            } else {
                ctx.status(500).result("Error del servidor administrativo: HTTP " + status);
            }

        } catch (Exception e) {
            System.err.println("Error actualizarEstadoSolicitud: " + e.getMessage());
            ctx.status(500).result("Error al actualizar: " + e.getMessage());
        }
    };
}