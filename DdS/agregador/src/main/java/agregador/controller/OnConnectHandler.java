package agregador.controller;

import agregador.service.ConexionCargadorService;
// import agregador.repository.*; // Ya no hace falta importar repositorios aquí
import agregador.domain.fuente.Fuente;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import org.jetbrains.annotations.NotNull;

public class OnConnectHandler implements WsConnectHandler {
    private final ConexionCargadorService conexionCargadorService;
    private static final ObjectMapper mapper = new ObjectMapper();

    public OnConnectHandler(ConexionCargadorService conCargador) {
        this.conexionCargadorService = conCargador;
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        String sessionId = ctx.getSessionId();
        System.out.println("Nueva conexión WebSocket - Session: " + sessionId);

        try {
            String header = ctx.header("FuenteDTO");
            if (header == null || header.trim().isEmpty()) {
                System.err.println("Conexión rechazada: Header FuenteDTO faltante - Session: " + sessionId);
                ctx.closeSession(1008, "Header FuenteDTO requerido");
                return;
            }

            Fuente nuevo = mapper.readValue(header, Fuente.class);
            if (nuevo.getDescriptor() == null || nuevo.getDescriptor().trim().isEmpty()) {
                System.err.println("Conexión rechazada: Descriptor de fuente inválido - Session: " + sessionId);
                ctx.closeSession(1008, "Descriptor de fuente requerido");
                return;
            }

            System.out.println("Procesando fuente: " + nuevo.getDescriptor() + " - Session: " + sessionId);

            ctx.send(conexionCargadorService.nuevaConexion(sessionId, nuevo, ctx));

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            System.err.println("Error parseando JSON - Session: " + sessionId + " - Error: " + e.getMessage());
            ctx.closeSession(1008, "Formato JSON inválido en FuenteDTO");

        } catch (Exception e) {
            System.err.println("Error inesperado en conexión - Session: " + sessionId + " - Error: " + e.getMessage());
            e.printStackTrace();
            ctx.closeSession(1011, "Error interno del servidor");
        }
    }
}