package Agregador;

import Agregador.Normalizador.NormalizadorMock;
import Agregador.Persistencia.ColeccionRepositorio;
import Agregador.Persistencia.HechoRepositorio;
import Agregador.Persistencia.SolicitudEliminacionRepositorio;
import Agregador.Persistencia.SolicitudModificacionRepositorio;
import Agregador.Solicitudes.DetectorDeSpam;
import utils.DTO.*;
import Agregador.HechosYColecciones.Coleccion;
import Agregador.HechosYColecciones.Hecho;
import Agregador.Solicitudes.SolicitudDeEliminacion;
import Agregador.fuente.*;

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
    private SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;
    private SolicitudModificacionRepositorio solicitudModificacionRepositorio;
    private NormalizadorMock normalizador;
    ConexionCargador conexionCargador;

    public Agregador(HechoRepositorio hechoRepositorio, ColeccionRepositorio coleccionRepositorio, SolicitudEliminacionRepositorio solicitudEliminacionRepositorio,
                     SolicitudModificacionRepositorio solicitudModificacionRepositorio, NormalizadorMock normalizador, ConexionCargador conexionCargador){
        this.hechoRepositorio = hechoRepositorio;
        this.coleccionRepositorio = coleccionRepositorio;
        //this.fuentesDisponibles = fuentesDisponibles;
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
            SolicitudDeEliminacion nueva = new SolicitudDeEliminacion(dto);
            boolean yaExiste = solicitudEliminacionRepositorio.buscarPorId(nueva.getId()).isPresent();
            boolean esSpam = DetectorDeSpam.esSpam(dto.getJustificacion());

            if (!yaExiste && !esSpam) {
                solicitudEliminacionRepositorio.add(nueva);
            }

            if (esSpam) {
                nueva.rechazarSolicitud();
            }
        }
    }
        /*    SolicitudDeEliminacion nueva = new SolicitudDeEliminacion(solicitudDeEliminacionDTO);

            boolean yaExisteSoli = solicitudEliminacionRepositorio.buscarPorId(nueva.getId()).isPresent();

            boolean soliEsSpam = DetectorDeSpam.esSpam(solicitudDeEliminacionDTO.getJustificacion());

            if (!yaExisteSoli && !soliEsSpam) {
                solicitudEliminacionRepositorio.add(nueva);
            }

            if(soliEsSpam) {
                nueva.rechazarSolicitud();
            }
        }

     */


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