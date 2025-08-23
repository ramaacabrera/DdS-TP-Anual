package Agregador;

import Persistencia.ColeccionRepositorio;
import Persistencia.HechoRepositorio;
import Persistencia.SolicitudEliminacionRepositorio;
import Agregador.DTO.HechoDTO;
import Agregador.Criterios.Criterio;
import Agregador.DTO.SolicitudDeEliminacionDTO;
import Agregador.HechosYColecciones.Coleccion;
import Agregador.HechosYColecciones.Hecho;
import Agregador.Solicitudes.SolicitudDeEliminacion;
import Agregador.fuente.*;
import FuenteDinamica.FuenteDinamica;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.time.LocalDateTime;

public class Agregador {

    private HechoRepositorio hechoRepositorio;
    private ColeccionRepositorio coleccionRepositorio;
    private List<Fuente> fuentesDisponibles;
    private SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;

    public Agregador(HechoRepositorio hechoRepositorio, ColeccionRepositorio coleccionRepositorio, SolicitudEliminacionRepositorio solicitudEliminacionRepositorio, List<Fuente> fuentesDisponibles) {
        this.hechoRepositorio = hechoRepositorio;
        this.coleccionRepositorio = coleccionRepositorio;
        this.fuentesDisponibles = fuentesDisponibles;
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            this.actualizarSolicitudesYHechosDesdeFuentes();
        }, 1, 1, TimeUnit.HOURS);

        long delayInicial = calcularDelayHastaHora(2);  // 2 AM
        scheduler.scheduleAtFixedRate(() -> {
            this.ejecutarAlgoritmoDeConsenso();
        }, delayInicial, 24, TimeUnit.HOURS);

    }
    /*
    Constructor (lo hicimos para inicializar) que cuando se crea el objeto Agregador,
    arranca un scheduler que cada 1 hora ejecuta el metodo actualizarHechosDesdeFuentes().
    */


    public void actualizarSolicitudesYHechosDesdeFuentes() {
        // Traemos hechos nuevos de TODAS las fuentes disponibles
        System.out.printf("Fuentes disponibles: %d \n", fuentesDisponibles.size());
        for (Fuente fuente : fuentesDisponibles) {
            System.out.printf("Fuente a buscar: %s\n", fuente);
            List<Hecho> nuevosHechos = obtenerHechosExterno(fuente, new ArrayList<>());
            if(fuente.getTipoDeFuente() == TipoDeFuente.DINAMICA && fuente instanceof FuenteDinamica){
                agregarSolicitudes((FuenteDinamica) fuente);
            }
            System.out.printf("Se cargan %d nuevos hechos desde %s", nuevosHechos.size(), fuente + "\n");
            for (Hecho hecho : nuevosHechos) {
                int nuevo = 0;
                // Por cada colección, vemos si el hecho cumple criterios
                for (Coleccion coleccion : coleccionRepositorio.obtenerTodas()) {
                    if (coleccion.cumpleCriterio(hecho)) {
                        boolean esNuevo = coleccion.agregarHecho(hecho);
                        if(esNuevo){ nuevo++;}
                    }
                }
                if (nuevo>0) {
                    hechoRepositorio.guardar(hecho);
                } else {
                    Hecho hechoExistente = buscarHechoSimilar(hecho);
                    if (hechoExistente != null) {
                        hechoRepositorio.actualizar(hechoExistente);
                    }

                }
            }
            System.out.println("Hechos cargados desde " + fuente+"\n");
        }
        System.out.printf("Hechos en repo: %d ", hechoRepositorio.buscarHechos(new ArrayList<>()).size());
    }


    private Hecho buscarHechoSimilar(Hecho hechoNuevo) {
        for (Hecho h : hechoRepositorio.buscarHechos(null)) {
            if (h.tieneMismosAtributosQue(hechoNuevo)) {
                return h;
            }
        }
        return null;
    }

    public void buscarHechosIniciales(){
        for (Fuente fuente : fuentesDisponibles){
            List<Hecho> nuevosHechos = obtenerHechosExterno(fuente, new ArrayList<>());
            nuevosHechos.forEach(hecho -> hechoRepositorio.guardar(hecho));
            System.out.printf("%d hechos cargados desde %s", nuevosHechos.size(), fuente + "\n");
        }
    }

    private Hecho buscarHechoPorTitulo(String titulo) {
        for (Hecho h : hechoRepositorio.buscarHechos(null)) {
            if (h.getTitulo().equalsIgnoreCase(titulo)) {
                return h;
            }
        }
        return null;
    }

    public List<Hecho> obtenerHechosExterno(Fuente fuente, List<Criterio> criterios) {
        List<Hecho> hechos = new ArrayList<>();
        for (HechoDTO hechoDTO : fuente.obtenerHechos(criterios)){
            Hecho hecho = new Hecho(hechoDTO);
            hechos.add(hecho);
        }
        return hechos;
    }

    public void agregarSolicitudes(FuenteDinamica fuente) {
        List<SolicitudDeEliminacion> solicitudesDeEliminacion = new ArrayList<>();
        for (SolicitudDeEliminacionDTO solicitudDeEliminacionDTO : fuente.obtenerSolicitudDeEliminacion()){
            SolicitudDeEliminacion nueva = new SolicitudDeEliminacion(solicitudDeEliminacionDTO);
            boolean yaExisteSoli = solicitudEliminacionRepositorio.buscarPorId(nueva.getId()).isPresent();
            if(!yaExisteSoli) {
                solicitudEliminacionRepositorio.add(nueva);
            }
        }
    }

    public void ejecutarAlgoritmoDeConsenso() {
        for (Coleccion coleccion : coleccionRepositorio.obtenerTodas()) {
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

    //idea de normalizacion:
    Normalizador normalizador = new Normalizador();
for (Fuente fuente : fuentesDisponibles){
for (HechoDTO dto : fuente.obtenerHechos(null)) {
        Hecho hecho = new Hecho(dto);
        Hecho hechoNormalizado = normalizador.normalizar(hecho);
        hechoRepositorio.guardar(hechoNormalizado);
    }
}


}

