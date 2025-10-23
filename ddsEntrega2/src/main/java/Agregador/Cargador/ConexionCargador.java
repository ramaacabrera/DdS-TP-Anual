package Agregador.Cargador;

import Agregador.Persistencia.FuenteRepositorio;
import Agregador.fuente.Fuente;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsContext;
import utils.ApiGetter;
import utils.DTO.*;
import utils.DTO.ModelosMensajesDTO.MensajeVacioPayload;
import utils.DTO.ModelosMensajesDTO.WsMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConexionCargador {


    private final ConcurrentMap<UUID, WsContext> fuentes =  new ConcurrentHashMap<>();
    FuenteRepositorio fuenteRepositorio;

    ObjectMapper mapper = new ObjectMapper();

    public ConexionCargador(FuenteRepositorio nuevaFuenteRepositorio) {
        fuenteRepositorio =  nuevaFuenteRepositorio;
    }
    public ConcurrentMap<UUID, WsContext> getFuentes() { return fuentes; }

    public void obtenerHechosNuevos(){
        fuentes.forEach((fuenteId, ctx) -> {
            String mensaje = null;
            try {
                mensaje = mapper.writeValueAsString(new WsMessage<MensajeVacioPayload>("obtenerHechos", null));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            ctx.send(mensaje);
        });
    }

    public void obtenerSolicitudes() {
        fuentes.forEach((fuenteId, ctx) -> {
            String mensaje = null;
            try{
                mensaje = mapper.writeValueAsString(new WsMessage<MensajeVacioPayload>("obtenerSolicitudesEliminacion", null));
                ctx.send(mensaje);
                mensaje = mapper.writeValueAsString(new WsMessage<MensajeVacioPayload>("obtenerSolicitudesModificacion", null));
                ctx.send(mensaje);
            }
            catch(JsonProcessingException e){
                throw new RuntimeException(e);
            }
        });

    }
/*
    private List<SolicitudDeModificacionDTO> obtenerSolicitudesDeFuente(Fuente fuente) {
        ApiGetter api = new ApiGetter();
        return api.getFromApi(
                fuente.getRuta() + "/solicitudesModificacion",
                new TypeReference<List<SolicitudDeModificacionDTO>>() {}
        );
    }


    public List<SolicitudDeEliminacionDTO> obtenerSolicitudesEliminacion() {
        List<SolicitudDeEliminacionDTO> todas = new ArrayList<>();
        for (Fuente fuente : fuentes) {
            if (fuente.getTipoDeFuente() == TipoDeFuente.DINAMICA) {
                todas.addAll(obtenerSolicitudesDeEliminacionDeFuente(fuente));
            }
        }
        return todas;
    }

    private List<SolicitudDeEliminacionDTO> obtenerSolicitudesDeEliminacionDeFuente(Fuente fuente) {
        ApiGetter api = new ApiGetter();
        return api.getFromApi(
                fuente.getRuta() + "/solicitudesEliminacion",
                new TypeReference<List<SolicitudDeEliminacionDTO>>() {}
        );
    }


    private <T> T getFromApi(String url, TypeReference<T> typeRef){
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), typeRef);
        } catch (Exception e) {
            System.err.println("Error al consultar API (" + url + "): " + e.getMessage());
            return null;
        }
    }
*/
    public void agregarFuente(UUID idFuente, WsContext ctx) {
        fuentes.put(idFuente, ctx);
    }

    public boolean borrarFuente(UUID idFuente) {
         fuentes.remove(idFuente);
         return false;
    }
}
