package Estadisticas;

import Agregador.HechosYColecciones.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Date;

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
                    .filter(contador -> contador
                            .getValor().equals(hecho.getUbicacion().getDescripcion()))
                    .findFirst();
            if(resultado.isPresent()){
                resultado.get().incrementar();
            } else{
                Contador contador = new Contador(hecho.getUbicacion().getDescripcion());
                provincias.add(contador);
            }
        }
        return "";
    }

    public String categoriaConMasHechos(){
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
