package Agregador.Handlers;

import Agregador.PaqueteAgregador.Agregador;
import Agregador.fuente.Fuente;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.ModelosMensajesDTO.HechosObtenidosPayload;
import utils.DTO.ModelosMensajesDTO.SolicitudesEliminacionObtenidosPayload;
import utils.DTO.ModelosMensajesDTO.SolicitudesModificacionObtenidosPayload;
import utils.DTO.SolicitudDeEliminacionDTO;
import utils.DTO.SolicitudDeModificacionDTO;

import java.util.ArrayList;
import java.util.List;

public class OnMessageHandler {

    Agregador agregador;

    public OnMessageHandler(Agregador agregadorNuevo){
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
                    agregador.actualizarHechosDesdeFuentes(payload.hechos);
                }
                case "solicitudesModificacionObtenidos" -> {
                    SolicitudesModificacionObtenidosPayload payload = mapper.treeToValue(root.get("payload"), SolicitudesModificacionObtenidosPayload.class);
                    System.out.println("→ Hechos recibidos: " + payload.solicitudes.size());
                    List<SolicitudDeEliminacionDTO> solicitudesVacia = new ArrayList<>();
                    agregador.agregarSolicitudes(payload.solicitudes, solicitudesVacia);
                }
                case "solicitudesDeEliminacionObtenidos" -> {
                    SolicitudesEliminacionObtenidosPayload payload = mapper.treeToValue(root.get("payload"), SolicitudesEliminacionObtenidosPayload.class);
                    System.out.println("→ Hechos recibidos: " + payload.solicitudes.size());
                    List<SolicitudDeModificacionDTO> solicitudesVacia = new ArrayList<>();
                    agregador.agregarSolicitudes(solicitudesVacia, payload.solicitudes);
                }
            }
        }
        catch (Exception e) {
            System.err.println("Error al procesar el objeto " + e.getMessage());
        }

    }
}
