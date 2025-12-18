package agregador.repository;

import io.javalin.websocket.WsContext;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConexionCargadorRepositorio {

    private final ConcurrentMap<String, UUID> sesionesFuentes = new ConcurrentHashMap<>();

    private final ConcurrentMap<UUID, WsContext> fuentesContextos = new ConcurrentHashMap<>();

    public void registrarFuentePorSession(String sessionId, UUID fuenteId, WsContext ctx) {
        sesionesFuentes.put(sessionId, fuenteId);
        fuentesContextos.put(fuenteId, ctx);
    }

    public UUID borrarFuentePorSession(String sessionId) {
        UUID fuenteId = sesionesFuentes.remove(sessionId);

        if (fuenteId != null) {
            fuentesContextos.remove(fuenteId);
        } else {
            System.out.println("Aviso: Se intentó borrar una sesión que no estaba registrada: " + sessionId);
        }

        return fuenteId;
    }

    public Boolean borrarFuente(UUID idFuente) {
        if (idFuente == null) return false;

        WsContext ctx = fuentesContextos.remove(idFuente);

        if (ctx != null) {
            sesionesFuentes.values().remove(idFuente);
            return true;
        }
        return false;
    }

    public UUID obtenerFuenteIdPorSession(String sessionId) {
        return sesionesFuentes.get(sessionId);
    }

    public ConcurrentMap<String, UUID> obtenerFuentesIDs() {
        return sesionesFuentes;
    }

    public ConcurrentMap<UUID, WsContext> obtenerFuentesCtxs() {
        return fuentesContextos;
    }

    public void agregarFuente(UUID idFuente, WsContext ctx) {
        fuentesContextos.put(idFuente, ctx);
    }
}