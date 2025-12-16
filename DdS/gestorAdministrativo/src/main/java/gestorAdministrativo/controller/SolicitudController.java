package gestorAdministrativo.controller;

import gestorAdministrativo.dto.PageDTO;
import gestorAdministrativo.dto.Solicitudes.SolicitudDTO;
import gestorAdministrativo.dto.Solicitudes.SolicitudDeEliminacionDTO;
import gestorAdministrativo.dto.Solicitudes.SolicitudDeModificacionDTO;
import gestorAdministrativo.service.SolicitudEliminacionService;
import gestorAdministrativo.service.SolicitudModificacionService;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
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
        Map<String, String> map = ctx.pathParamMap();
        String estado = map.get("estado");
        System.out.println("Obteniendo solicitudes " + estado);
        try {
            int pagina = ctx.queryParamAsClass("pagina", Integer.class).getOrDefault(1);
            int limite = ctx.queryParamAsClass("limite", Integer.class).getOrDefault(10);

            PageDTO<SolicitudDeEliminacionDTO> resultado = eliminacionService.obtenerTodasLasSolicitudes(pagina, limite, estado);
            System.out.println("Devolviendo solicitudes");
            ctx.status(200).json(resultado);
            System.out.println("Devolvi un 200");
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

    public Handler obtenerCantidadEliminacion = ctx -> {
        String estado = ctx.pathParam("estado");
        System.out.println("Obteniendo cantidad solicitudes " + estado + " eliminacion");
        ctx.status(200).json(eliminacionService.obtenerCantidad(estado));
    };

    public Handler obtenerCantidadModificacion = ctx -> {
        try{
            String estado = ctx.pathParam("estado");
            System.out.println("Obteniendo cantidad "  + estado + " modificacion");
            ctx.status(200).json(modificacionService.obtenerCantidad(estado));
        }catch (IllegalArgumentException e){
            ctx.status(400);
        }
        catch(Exception e){
            ctx.status(500).json("Error obteniendo cantidad pendientes modificacion");
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
        String estado = ctx.queryParam("estado");
        System.out.println("Obteniendo solicitudes de modificacion " + estado);
        try {
            int pagina = ctx.queryParamAsClass("pagina", Integer.class).getOrDefault(1);
            int limite = ctx.queryParamAsClass("limite", Integer.class).getOrDefault(10);

            PageDTO<SolicitudDeModificacionDTO> resultado = modificacionService.obtenerTodasLasSolicitudes(pagina, limite, estado);
            System.out.println("Devolviendo solicitudes");
            ctx.status(200).json(resultado);
            System.out.println("Devolvi un 200");
        } catch (Exception e) {
            ctx.status(500).json("Error obteniendo solicitudes: " + e.getMessage());
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