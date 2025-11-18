package service;

import domain.DTO.ModelosMensajesDTO.IdCargadorPayload;
import io.javalin.websocket.WsConnectContext;
import org.jetbrains.annotations.NotNull;
import repository.ConexionCargadorRepositorio;
import utils.Dominio.fuente.Fuente;
import repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsContext;
import utils.DTO.ModelosMensajesDTO.WsMessage;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class ConexionCargadorService {

    ConexionCargadorRepositorio conexionCargadorRepositorio;
    FuenteRepositorio fuenteRepositorio;
    ObjectMapper mapper = new ObjectMapper();

    public ConexionCargadorService(FuenteRepositorio fuenteRepositorio, ConexionCargadorRepositorio conexionCargadorRepositorio) {
        this.fuenteRepositorio = fuenteRepositorio;
        this.conexionCargadorRepositorio = conexionCargadorRepositorio;
    }

    public void registrarFuentePorSession(String sessionId, UUID fuenteId, WsContext ctx) {
        conexionCargadorRepositorio.registrarFuentePorSession(sessionId, fuenteId, ctx);
        System.out.println("🔗 Fuente registrada - Session: " + sessionId + " -> Fuente: " + fuenteId);
    }

    public boolean borrarFuentePorSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            System.out.println("⚠️ Session ID nulo o vacío");
            return false;
        }

        UUID fuenteId = conexionCargadorRepositorio.borrarFuentePorSession(sessionId);
        if (fuenteId != null) {
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

        Boolean fuenteBorrada = conexionCargadorRepositorio.borrarFuente(idFuente);
        if(fuenteBorrada) {
            System.out.println("🔌 Fuente removida por ID: " + idFuente);
            return true;
        }
        else{
            System.out.println("Error al remover fuente por ID: " + idFuente);
            return false;
        }

    }

    public UUID obtenerFuenteIdPorSession(String sessionId) {
        return conexionCargadorRepositorio.obtenerFuenteIdPorSession(sessionId);
    }

    public void mostrarSesionesActivas() {
        System.out.println("=== SESIONES ACTIVAS ===");
        ConcurrentMap<String, UUID> fuentes = conexionCargadorRepositorio.obtenerFuentesIDs();
        fuentes.forEach((sessionId, fuenteId) -> {
            System.out.println("Session: " + sessionId + " -> Fuente: " + fuenteId);
        });
        System.out.println("Total: " + fuentes.size() + " sesiones activas");
    }

    public void agregarFuente(UUID idFuente, WsContext ctx) {
        conexionCargadorRepositorio.agregarFuente(idFuente, ctx);
        System.out.println("⚠️ Usando agregarFuente sin sessionId. Considera usar registrarFuentePorSession");
    }

    public String nuevaConexion(String sessionId, Fuente nuevo, @NotNull WsConnectContext ctx) {

        // ✅ Buscar o guardar fuente
        Fuente fuentePersistida = fuenteRepositorio.buscarPorRuta(nuevo.getDescriptor());
        if (fuentePersistida == null) {
            System.out.println("➕ Guardando nueva fuente: " + nuevo.getDescriptor());
            fuentePersistida = fuenteRepositorio.guardar(nuevo);
        } else {
            System.out.println("🔍 Fuente existente encontrada: " + fuentePersistida.getId());
        }

        // ✅ Registrar fuente con sessionId (usando el nuevo método)
        this.registrarFuentePorSession(sessionId, fuentePersistida.getId(), ctx);

        // ✅ Enviar ID al cliente
        WsMessage<IdCargadorPayload> mensaje = new WsMessage<>("idCargador", new IdCargadorPayload(fuentePersistida.getId()));

        try {
            String message = mapper.writeValueAsString(mensaje);
            System.out.println("🎯 Conexión establecida exitosamente - " +
                    "Fuente: " + fuentePersistida.getId() + " - " +
                    "Session: " + sessionId);
            return (message);
        }
        catch (Exception e){
            System.out.println("Error al guardar fuente: " + nuevo.getDescriptor());
            return null;
        }
    }
}