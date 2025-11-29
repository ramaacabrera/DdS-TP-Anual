package agregador.controller;

import agregador.dto.Solicitudes.SolicitudDeEliminacionDTO;
import agregador.dto.Solicitudes.SolicitudDeModificacionDTO;
import agregador.service.AgregadorOrquestador;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsMessageContext;
import agregador.dto.ModelosMensajesDTO.HechosObtenidosPayload;
import agregador.dto.ModelosMensajesDTO.SolicitudesEliminacionObtenidosPayload;
import agregador.dto.ModelosMensajesDTO.SolicitudesModificacionObtenidosPayload;

import java.util.ArrayList;
import java.util.List;

public class OnMessageHandler {

    private final AgregadorOrquestador agregador;

    public OnMessageHandler(AgregadorOrquestador agregadorNuevo){
        this.agregador = agregadorNuevo;
    }

    ObjectMapper mapper = new ObjectMapper();

    public void handleMessageSeguro(String raw, WsMessageContext ctx) {
        try {
            JsonNode root = mapper.readTree(raw);
            String type = root.get("type").asText();

            switch (type) {
                case "hechosObtenidos" -> {
                    HechosObtenidosPayload payload = mapper.treeToValue(root.get("payload"), HechosObtenidosPayload.class);
                    System.out.println("→ Hechos recibidos: " + payload.hechos.size());
                    agregador.procesarNuevosHechos(payload.hechos);
                }
                case "solicitudesModificacionObtenidos" -> {
                    SolicitudesModificacionObtenidosPayload payload = mapper.treeToValue(root.get("payload"), SolicitudesModificacionObtenidosPayload.class);
                    System.out.println("→ Solicitud de Modificacion recibidas: " + payload.solicitudes.size());

                    List<SolicitudDeEliminacionDTO> solicitudesVacia = new ArrayList<>();

                    agregador.procesarSolicitudes(payload.solicitudes, solicitudesVacia);
                }
                case "solicitudesEliminacionObtenidos" -> {
                    SolicitudesEliminacionObtenidosPayload payload = mapper.treeToValue(root.get("payload"), SolicitudesEliminacionObtenidosPayload.class);
                    System.out.println("→ Solicitud de Eliminacion recibidas: " + payload.solicitudes.size());

                    List<SolicitudDeModificacionDTO> solicitudesVacia = new ArrayList<>();

                    agregador.procesarSolicitudes(solicitudesVacia, payload.solicitudes);
                }
            }
        }
        catch (Exception e) {
            System.err.println("Error al procesar el objeto " + e.getMessage());
            e.printStackTrace();
        }
    }
}