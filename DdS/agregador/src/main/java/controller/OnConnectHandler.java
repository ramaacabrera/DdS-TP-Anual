package controller;

import service.ConexionCargadorService;
import repository.*;
import domain.fuente.Fuente;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import org.jetbrains.annotations.NotNull;
import domain.DTO.ModelosMensajesDTO.IdCargadorPayload;
import domain.DTO.ModelosMensajesDTO.WsMessage;

public class OnConnectHandler implements WsConnectHandler {
    private final ConexionCargadorService conexionCargadorService;
    private static final ObjectMapper mapper = new ObjectMapper();

    public OnConnectHandler(ConexionCargadorService conCargador, FuenteRepositorio fuenteRepo) {
        conexionCargadorService = conCargador;
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        String sessionId = ctx.getSessionId();
        System.out.println("✅ Nueva conexión WebSocket - Session: " + sessionId);

        try {
            // ✅ Validar header FuenteDTO
            String header = ctx.header("FuenteDTO");
            if (header == null || header.trim().isEmpty()) {
                System.err.println("❌ Conexión rechazada: Header FuenteDTO faltante - Session: " + sessionId);
                ctx.closeSession(400, "Header FuenteDTO requerido");
                return;
            }

            // ✅ Parsear y validar fuente
            Fuente nuevo = mapper.readValue(header, Fuente.class);
            if (nuevo.getDescriptor() == null || nuevo.getDescriptor().trim().isEmpty()) {
                System.err.println("❌ Conexión rechazada: Descriptor de fuente inválido - Session: " + sessionId);
                ctx.closeSession(400, "Descriptor de fuente requerido");
                return;
            }

            System.out.println("🔍 Procesando fuente: " + nuevo.getDescriptor() + " - Session: " + sessionId);

            ctx.send(conexionCargadorService.nuevaConexion(sessionId,nuevo, ctx));

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            System.err.println("❌ Error parseando JSON - Session: " + sessionId + " - Error: " + e.getMessage());
            ctx.closeSession(400, "Formato JSON inválido en FuenteDTO");

        } catch (Exception e) {
            System.err.println("❌ Error inesperado en conexión - Session: " + sessionId + " - Error: " + e.getMessage());
            e.printStackTrace();
            ctx.closeSession(500, "Error interno del servidor");
        }
    }
}