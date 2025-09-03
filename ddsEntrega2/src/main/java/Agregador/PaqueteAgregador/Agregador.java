package Agregador.PaqueteAgregador;

import Agregador.PaqueteNormalizador.MockNormalizador;
import Agregador.Persistencia.ColeccionRepositorio;
import Agregador.Persistencia.HechoRepositorio;
import Agregador.Persistencia.SolicitudEliminacionRepositorio;
import Agregador.Persistencia.SolicitudModificacionRepositorio;
import utils.DTO.*;
import Agregador.HechosYColecciones.Coleccion;
import Agregador.HechosYColecciones.Hecho;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.time.LocalDateTime;

public class Agregador {

    private final HechoRepositorio hechoRepositorio;
    private final ColeccionRepositorio coleccionRepositorio;
    private final SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;
    private final SolicitudModificacionRepositorio solicitudModificacionRepositorio;
    private final MockNormalizador normalizador;
    private final DetectorDeSpam detectorDeSpam =  new DetectorDeSpam();
    private final ConexionCargador conexionCargador;

    public Agregador(HechoRepositorio hechoRepositorio, ColeccionRepositorio coleccionRepositorio, SolicitudEliminacionRepositorio solicitudEliminacionRepositorio,
                     SolicitudModificacionRepositorio solicitudModificacionRepositorio, MockNormalizador normalizador, ConexionCargador conexionCargador){
        this.hechoRepositorio = hechoRepositorio;
        this.coleccionRepositorio = coleccionRepositorio;
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;
        this.normalizador = normalizador;
        this.conexionCargador = conexionCargador;
        this.solicitudModificacionRepositorio = solicitudModificacionRepositorio;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            this.actualizarHechosDesdeFuentes();
            this.agregarSolicitudes();
        }, 0, 1, TimeUnit.HOURS);

        long delayInicial = calcularDelayHastaHora(2);  // 2 AM
        scheduler.scheduleAtFixedRate(() -> {
            this.ejecutarAlgoritmoDeConsenso();
        }, delayInicial, 24, TimeUnit.HOURS);


    }
    /*
    Constructor (lo hicimos para inicializar) que cuando se crea el objeto Agregador,
    arranca un scheduler que cada 1 hora ejecuta el metodo actualizarHechosDesdeFuentes().
    */

    private Hecho normalizarHecho(HechoDTO hecho){
        return normalizador.normalizar(new Hecho(hecho));
    }

    public void actualizarHechosDesdeFuentes() {
        List<HechoDTO> hechos = conexionCargador.obtenerHechosNuevos();

        hechos.stream()
                .map(this::normalizarHecho).forEach(hechoNormalizado -> {
                    Hecho existente = buscarHechoSimilar(hechoNormalizado);

                    if (existente == null) {
                        // si no existe lo guardo como nuevo
                        hechoRepositorio.guardar(hechoNormalizado);
                        System.out.println("Nuevo hecho agregado: " + hechoNormalizado.getTitulo());
                    } else {
                        // si ya existe lo actualizo
                        hechoRepositorio.actualizar(hechoNormalizado);
                        System.out.println("Hecho actualizado: " + hechoNormalizado.getTitulo());
                    }
                });
    }

    private Hecho buscarHechoSimilar(Hecho hechoNuevo) {
        for (Hecho h : hechoRepositorio.buscarHechos(null)) {
            if (h.tieneMismosAtributosQue(hechoNuevo)) {
                return h;
            }
        }
        return null;
    }

    public void agregarSolicitudes() {
        List<SolicitudDeModificacionDTO> solicitudesDeModificacion = conexionCargador.obtenerSolicitudes();
        List<SolicitudDeEliminacionDTO>  solicitudesDeEliminacion = conexionCargador.obtenerSolicitudesEliminacion();


        for (SolicitudDeEliminacionDTO dto : solicitudesDeEliminacion) {
            //SolicitudDeEliminacion nueva = new SolicitudDeEliminacion(dto);
            //boolean yaExiste = solicitudEliminacionRepositorio.yaExiste(dto);
            boolean esSpam = DetectorDeSpam.esSpam(dto.getJustificacion());

            if (!esSpam){ // !yaExiste &&) {
                solicitudEliminacionRepositorio.agregarSolicitudEliminacion(dto);
            }
            // un posible else luego para las estadisticas

        }
        for (SolicitudDeModificacionDTO dto : solicitudesDeModificacion) {
            //boolean yaExiste = solicitudModificacionRepositorio.yaExiste(dto);

            //if (!yaExiste) {
                solicitudModificacionRepositorio.agregarSolicitudDeModificacion(dto);
            //}
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
}