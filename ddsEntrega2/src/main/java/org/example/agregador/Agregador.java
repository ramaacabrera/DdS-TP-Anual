package org.example.agregador;

import org.example.fuente.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.time.LocalDateTime;

public class Agregador {

    private List<Coleccion> colecciones = new ArrayList<>();
    private HechoRepositorio hechoRepositorio;

    public Agregador() {
        this.hechoRepositorio = hechoRepositorio;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            this.actualizarHechosDesdeFuentes();
        }, 0, 1, TimeUnit.HOURS);

        long delayInicial = calcularDelayHastaHora(2);  // 2 AM

        scheduler.scheduleAtFixedRate(() -> {
            this.ejecutarAlgoritmoDeConsenso();
        }, delayInicial, 24, TimeUnit.HOURS);
    }
    //Constructor (lo hicimos para inicializar) que cuando se crea el objeto Agregador,
    // arranca un scheduler que cada 1 hora ejecuta el método actualizarHechosDesdeFuentes().

    public void actualizarHechosDesdeFuentes() {
        for (Coleccion coleccion : colecciones) {
            List<Fuente> fuentes = coleccion.getFuente();

            for (Fuente fuente : fuentes) {
                List<Hecho> nuevosHechos = obtenerHechosExterno(fuente);
                for (Hecho hecho : nuevosHechos) {
                    if (coleccion.cumpleCriterio(hecho)) {
                        boolean esNuevo = coleccion.agregarHecho(hecho);
                        if (esNuevo) {
                            hechoRepositorio.guardar(hecho);
                        } else {
                            Hecho hechoExistente = buscarHechoPorTitulo(coleccion, hecho.getTitulo());
                            if (hechoExistente != null) {
                                hechoRepositorio.actualizar(hechoExistente);
                            }
                        }
                    }
                }
            }
        }
    }

    private Hecho buscarHechoPorTitulo(Coleccion coleccion, String titulo) {
        for (Hecho h : coleccion.getHechos()) {
            if (h.getTitulo().equalsIgnoreCase(titulo)) {
                return h;
            }
        }
        return null;
    }

    public List<Hecho> obtenerHechosExterno(Fuente fuente) {
        List<Hecho> hechos = new ArrayList();
        for (HechoDTO hechoDTO : fuente.obtenerHechos()){
            Hecho hecho = new Hecho(hechoDTO);
            hechos.add(hecho);
        }
        return hechos;
    }

    public void ejecutarAlgoritmoDeConsenso() {
        for (Coleccion coleccion : colecciones) {
            coleccion.ejecutarAlgoritmoDeConsenso();
        }
    }

    private long calcularDelayHastaHora(int horaObjetivo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime proximaEjecucion = ahora.withHour(horaObjetivo).withMinute(0).withSecond(0).withNano(0);

        if (ahora.compareTo(proximaEjecucion) >= 0) {
            // Si ya pasó la hora objetivo de hoy, ponemos para mañana
            proximaEjecucion = proximaEjecucion.plusDays(1);
        }

        Duration duration = Duration.between(ahora, proximaEjecucion);
        long delayEnHoras = duration.toHours();

        return delayEnHoras;
    }

}


// revisar como se actualizan los repos de hechos