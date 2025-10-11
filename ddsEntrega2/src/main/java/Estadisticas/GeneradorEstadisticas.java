package Estadisticas;

import Agregador.HechosYColecciones.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GeneradorEstadisticas {
    public GeneradorEstadisticas(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            //}, 0, 1, TimeUnit.HOURS);
        }, 0, 30, TimeUnit.SECONDS);

    }

    public String provinciaConMasHechos(Coleccion coleccion){
        List<Contador> provincias = new ArrayList<>();
        for(Hecho hecho : coleccion.getHechos()){
            Optional<Contador> resultado = provincias.stream()
                    .filter(contador -> contador.getValor()
                            .equals(hecho.getUbicacion().getDescripcion()))
                    .findFirst();
            if(resultado.isPresent()){
                resultado.get().incrementar();
            } else{
                Contador contador = new Contador(hecho.getUbicacion().getDescripcion());
                provincias.add(contador);
            }
        }
        provincias.sort(Comparator.comparing(Contador::getContador).reversed());
        return provincias.stream().findFirst().get().getValor();
    }

    public String categoriaConMasHechos(List<Hecho> hechos){

        return "";
    }

    public String provinciaConMasHechos(String categoria){
        return "";
    }

    public Date horaConMasHechos(){
        return null;
    }

    public int contarSolicitudesSpam(){
        return 0;
    }
}
