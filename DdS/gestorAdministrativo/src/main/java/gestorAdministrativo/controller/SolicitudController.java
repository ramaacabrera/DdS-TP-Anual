package gestorAdministrativo.controller;

import DominioGestorAdministrativo.DTO.Solicitudes.SolicitudDTO;
import DominioGestorAdministrativo.DTO.Solicitudes.SolicitudDeEliminacionDTO;
import DominioGestorAdministrativo.DTO.Solicitudes.SolicitudDeModificacionDTO;
import gestorAdministrativo.service.SolicitudEliminacionService;
import gestorAdministrativo.service.SolicitudModificacionService;
import io.javalin.http.Handler;

import java.util.List;
import java.util.UUID;

public class SolicitudController {

    private SolicitudEliminacionService eliminacionService=null;
    private SolicitudModificacionService modificacionService=null;

    public SolicitudController(SolicitudEliminacionService eliminacionService, SolicitudModificacionService modificacionService) {
        this.eliminacionService = eliminacionService;
        this.modificacionService = modificacionService;
    }

    public Handler crearSolicitud = ctx -> {
        try {
            SolicitudDTO request = ctx.bodyAsClass(SolicitudDTO.class);

            if (request.getHechoId() == null) {
                ctx.status(400).json("El ID del hecho es requerido");
                return;
            }

            if (request.getUsuarioId() == null) {
                ctx.status(400).json("El ID del usuario es requerido");
                return;
            }

            if (request.getJustificacion() == null || request.getJustificacion().trim().isEmpty()) {
                ctx.status(400).json("La justificación es requerida");
                return;
            }

            eliminacionService.crearSolicitudEliminacion(request);
            ctx.status(201).json("Solicitud creada exitosamente");

        } catch (IllegalArgumentException e) {
            // Capturamos errores de validación lógica (ej. hecho no existe)
            ctx.status(400).json(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json("Error interno creando solicitud: " + e.getMessage());
        }
    };

    public Handler procesarSolicitud = ctx -> {
        String idString = ctx.pathParam("id");
        // Usamos la clase estática interna para mapear el JSON {"accion": "APROBADA"}
        ProcesarSolicitudDTO request = ctx.bodyAsClass(ProcesarSolicitudDTO.class);

        try {
            UUID id = UUID.fromString(idString);

            if (request.getAccion() == null || request.getAccion().trim().isEmpty()) {
                ctx.status(400).json("La acción es requerida");
                return;
            }

            boolean resultado = eliminacionService.procesarSolicitud(id, request.getAccion());

            if (resultado) {
                ctx.status(200).json("Solicitud " + request.getAccion().toLowerCase() + " correctamente");
            } else {
                ctx.status(404).json("Solicitud no encontrada o estado inválido");
            }

        } catch (IllegalArgumentException e) {
            ctx.status(400).json("Error: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).json("Error procesando solicitud: " + e.getMessage());
        }
    };

    public Handler obtenerSolicitudes = ctx -> {
        try {
            List<SolicitudDeEliminacionDTO> solicitudes = eliminacionService.obtenerTodasLasSolicitudes();
            ctx.status(200).json(solicitudes);
        } catch (Exception e) {
            ctx.status(500).json("Error obteniendo solicitudes: " + e.getMessage());
        }
    };

    public Handler obtenerSolicitud = ctx -> {
        String idString = ctx.pathParam("id");

        try {
            UUID id = UUID.fromString(idString);
            var solicitud = eliminacionService.obtenerSolicitudPorId(id);

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

    public Handler crearSolicitudModificacion = ctx -> {
        try {
            SolicitudDeModificacionDTO request = ctx.bodyAsClass(SolicitudDeModificacionDTO.class);

            if (request.getHechoId() == null || request.getUsuarioId() == null) {
                ctx.status(400).json("Faltan datos (hechoId, usuarioId)");
                return;
            }

            modificacionService.crearSolicitud(request);
            ctx.status(201).json("Solicitud de modificación creada exitosamente");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400).json("Error creando solicitud: " + e.getMessage());
        }
    };

    public Handler procesarSolicitudModificacion = ctx -> {
        String idString = ctx.pathParam("id");
        ProcesarSolicitudDTO request = ctx.bodyAsClass(ProcesarSolicitudDTO.class);

        try {
            UUID id = UUID.fromString(idString);
            boolean resultado = modificacionService.procesarSolicitud(id, request.getAccion());

            if (resultado) {
                ctx.status(200).json("Solicitud de modificación procesada");
            } else {
                ctx.status(404).json("Solicitud no encontrada");
            }
        } catch (Exception e) {
            ctx.status(400).json("Error: " + e.getMessage());
        }
    };

    public Handler obtenerSolicitudesModificacion = ctx -> {
        try {
            ctx.json(modificacionService.obtenerTodasLasSolicitudes());
        } catch (Exception e) {
            ctx.status(500).json(e.getMessage());
        }
    };

    public Handler obtenerSolicitudModificacion = ctx -> {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            var sol = modificacionService.obtenerSolicitudPorId(id);
            if(sol.isPresent()) ctx.json(sol.get()); else ctx.status(404);
        } catch (Exception e) {
            ctx.status(400).json("ID inválido");
        }
    };
}