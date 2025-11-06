package DominioAgregador.Cargador;

import utils.Dominio.fuente.Fuente;
import utils.Dominio.fuente.TipoDeFuente;
import utils.Persistencia.FuenteRepositorio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsContext;
import utils.DTO.ModelosMensajesDTO.MensajeVacioPayload;
import utils.DTO.ModelosMensajesDTO.WsMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConexionCargador {

    private final ConcurrentMap<UUID, WsContext> fuentes = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, UUID> sessionToFuenteMap = new ConcurrentHashMap<>(); // ✅ Nuevo mapa para sesiones
    FuenteRepositorio fuenteRepositorio;
    ObjectMapper mapper = new ObjectMapper();

    public ConexionCargador(FuenteRepositorio nuevaFuenteRepositorio) {
        fuenteRepositorio = nuevaFuenteRepositorio;
    }

    public ConcurrentMap<UUID, WsContext> getFuentes() { return fuentes; }

    public void registrarFuentePorSession(String sessionId, UUID fuenteId, WsContext ctx) {
        sessionToFuenteMap.put(sessionId, fuenteId);
        fuentes.put(fuenteId, ctx);
        System.out.println("🔗 Fuente registrada - Session: " + sessionId + " -> Fuente: " + fuenteId);
    }

    public boolean borrarFuentePorSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            System.out.println("⚠️ Session ID nulo o vacío");
            return false;
        }

        UUID fuenteId = sessionToFuenteMap.remove(sessionId);
        if (fuenteId != null) {
            fuentes.remove(fuenteId);
            System.out.println("🔌 Fuente removida - Session: " + sessionId + " -> Fuente: " + fuenteId);
            return true;
        } else {
            System.out.println("⚠️ No se encontró fuente para session: " + sessionId);
            return false;
        }
    }

    public boolean borrarFuente(UUID idFuente) {
        if (idFuente == null) {
            System.out.println("⚠️ ID de fuente nulo");
            return false;
        }

        // Remover de ambas estructuras
        fuentes.remove(idFuente);

        // Buscar y remover del mapa de sesiones
        sessionToFuenteMap.entrySet().removeIf(entry -> entry.getValue().equals(idFuente));

        System.out.println("🔌 Fuente removida por ID: " + idFuente);
        return true;
    }

    public UUID obtenerFuenteIdPorSession(String sessionId) {
        return sessionToFuenteMap.get(sessionId);
    }

    public void mostrarSesionesActivas() {
        System.out.println("=== SESIONES ACTIVAS ===");
        sessionToFuenteMap.forEach((sessionId, fuenteId) -> {
            System.out.println("Session: " + sessionId + " -> Fuente: " + fuenteId);
        });
        System.out.println("Total: " + sessionToFuenteMap.size() + " sesiones activas");
    }

    public void obtenerHechosNuevos(){
        fuentes.forEach((fuenteId, ctx) -> {
            String mensaje = null;
            try {
                mensaje = mapper.writeValueAsString(new WsMessage<MensajeVacioPayload>("obtenerHechos", null));
                System.out.println("envio mensaje obtenerHechos a: " + fuenteId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            ctx.send(mensaje);
        });
    }

    public void obtenerSolicitudes() {
        fuentes.forEach((fuenteId, ctx) -> {
            Fuente fuente = fuenteRepositorio.buscarPorId(fuenteId);
            String mensaje = null;
            if(fuente.getTipoDeFuente().equals(TipoDeFuente.DINAMICA)) {
                try{
                    mensaje = mapper.writeValueAsString(new WsMessage<MensajeVacioPayload>("obtenerSolicitudesEliminacion", null));
                    ctx.send(mensaje);
                    mensaje = mapper.writeValueAsString(new WsMessage<MensajeVacioPayload>("obtenerSolicitudesModificacion", null));
                    ctx.send(mensaje);

                    System.out.println("envio mensaje obtenerSolicitudes a: " + fuenteId);
                }
                catch(JsonProcessingException e){
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void agregarFuente(UUID idFuente, WsContext ctx) {
        fuentes.put(idFuente, ctx);
        System.out.println("⚠️ Usando agregarFuente sin sessionId. Considera usar registrarFuentePorSession");
    }
}