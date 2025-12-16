package gestorPublico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gestorPublico.service.SolicitudService;
import io.javalin.http.Handler;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class SolicitudController {

    private SolicitudService solicitudService=null;
    private final ObjectMapper mapper= new ObjectMapper();;

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
            ctx.status(500).json("Error interno: " + e.getMessage());
        }
    };

    public Handler crearSolicitudModificacion = ctx -> {
        try {
            Map<String, Object> bodyFront = ctx.bodyAsClass(Map.class);

            String hechoId = (String) bodyFront.get("hechoId");
            String justificacion = (String) bodyFront.get("justificacion");

            String usuarioId = (String) bodyFront.get("usuarioId");
            String username = (String) bodyFront.get("username");

            Object hechoModificado = bodyFront.get("hechoModificado");

            System.out.println("GESTOR PUBLICO -> Procesando solicitud.");
            System.out.println("  > Usuario UUID recibido: " + usuarioId);
            System.out.println("  > Username recibido: " + username);

            Map<String, Object> dtoDinamico = new HashMap<>();

            dtoDinamico.put("ID_HechoAsociado", hechoId);
            dtoDinamico.put("Justificacion", justificacion);
            dtoDinamico.put("HechoModificado", hechoModificado);

            Map<String, String> usuarioMap = new HashMap<>();

            usuarioMap.put("id_usuario", usuarioId);
            usuarioMap.put("username", username);

            dtoDinamico.put("Usuario", usuarioMap);

            String jsonParaDinamico = mapper.writeValueAsString(dtoDinamico);

            System.out.println("GESTOR PUBLICO -> JSON saliente: " + jsonParaDinamico);

            HttpResponse<String> response = solicitudService.enviarSolicitudModificacion(jsonParaDinamico);

            ctx.status(response.statusCode()).result(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json("Error interno en Gestor Publico: " + e.getMessage());
        }
    };
}