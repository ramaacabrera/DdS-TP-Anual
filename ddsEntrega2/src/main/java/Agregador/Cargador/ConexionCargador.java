package Agregador.Cargador;

import Agregador.fuente.Fuente;
import Agregador.fuente.TipoDeFuente;
import CargadorDemo.DemoLoader;
import CargadorMetamapa.MetamapaLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import utils.ApiGetter;
import utils.DTO.FuenteDTO;
import utils.DTO.HechoDTO;
import utils.DTO.SolicitudDeModificacionDTO;
import utils.DTO.SolicitudDeEliminacionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConexionCargador {
    private final MetamapaLoader metamapaLoader;
    private final DemoLoader demoLoader;

    private final List<Fuente> fuentes =  new ArrayList<>();
    private int idIncremental = 1;

    public ConexionCargador(MetamapaLoader metaLoader, DemoLoader demoLoader) {
        this.metamapaLoader = metaLoader;
        this.demoLoader = demoLoader;
    }
    public List<Fuente> getFuentes() { return fuentes; }

    public List<HechoDTO> obtenerHechosNuevos(){
        List<HechoDTO> listaHecho = new ArrayList<>();

        listaHecho.addAll(this.metamapaLoader.obtenerHechos());
        listaHecho.addAll(this.demoLoader.obtenerHechos());

        fuentes.forEach(fuente -> {

            if (fuente.getTipoDeFuente() != TipoDeFuente.METAMAPA &&
                    fuente.getTipoDeFuente() != TipoDeFuente.DEMO) {
                    List<HechoDTO> hechosTemporales;
                    hechosTemporales = this.buscarHechos(fuente.getRuta());
                    hechosTemporales.forEach((hecho) -> {
                        hecho.setFuente(fuente);
                    });
                    listaHecho.addAll(hechosTemporales);
            }
        });

        System.out.println("Hechos obtenidos desde fuentes: " + listaHecho.size());
        return listaHecho;
    }


    public List<HechoDTO> buscarHechos(String url){
        ApiGetter api = new ApiGetter();
        return api.getFromApi(url + "/hechos", new TypeReference<List<HechoDTO>>() {
        });
    }

    public List<SolicitudDeModificacionDTO> obtenerSolicitudes() {
        List<SolicitudDeModificacionDTO> todas = new ArrayList<>();
        for (Fuente fuente : fuentes) {
            if (fuente.getTipoDeFuente() == TipoDeFuente.DINAMICA) {
                todas.addAll(this.obtenerSolicitudesDeFuente(fuente));
            }
        }
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
    public boolean agregarFuente(FuenteDTO fuente) {
        for(Fuente fuente1 : fuentes) {
            if(fuente1.getRuta().equals(fuente.getRuta())) {
                return false;
            }
        }
        fuentes.add(new Fuente(fuente,idIncremental));
        idIncremental++;
        return true;
    }

    public boolean borrarFuente(String idFuente) {
         for(Fuente fuente1 : fuentes) {
             if(Objects.equals(fuente1.getId().toString(), idFuente)) {
                 fuentes.remove(fuente1);
                 return true;
             }
         }
         return false;
    }
}
