// gestorAdministrativo/controller/SolicitudController.java
package gestorAdministrativo.controller;

import DominioGestorAdministrativo.DTO.Solicitudes.SolicitudDTO;
import DominioGestorAdministrativo.DTO.Solicitudes.SolicitudDeEliminacionDTO;
import gestorAdministrativo.service.SolicitudEliminacionService;
import io.javalin.http.Handler;

import java.util.List;
import java.util.UUID;

public class SolicitudController {
    private SolicitudEliminacionService solicitudService;

    public SolicitudController(SolicitudEliminacionService solicitudService) {
        this.solicitudService = solicitudService;
    }

    public Handler crearSolicitud = ctx -> {
        try {
            SolicitudDTO request = ctx.bodyAsClass(SolicitudDTO.class);

            if (request.getID_HechoAsociado() == null) {
                ctx.status(400).json("El ID del hecho es requerido");
                return;
            }

            if (request.getJustificacion() == null || request.getJustificacion().trim().isEmpty()) {
                ctx.status(400).json("La justificación es requerida");
                return;
            }

            solicitudService.crearSolicitudEliminacion(request);
            ctx.status(201).json("Solicitud creada exitosamente");

        } catch (Exception e) {
            ctx.status(400).json("Error creando solicitud: " + e.getMessage());
        }
    };

    public Handler procesarSolicitud = ctx -> {
        String idString = ctx.pathParam("id");
        ProcesarSolicitudDTO request = ctx.bodyAsClass(ProcesarSolicitudDTO.class);

        try {
            UUID id = UUID.fromString(idString);

            if (request.getAccion() == null || request.getAccion().trim().isEmpty()) {
                ctx.status(400).json("La acción es requerida");
                return;
            }

            boolean resultado = solicitudService.procesarSolicitud(id, request.getAccion());

            if (resultado) {
                ctx.status(200).json("Solicitud " + request.getAccion().toLowerCase() + " correctamente");
            } else {
                ctx.status(404).json("Solicitud no encontrada");
            }

        } catch (IllegalArgumentException e) {
            ctx.status(400).json("ID inválido: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(400).json("Error procesando solicitud: " + e.getMessage());
        }
    };

    public Handler obtenerSolicitudes = ctx -> {
        try {
            List<SolicitudDeEliminacionDTO> solicitudes = solicitudService.obtenerTodasLasSolicitudes();
            ctx.status(200).json(solicitudes);
        } catch (Exception e) {
            ctx.status(500).json("Error obteniendo solicitudes: " + e.getMessage());
        }
    };

    public Handler obtenerSolicitud = ctx -> {
        String idString = ctx.pathParam("id");

        try {
            UUID id = UUID.fromString(idString);
            var solicitud = solicitudService.obtenerSolicitudPorId(id);

            if (solicitud.isPresent()) {
                ctx.status(200).json(solicitud.get());
            } else {
                ctx.status(404).json("Solicitud no encontrada");
            }

        } catch (IllegalArgumentException e) {
            ctx.status(400).json("ID inválido");
        } catch (Exception e) {
            ctx.status(500).json("Error obteniendo solicitud: " + e.getMessage());
        }
    };

    public static class ProcesarSolicitudDTO {
        private String accion;

        public ProcesarSolicitudDTO() {}

        public ProcesarSolicitudDTO(String accion) {
            this.accion = accion;
        }

        public String getAccion() { return accion; }
        public void setAccion(String accion) { this.accion = accion; }
    }
}