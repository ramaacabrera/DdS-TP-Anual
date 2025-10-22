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

public class OnMessageHandler implements WsMessageHandler {

    Agregador agregador;

    public OnMessageHandler(Agregador agregadorNuevo){
        this.agregador = agregadorNuevo;
    }

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        String raw = ctx.message();

        try {
            JsonNode root = mapper.readTree(raw);
            String type = root.get("type").asText();

            switch (type) {
                case "hechosObtenidos":
                    HechosObtenidosPayload payload = mapper.treeToValue(root.get("payload"), HechosObtenidosPayload.class);
                    System.out.println("→ Hechos recibidos: " + payload.hechos.size());
                    agregador.actualizarHechosDesdeFuentes(payload.hechos);
            }
        }
        catch (Exception e) {
            System.err.println("Error al procesar el objeto " + e.getMessage());
        }
    }
}
