package agregador.Handlers;

import agregador.Cargador.ConexionCargador;
import utils.Persistencia.FuenteRepositorio;
import utils.Dominio.fuente.Fuente;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.ModelosMensajesDTO.IdCargadorPayload;
import utils.DTO.ModelosMensajesDTO.WsMessage;

public class OnConnectHandler implements WsConnectHandler {
    private final FuenteRepositorio fuenteRepositorio;
    private final ConexionCargador conexionCargador;
    private static final ObjectMapper mapper = new ObjectMapper();

    public OnConnectHandler(ConexionCargador conCargador, FuenteRepositorio fuenteRepo) {
        fuenteRepositorio = fuenteRepo;
        conexionCargador = conCargador;
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

            // ✅ Buscar o guardar fuente
            Fuente fuentePersistida = fuenteRepositorio.buscarPorRuta(nuevo.getDescriptor());
            if (fuentePersistida == null) {
                System.out.println("➕ Guardando nueva fuente: " + nuevo.getDescriptor());
                fuentePersistida = fuenteRepositorio.guardar(nuevo);
            } else {
                System.out.println("🔍 Fuente existente encontrada: " + fuentePersistida.getId());
            }

            // ✅ Registrar fuente con sessionId (usando el nuevo método)
            conexionCargador.registrarFuentePorSession(sessionId, fuentePersistida.getId(), ctx);

            // ✅ Enviar ID al cliente
            WsMessage<IdCargadorPayload> mensaje = new WsMessage<>("idCargador", new IdCargadorPayload(fuentePersistida.getId()));
            ctx.send(mapper.writeValueAsString(mensaje));

            System.out.println("🎯 Conexión establecida exitosamente - " +
                    "Fuente: " + fuentePersistida.getId() + " - " +
                    "Session: " + sessionId);

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