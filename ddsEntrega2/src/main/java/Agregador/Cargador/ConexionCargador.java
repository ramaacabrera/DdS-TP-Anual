package Agregador.Cargador;

import Agregador.Persistencia.FuenteRepositorio;
import Agregador.fuente.Fuente;
import Agregador.fuente.TipoDeFuente;
import CargadorDemo.DemoLoader;
import CargadorMetamapa.MetamapaLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import utils.ApiGetter;
import utils.DTO.*;

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
                mensaje = mapper.writeValueAsString(new SolicitudHechosMensajeDTO("obtenerHechosNuevos"));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            ctx.send(mensaje);
        });
    }

    /*
    public List<HechoDTO> obtenerHechosNuevos(){
        List<HechoDTO> listaHecho = new ArrayList<>();


        fuentes.forEach((id, ctx) -> {

            try {
                // La llamada a buscarHechos (que usa ApiGetter) debe estar envuelta
                List<HechoDTO> hechosTemporales = this.buscarHechos(fuente.getRuta());

                if (hechosTemporales != null) {
                    hechosTemporales.forEach((hecho) -> hecho.setFuente(fuente));
                    listaHecho.addAll(hechosTemporales);
                }
            } catch (RuntimeException e) {
                // Capturar la excepción lanzada por ApiGetter (Error de red, JSON, etc.)
                System.err.println("Saltando fuente " + fuente.getRuta() + " por error: " + e.getMessage());
                // No hacemos nada, solo pasamos a la siguiente fuente.
            }
        });

        System.out.println("Hechos obtenidos desde fuentes: " + listaHecho.size());
        return listaHecho;
    }
    */

    public List<HechoDTO> buscarHechos(String url){
        ApiGetter api = new ApiGetter();
        return api.getFromApi(url + "/hechos", new TypeReference<List<HechoDTO>>() {
        });
    }

    public List<SolicitudDeModificacionDTO> obtenerSolicitudes() {
        List<SolicitudDeModificacionDTO> todas = new ArrayList<>();
        /*for (Fuente fuente : fuentes) {
            if (fuente.getTipoDeFuente() == TipoDeFuente.DINAMICA) {
                todas.addAll(this.obtenerSolicitudesDeFuente(fuente));
            }
        }*/
        return todas;
    }

    private List<SolicitudDeModificacionDTO> obtenerSolicitudesDeFuente(Fuente fuente) {
        ApiGetter api = new ApiGetter();
        return api.getFromApi(
                fuente.getRuta() + "/solicitudesModificacion",
                new TypeReference<List<SolicitudDeModificacionDTO>>() {}
        );
    }


    public List<SolicitudDeEliminacionDTO> obtenerSolicitudesEliminacion() {
        List<SolicitudDeEliminacionDTO> todas = new ArrayList<>();
        /*for (Fuente fuente : fuentes) {
            if (fuente.getTipoDeFuente() == TipoDeFuente.DINAMICA) {
                todas.addAll(obtenerSolicitudesDeEliminacionDeFuente(fuente));
            }
        }*/
        return todas;
    }

    private List<SolicitudDeEliminacionDTO> obtenerSolicitudesDeEliminacionDeFuente(Fuente fuente) {
        ApiGetter api = new ApiGetter();
        return api.getFromApi(
                fuente.getRuta() + "/solicitudesEliminacion",
                new TypeReference<List<SolicitudDeEliminacionDTO>>() {}
        );
    }

/*
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

    public boolean borrarFuente(String idFuente) {
         /*for(Fuente fuente1 : fuentes) {
             if(Objects.equals(fuente1.getId().toString(), idFuente)) {
                 fuentes.remove(fuente1);
                 return true;
             }
         }*/
         return false;
    }
}
