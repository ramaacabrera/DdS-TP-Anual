package gestorPublico.controller;

import gestorPublico.service.SolicitudService;
import io.javalin.http.Handler;

import java.net.http.HttpResponse;

public class SolicitudController {

    private SolicitudService solicitudService=null;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    public Handler crearSolicitudEliminacion = ctx -> {
        try {
            String body = ctx.body();

            HttpResponse<String> response = solicitudService.enviarSolicitudEliminacion(body);

            ctx.status(response.statusCode()).result(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json("Error interno al procesar solicitud: " + e.getMessage());
        }
    };

    public Handler crearSolicitudModificacion = ctx -> {
        try {
            String body = ctx.body();

            HttpResponse<String> response = solicitudService.enviarSolicitudModificacion(body);

            ctx.status(response.statusCode()).result(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json("Error interno al procesar solicitud: " + e.getMessage());
        }
    };
}