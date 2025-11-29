package agregador.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import agregador.dto.ModelosMensajesDTO.MensajeVacioPayload;
import agregador.dto.ModelosMensajesDTO.WsMessage;
import io.javalin.websocket.WsContext;
import agregador.repository.ConexionCargadorRepositorio;
import agregador.repository.FuenteRepositorio;
import agregador.domain.fuente.Fuente;
import agregador.domain.fuente.TipoDeFuente;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class HechosCargadorService {
    ConexionCargadorRepositorio conexionCargadorRepositorio;
    FuenteRepositorio fuenteRepositorio;
    ObjectMapper mapper = new ObjectMapper();

    public HechosCargadorService(FuenteRepositorio fuenteRepositorio, ConexionCargadorRepositorio conexionCargadorRepositorio) {
        this.conexionCargadorRepositorio = conexionCargadorRepositorio;
        this.fuenteRepositorio = fuenteRepositorio;
    }

    public void obtenerHechosNuevos(){
        ConcurrentMap<UUID, WsContext> fuentes = conexionCargadorRepositorio.obtenerFuentesCtxs();
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
        ConcurrentMap<UUID, WsContext> fuentes = conexionCargadorRepositorio.obtenerFuentesCtxs();
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
}
