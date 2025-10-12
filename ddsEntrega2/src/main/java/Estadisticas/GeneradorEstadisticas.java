package Estadisticas;

import Agregador.HechosYColecciones.*;
import Agregador.PaqueteAgregador.DetectorDeSpam;
import Agregador.Solicitudes.SolicitudDeEliminacion;
import com.fasterxml.jackson.core.type.TypeReference;
import utils.ApiGetter;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class GeneradorEstadisticas {
    private String conexionAgregador;

    public GeneradorEstadisticas(String conexionAgregador){
        this.conexionAgregador = conexionAgregador;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            ApiGetter apiGetter = new ApiGetter();
            List<Hecho> hechos = apiGetter.getFromApi(
                    conexionAgregador + "/hechos", new TypeReference<List<Hecho>>() {
                    });
            List<SolicitudDeEliminacion> solicitudes = apiGetter.getFromApi(
                    conexionAgregador + "/solicitudesEliminacion",
                    new TypeReference<List<SolicitudDeEliminacion>>() {
                    });

            this.contarSolicitudesSpam(solicitudes);
            this.categoriaConMasHechos(hechos);

            /*
            HAY QUE VER DE PEDIR LA CATEGORIA
            this.horaConMasHechos(hechos, categoria);
            this.provinciaConMasHechos(coleccion);
            this.provinciaConMasHechos(hechos, categoria);
             */

        }, 0, 1, TimeUnit.DAYS);

    }


    public String provinciaConMasHechos(Coleccion coleccion){
        return this.valorMasFrecuente(coleccion.getHechos(),
                h -> h.getUbicacion().getDescripcion());
    }

    public String categoriaConMasHechos(List<Hecho> hechos){
        return this.valorMasFrecuente(hechos, Hecho :: getCategoria);
    }

    public String provinciaConMasHechos(String categoria, List<Hecho> hechos){
        return this.valorMasFrecuente(hechos.stream()
                .filter(h -> h.getCategoria().equals(categoria)).toList(),
                h -> h.getUbicacion().getDescripcion());
    }

    public <T> T valorMasFrecuente(List<Hecho> hechos, Function<Hecho, T> criterio){
        List<Contador<T>> contadores = new ArrayList<>();
        for(Hecho hecho : hechos){
            T valor = criterio.apply(hecho);
            Optional<Contador<T>> resultado = contadores.stream()
                    .filter(c -> c.getValor().equals(valor))
                    .findFirst();
            if(resultado.isPresent()){
                resultado.get().incrementar();
            } else{
                contadores.add(new Contador<>(valor));
            }
        }
        contadores.sort(Comparator.comparing((Contador<T> c) -> c.getContador()).reversed());
        return contadores.get(0).getValor();
    }

    public Date horaConMasHechos(List<Hecho> hechos, String categoria){
        return this.valorMasFrecuente(hechos.stream()
                        .filter(h -> h.getCategoria().equals(categoria)).toList(),
                Hecho :: getFechaDeAcontecimiento);
    }

    public int contarSolicitudesSpam(List<SolicitudDeEliminacion> solicitudes){
        int contador = 0;
        for(SolicitudDeEliminacion solicitud : solicitudes){
            if(DetectorDeSpam.esSpam(solicitud.getJustificacion())){
                contador++;
            }
        }
        return contador;
    }
}
