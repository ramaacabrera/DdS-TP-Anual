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

        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("solicitudesEliminacion", solicitudesEliminacion);
        model.put("solicitudesModificacion", solicitudesModificacion);

        ctx.render("solicitudes.ftl", model);
    };

    public Handler obtenerSolicitud = ctx -> {
        // Validación de permisos (si la descomentas luego)
        /* if (!esAdministrador(ctx)) { ... } */

        String id = ctx.pathParam("id");
        String tipo = ctx.pathParam("tipo"); // "eliminacion" o "modificacion"

        Map<String, Object> model = ViewUtil.baseModel(ctx);
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

        ctx.render("solicitud-detalle.ftl", model);
    };

    public Handler obtenerFormsEliminarSolicitud = ctx -> {
        try {
            String hechoId = ctx.pathParam("id");
            System.out.println("Solicitando formulario de eliminación para hecho ID: " + hechoId);

            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Solicitar Eliminación");
            modelo.put("hechoId", hechoId);

            ctx.render("crear-solicitud-eliminacion.ftl", modelo);

        } catch (Exception e) {
            System.err.println("ERROR en GetSolicitudEliminacionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar el formulario: " + e.getMessage());
        }
    };

}
