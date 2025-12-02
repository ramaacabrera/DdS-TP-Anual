package web.controller;

import io.javalin.http.Handler;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import web.domain.Solicitudes.SolicitudDeEliminacion;
import web.domain.Solicitudes.SolicitudDeModificacion;
import web.service.SolicitudService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolicitudController {

    private SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    public Handler actualizarEstadoSolicitud = ctx -> {
        /* if (!esAdministrador(ctx)) {
            ctx.status(403).result("No tiene permisos de administrador");
            return;
        }*/

        String id = ctx.pathParam("id");
        String tipo = ctx.pathParam("tipo");
        String accion = ctx.body(); // "ACEPTADA" o "RECHAZADA"

        try {
            solicitudService.actualizarEstadoSolicitud(id, tipo, accion);
            ctx.status(200).result("Solicitud actualizada correctamente");
        }catch (Exception e){
            ctx.status(400).result("Error al actualizar solicitud");
        }
    };

    public Handler listarSolicitudes = ctx -> {
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
    };

    public Handler obtenerSolicitud = ctx -> {
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
    };

    public Handler obtenerFormsEliminarSolicitud = ctx -> {
        try {
            String hechoId = ctx.pathParam("id");
            System.out.println("Solicitando formulario de eliminación para hecho ID: " + hechoId);

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Solicitar Eliminación");
            modelo.put("hechoId", hechoId);

            if(!ctx.sessionAttributeMap().isEmpty()){
                String username = ctx.sessionAttribute("username");
                System.out.println("Usuario: " + username);
                String access_token = ctx.sessionAttribute("access_token");
                modelo.put("username", username);
                modelo.put("access_token", access_token);
            }

            ctx.render("crear-solicitud-eliminacion.ftl", modelo);

        } catch (Exception e) {
            System.err.println("ERROR en GetSolicitudEliminacionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar el formulario: " + e.getMessage());
        }
    };

}
