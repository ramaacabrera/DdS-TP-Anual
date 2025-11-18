package repository;

import io.javalin.websocket.WsContext;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConexionCargadorRepositorio {

    private final ConcurrentMap<UUID, WsContext> fuentes = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, UUID> sessionToFuenteMap = new ConcurrentHashMap<>();

    public ConcurrentMap<String, UUID> obtenerFuentesIDs() {
        return this.sessionToFuenteMap;
    }

    public void registrarFuentePorSession(String sessionId, UUID fuenteId, WsContext ctx) {
        sessionToFuenteMap.put(sessionId, fuenteId);
        fuentes.put(fuenteId, ctx);
    }

    public UUID borrarFuentePorSession(String sessionId) {
        UUID fuenteId = sessionToFuenteMap.remove(sessionId);
        fuentes.remove(fuenteId);
        return fuenteId;
    }

    public Boolean borrarFuente(UUID idFuente) {
        try{
            // Remover de ambas estructuras
            fuentes.remove(idFuente);

            // Buscar y remover del mapa de sesiones
            sessionToFuenteMap.entrySet().removeIf(entry -> entry.getValue().equals(idFuente));
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public UUID obtenerFuenteIdPorSession(String sessionId) {
        return sessionToFuenteMap.get(sessionId);
    }

    public ConcurrentMap<UUID, WsContext> obtenerFuentesCtxs() {
        return this.fuentes;
    }

    public void agregarFuente(UUID idFuente, WsContext ctx) {
        fuentes.put(idFuente, ctx);
    }
}
